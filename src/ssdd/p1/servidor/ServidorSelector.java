/*
 * AUTORES: Juan Vela Garcia / Marta Frias Zapater
 * NIA: 643821 / 535621
 * FICHERO: ServidorSelector.java
 * TIEMPO: 20 horas
 * DESCRIPCION: Servidor web (HTTP) usando un Selector.
 */

package ssdd.p1.servidor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import ssdd.p1.herramientas.NonBlockingHTTPParser;
import ssdd.p1.herramientas.Utiles;

/**
 * Servidor HTTP sencillo utilizando la clase Selector (secuencial)
 * 
 * @author Juan Vela, Marta Frias
 *
 */
public class ServidorSelector extends ServidorHTTP {

    /**
     * Metodo que permite iniciar un servidor HTTP cuyo funcionamiento se basa
     * en un selector
     * 
     * @param puerto : Numero de puerto en el que el servidor debe permanecer a
     *            la escucha de nuevas conexiones
     * 
     */
    public static void iniciar(int puerto) {

        try {

            boolean finalizar = false;

            // crear el socket servidor en el puerto [puerto]
            // y configurarlo como NO BLOQUEANTE
            ServerSocketChannel servidor = ServerSocketChannel.open();
            servidor.configureBlocking(false);
            servidor.socket().bind(new InetSocketAddress(puerto));

            // crear un selector y registrar el socket del servidor en dicho
            // selector con la operacion aceptar (clientes)
            Selector selector = Selector.open();
            servidor.register(selector, SelectionKey.OP_ACCEPT);

            while (!finalizar) {

                // se bloquea en espera de nuevas operaciones
                int numOperacionesDisponibles = selector.select();

                // si se reciben operaciones
                // (puede desbloquearse sin recibir operaciones)
                if (numOperacionesDisponibles > 0) {

                    // obtener las operaciones y un iterador para recorrerlas
                    Set<SelectionKey> operaciones = selector.selectedKeys();
                    Iterator<SelectionKey> iteradorOps = operaciones.iterator();

                    // mientras queden operaciones por procesar
                    while (iteradorOps.hasNext()) {

                        SelectionKey operacion = iteradorOps.next();

                        // si la operacion consiste en aceptar un nuevo cliente
                        if (operacion.isAcceptable()) {

                            acepta(servidor, selector);
                        }

                        // si la operacion consiste en recibir informacion de un
                        // cliente
                        else if (operacion.isReadable()) {

                            lee(operacion, selector);
                        }

                        // si la operacion consiste en enviar informacion a un
                        // cliente
                        else if (operacion.isWritable()) {

                            escribe(operacion);
                        }

                        // eliminar la operacion que se acaba de atender
                        iteradorOps.remove();
                    }
                }
            }

            // cerrar el servidor
            selector.close();
            servidor.close();

        } catch (IOException e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Metodo auxiliar que acepta nuevos clientes
     * 
     * @param servidor : Canal asociado al servidor
     * @param selector : Selector del servidor
     * 
     */
    private static void acepta(ServerSocketChannel servidor,
            Selector selector) {

        SocketChannel cliente;
        Utiles util = new Utiles();
        NonBlockingHTTPParser analizador = new NonBlockingHTTPParser();

        try {

            // aceptar nuevo cliente
            cliente = servidor.accept();

            // configurar el socket cliente como NO BLOQUEANTE
            cliente.configureBlocking(false);

            // iniciar fase de lectura
            // (registrar operacion de leer en el selector)
            SelectionKey operacion = cliente.register(selector,
                    SelectionKey.OP_READ);

            // adjuntar los datos necesarios a la operacion
            util.setAnalizador(analizador);
            operacion.attach(util);

        } catch (IOException e) {
            System.err.println("ERROR: Fallo aceptando nueva conexion");
            e.printStackTrace();
        }
    }

    /**
     * Metodo auxiliar que lee y procesa peticiones HTTP cuando el canal esta
     * preparado para leer.
     * 
     * @param operacion : Operacion a realizar
     * @param selector : Selector del servidor
     * 
     */
    private static void lee(SelectionKey operacion, Selector selector) {

        try {

            // obtener el cliente a partir de la operacion
            SocketChannel cliente = (SocketChannel) operacion.channel();

            // si el cliente no tiene errores
            if (cliente != null) {

                // obtener los datos adjuntos
                Utiles util = (Utiles) operacion.attachment();
                NonBlockingHTTPParser analizador = util.getAnalizador();
                ByteBuffer bufer;

                // si ya habia un bufer creado
                if (util.getBuffer() != null) {

                    // obtener el bufer y reutilizarlo
                    bufer = util.getBuffer();
                }

                // si no habia ningun bufer creado
                else {

                    // crear un nuevo buffer
                    bufer = ByteBuffer.allocate(1024);
                    util.setBuffer(bufer);
                }

                // limpiar y reiniciar el bufer
                bufer.clear();

                // recibir datos del cliente y escribirlos en el bufer
                cliente.read(bufer);

                // cambiar el bufer a modo lectura
                bufer.flip();

                // analizar peticion
                analizador.parseRequest(bufer);

                // PETICION FALLIDA (400 BAD REQUEST)
                if (analizador.failed()) {
                    util.setRespuesta(Utiles.generaRespuesta(400));

                    // iniciar fase de escritura
                    // (registrar operacion de escribir en el selector)
                    SelectionKey nuevaOperacion = cliente.register(selector,
                            SelectionKey.OP_WRITE);

                    // adjuntar los datos necesarios a la nueva operacion
                    nuevaOperacion.attach(util);
                }

                // PETICION COMPLETA
                else if (analizador.isComplete()) {

                    // METODO GET
                    if (analizador.getMethod().equals("GET")) {
                        util.setRespuesta(httpGet(analizador));
                    }

                    // METODO POST
                    else if (analizador.getMethod().equals("POST")) {
                        util.setRespuesta(httpPost(analizador));
                    }

                    // METODO NO IMPLEMENTADO (501 NOT IMPLEMENTED)
                    else {
                        util.setRespuesta(Utiles.generaRespuesta(501));
                    }

                    // iniciar fase de escritura
                    // (registrar operacion de escribir en el selector)
                    SelectionKey nuevaOperacion = cliente.register(selector,
                            SelectionKey.OP_WRITE);

                    // adjuntar los datos necesarios a la nueva operacion
                    nuevaOperacion.attach(util);
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
        }
    }

    /**
     * Metodo auxiliar del metodo serverSelector que escribe peticiones HTTP
     * cuando el canal esta preparado para escribir.
     * 
     * @param operacion : Operacion a realizar
     * 
     */
    private static void escribe(SelectionKey operacion) {

        // obtener el cliente a partir de la operacion
        SocketChannel cliente = (SocketChannel) operacion.channel();

        // sie el cliente no tiene errores
        if (cliente != null) {

            // obtener los datos adjuntos
            Utiles util = (Utiles) operacion.attachment();

            // si hay informacion que enviar al cliente
            if (util.quedaCuerpo()) {

                // obtener el bufer y reutilizarlo
                ByteBuffer bufer = util.getBuffer();

                // limpiar y reiniciar el bufer
                bufer.clear();

                // obtener una porcion de respuesta
                String cuerpo = util.getCuerpo(bufer.capacity());

                // escribir en el bufer
                bufer.put(cuerpo.getBytes());

                // cambia el bufer a modo lectura
                bufer.flip();

                try {

                    // enviar al cliente los datos leidos del bufer
                    cliente.write(bufer);

                } catch (IOException e) {
                    System.err.println("ERROR: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            // si no queda nada por devolver, y por lo tanto se ha terminado de
            // atender la operacion
            else {

                // dar por terminada la operacion
                operacion.cancel();

                // cerrar conexion con el cliente
                try {
                    cliente.close();
                } catch (IOException e) {
                    System.err.println("ERROR: Fallo cerrando conexion. "
                            + e.getMessage());
                }
            }
        }
    }
}
