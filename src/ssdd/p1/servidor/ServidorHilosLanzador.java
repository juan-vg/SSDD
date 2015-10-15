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
     * @param puerto : Numero de puerto en el que el servidor debe
     *            permanecer a la escucha de nuevas conexiones
     * 
     */
    public static void iniciar(int puerto) {

        ServerSocket servidor = null;
        Socket cliente = null;

        try {

            // crea el socket servidor en el puerto [puerto]
            servidor = new ServerSocket(puerto);

            boolean finalizar = false;

            // espera peticiones de clientes y cede la atencion de los
            // mismos a un hilo separado, permitiendo atender a mas clientes
            // a la vez
            while (!finalizar) {

                // se bloquea en espera de nuevos clientes
                cliente = servidor.accept();

                // si llega un cliente sin errores
                if (cliente != null) {

                    // asigna cliente a nuevo hilo
                    ServidorHilosEjecutable hijo = new ServidorHilosEjecutable(
                            cliente);

                    // comienza la ejecucion del nuevo hilo
                    Thread thread = new Thread(hijo);
                    thread.start();
                }
            }

            // cierra el servidor
            servidor.close();

        } catch (IOException e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
