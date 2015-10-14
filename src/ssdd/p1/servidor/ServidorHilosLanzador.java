/*
 * AUTORES: Juan Vela Garcia / Marta Frias Zapater
 * NIA: 643821 / 535621
 * FICHERO: ServidorThreadLanzador.java
 * TIEMPO: 30 minutos
 * DESCRIPCION: Servidor web (HTTP) usando hilos.
 */

package ssdd.p1.servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Servidor HTTP sencillo utilizando un hilo por cada nuevo cliente
 * 
 * @author Juan Vela, Marta Frias
 *
 */
public class ServidorHilosLanzador {

    /**
     * Metodo que permite iniciar un servidor HTTP cuyo funcionamiento se basa
     * en hilos
     * 
     * @param puerto : <b>Numero</b> de puerto en el que el servidor debe
     *            permanecer a la escucha de nuevas conexiones
     */
    public static void iniciar(int puerto) {

        ServerSocket servSock = null;
        Socket clntSock = null;

        // crea servidor en el puerto [puerto]
        try {
            servSock = new ServerSocket(puerto);

        } catch (IOException e) {
            System.err.println("ERROR: Fallo asociando puerto: " + puerto);
            System.exit(1);
        }

        // espera peticiones de clientes y cede la atencion de los mismos
        // a un hilo separado, permitiendo atender a mas clientes
        try {
            while (true) {
                clntSock = servSock.accept();

                if (clntSock != null) {
                    ServidorHilosEjecutable hijo = new ServidorHilosEjecutable(clntSock);
                    Thread thread = new Thread(hijo);
                    thread.run();
                }
            }

        } catch (IOException e) {
            System.err.println("ERROR: Fallo aceptando nueva conexion.");
            System.exit(2);
        }

        // cierra el servidor
        try {
            servSock.close();

        } catch (IOException e) {
            System.err.println("ERROR: Fallo cerrando conexion.");
            System.exit(3);
        }
    }
}
