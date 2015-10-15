/*
 * AUTORES: Juan Vela Garcia / Marta Frias Zapater
 * NIA: 643821 / 535621
 * FICHERO: ServidorThreadRun.java
 * TIEMPO: 1 hora
 * DESCRIPCION: Gestor de peticiones web (HTTP) en hilos separados.
 */

package ssdd.p1.servidor;

import java.io.PrintWriter;
import java.net.Socket;

import ssdd.p1.herramientas.BlockingHTTPParser;
import ssdd.p1.herramientas.Utiles;

/**
 * Gestor de hilos que atienden clientes HTTP
 * 
 * @author Juan Vela, Marta Frias
 *
 */
public class ServidorHilosEjecutable extends ServidorHTTP implements Runnable {

    /** Atributo que hace referencia al socket asociado al cliente */
    private Socket cliente;

    /**
     * Metodo constructor de la clase ServidorThreadHTTP
     * 
     */
    public ServidorHilosEjecutable(Socket c) {
        cliente = c;
    }

    /**
     * Metodo que se ejecuta en el hilo. De forma secuencial analiza la
     * peticion, la sirve (responde) y termina.
     * 
     */
    @Override
    public void run() {
        try {
            PrintWriter salidaCliente = new PrintWriter(
                    cliente.getOutputStream(), true);

            BlockingHTTPParser analizador = new BlockingHTTPParser();

            // analizar peticion
            analizador.parseRequest(cliente.getInputStream());

            // PETICION FALLIDA
            if (analizador.failed()) {
                salidaCliente.append(Utiles.generaRespuesta(400));
            }

            // PETICION COMPLETA
            else if (analizador.isComplete()) {

                // METODO GET
                if (analizador.getMethod().equals("GET")) {
                    salidaCliente.append(httpGet(analizador));
                }

                // METODO POST
                else if (analizador.getMethod().equals("POST")) {
                    salidaCliente.append(httpPost(analizador));
                }

                // METODO NO IMPLEMENTADO (501)
                else {
                    salidaCliente.append(Utiles.generaRespuesta(501));
                }
            }

            // REQUEST NO COMPLETA
            else {
                // No deberia ocurrir por ser una conexion bloqueante
            }

            // fuerza escritura de lo que quede y finaliza el escritor
            salidaCliente.flush();
            salidaCliente.close();

            // cierra conexion
            cliente.close();

        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
