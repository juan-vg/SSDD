/*
 * AUTORES: Juan Vela Garcia / Marta Frias Zapater
 * NIA: 643821 / 535621
 * FICHERO: ServidorThreadRun.java
 * TIEMPO: 25 horas
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
    private Socket clientSocket;

    /** Metodo constructor de la clase ServidorThreadHTTP */
    public ServidorHilosEjecutable(Socket c) {
        clientSocket = c;
    }

    @Override
    public void run() {
        try {
            PrintWriter salida = new PrintWriter(clientSocket.getOutputStream(),
                    true);

            BlockingHTTPParser parser = new BlockingHTTPParser();
            
            // analizar peticion
            parser.parseRequest(clientSocket.getInputStream());

            // PETICION FALLIDA
            if (parser.failed()) {
                salida.append(Utiles.generaRespuesta(400, null));
            }
            
            // PETICION COMPLETA
            else if (parser.isComplete()) {

                // METODO GET
                if (parser.getMethod().equals("GET")) {
                    salida.append(httpGet(parser));
                }
                
                // METODO POST
                else if (parser.getMethod().equals("POST")) {
                    salida.append(httpPost(parser));
                }
                
                // METODO NO IMPLEMENTADO (501)
                else {
                    salida.append(Utiles.generaRespuesta(501, null));
                }
            }
            
            // REQUEST NO COMPLETA
            else {
                // No deberia ocurrir por ser una conexion bloqueante
            }
            
            salida.flush();
            salida.close();

            // cierra conexion
            clientSocket.close();

        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
