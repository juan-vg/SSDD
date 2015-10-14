/*
 * AUTORES: Juan Vela Garcia / Marta Frias Zapater
 * NIA: 643821 / 535621
 * FICHERO: ServidorSelector.java
 * TIEMPO: 20 horas
 * DESCRIPCION: Servidor web (HTTP) usando un Selector.
 */

package ssdd.p1.servidor;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
     * Version del servidor HTTP utilizando la clase Selector
     */
    public static void iniciar(int puerto) {

        try {
            ServerSocketChannel svrSockCh = ServerSocketChannel.open();
            svrSockCh.configureBlocking(false);
            svrSockCh.socket().bind(new InetSocketAddress(puerto));

            Selector selector = Selector.open();
            svrSockCh.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                int numCh = selector.select();

                if (numCh > 0) {

                    // Hay canales disponibles
                    Set<SelectionKey> selKeys = selector.selectedKeys();
                    Iterator<SelectionKey> keyIt = selKeys.iterator();

                    while (keyIt.hasNext()) {
                        SelectionKey key = keyIt.next();

                        if (key.isAcceptable()) {

                            // ServerSocketChannel acepta conexion
                            acepta(svrSockCh, selector);
                        } else if (key.isReadable()) {

                            // canal preparado para leer
                            lee(key, selector);
                        } else if (key.isWritable()) {

                            // canal preparado para escribir
                            escribe(key);
                        }
                        keyIt.remove();
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("ERROR: " + e.getMessage());
        }
    }

    /**
     * Metodo auxiliar de serverSelector que acepta peticiones HTTP
     * 
     * @param svrSockCh Canal asociado al servidor
     * @param selector Objeto Selector asociado a la peticion
     */
    private static void acepta(ServerSocketChannel svrSockCh,
            Selector selector) {

        SocketChannel cSockCh;
        Utiles util = new Utiles();
        NonBlockingHTTPParser analizador = new NonBlockingHTTPParser();

        try {
            cSockCh = svrSockCh.accept();

            // configura el socket como NO bloqueante
            cSockCh.configureBlocking(false);

            // inicia fase de lectura
            SelectionKey selKey = cSockCh.register(selector,
                    SelectionKey.OP_READ);

            util.setAnalizador(analizador);
            selKey.attach(util);
        } catch (IOException e) {
            System.err.println(
                    "ERROR: Fallo aceptando nueva conexion " + e.getMessage());
        }
    }

    /**
     * Metodo auxiliar del metodo serverSelector que lee y procesa peticiones
     * HTTP cuando el canal esta preparado para leer.
     * 
     * @param key Objeto SelectionKey asociado a la conexion
     * @param selector Objeto Selector asociado a la peticion
     */
    private static void lee(SelectionKey key, Selector selector) {

        try {
            SocketChannel cSockCh = (SocketChannel) key.channel();

            if (cSockCh != null) {

                Utiles util = (Utiles) key.attachment();
                NonBlockingHTTPParser analizador = util.getAnalizador();
                ByteBuffer buf;
                if (util.getBuffer() != null) {
                    buf = util.getBuffer();
                } else {
                    buf = ByteBuffer.allocate(1024);
                    util.setBuffer(buf);
                }
                buf.clear();
                cSockCh.read(buf);
                buf.flip();

                // analizar peticion
                analizador.parseRequest(buf);

                // PETICION FALLIDA (400 BAD REQUEST)
                if (analizador.failed()) {
                    util.setRespuesta(Utiles.generaRespuesta(400, null));

                    SelectionKey selKey = cSockCh.register(selector,
                            SelectionKey.OP_WRITE);
                    selKey.attach(util);
                }

                // PETICION COMPLETA
                else if (analizador.isComplete()) {

                    // METODO GET
                    if (analizador.getMethod().equals("GET")) {
                        util.setRespuesta(httpGet(analizador, util));
                    }

                    // METODO POST
                    else if (analizador.getMethod().equals("POST")) {
                        util.setRespuesta(httpPost(analizador));
                    }

                    // METODO NO IMPLEMENTADO (501 NOT IMPLEMENTED)
                    else {
                        util.setRespuesta(Utiles.generaRespuesta(501, null));
                    }

                    // inicia fase de escritura
                    SelectionKey selKey = cSockCh.register(selector,
                            SelectionKey.OP_WRITE);
                    selKey.attach(util);
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
     * @param key Objeto SelectionKey asociado a la conexion
     */
    private static void escribe(SelectionKey key) {

        SocketChannel cSockCh = (SocketChannel) key.channel();

        if (cSockCh != null) {
            Utiles util = (Utiles) key.attachment();

            if (util.isCuerpo()) {
                ByteBuffer buf = util.getBuffer();
                buf.clear();
                try {
                    // escribe en el bufer
                    buf.put(util.getCuerpo(buf.capacity()).getBytes("UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    System.err.println("ERROR: Fallo al escribir en el buffer "
                            + e.getMessage());
                }
                
                // cambia a modo lectura
                buf.flip();
                try {
                    cSockCh.write(buf);
                } catch (IOException e) {
                    System.err.println("ERROR: " + e.getMessage());
                }
            } else {
                key.cancel();
                try {
                    cSockCh.close();
                } catch (IOException e) {
                    System.err.println("ERROR: Fallo cerrando conexion. "
                            + e.getMessage());
                }
            }
        }
    }
}
