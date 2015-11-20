/*
 * AUTOR: Juan Vela y Marta Frias
 * NIA: 643821 - 535621
 * FICHERO: MessageSystemSlave.java
 * TIEMPO: 1h
 * DESCRIPCION: Clase que gestiona un buzon de mensajes
 */

package ssdd.p3.ms;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Clase que se encarga de gestionar un buzon de mensajes. Recibe mensajes y los
 * añade al buzon mientras no este lleno y no este activada la peticion de
 * parada.
 * 
 * @author Juan Vela
 * @author Marta Frias
 *
 */
public class MessageSystemSlave implements Runnable {

    /** Buzon de mensajes */
    private InBox inBox;

    /** Peticion de parada */
    private boolean stopRequest;

    /** Numero de puerto en el que debe esperar nuevas conexiones */
    private int port;

    /**
     * Crea una instancia de MessageSystemSlave
     * 
     * @param inBox buzon de mensajes
     * @param port puerto en el que debe esperar nuevas conexiones
     */
    public MessageSystemSlave(InBox inBox, int port) {

        this.stopRequest = false;
        this.inBox = inBox;
        this.port = port;
    }

    /** Activa la peticion de parada */
    public void stop() {
        stopRequest = true;
    }

    /**
     * Ejecuta concurrente e indefinidamente una espera de conexiones. Las
     * conexiones se atienden secuencialmente.
     */
    @Override
    public void run() {

        ServerSocket receiver = null;

        try {
            receiver = new ServerSocket(port);

            // ejecutar hasta que se solicite su parada
            while (!stopRequest) {
                manageSender(receiver);
            }

        } catch (IOException e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Atiende y recibe el envio de un mensaje. El mensaje se encola en el buzon
     * de entrada asociado si hay hueco, o se descarta si no lo hay
     * 
     * @param receiver - Socket de servidor encargado de recibir las conexiones
     */
    private void manageSender(ServerSocket receiver) {

        Socket sender = null;
        ObjectInputStream senderInput = null;

        try {
            sender = receiver.accept();

            senderInput = new ObjectInputStream(sender.getInputStream());

            Serializable msg = (Serializable) senderInput.readObject();

            // si no se puede entregar el mensaje -> mostrar error
            if (!inBox.addMsg(msg)) {
                System.err.printf("ERROR: Se ha descartado el siguiente mensaje"
                        + " porque el buzon esta lleno\n%s\n", msg.toString());
            }

        } catch (IOException e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();

        } catch (ClassNotFoundException e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();

        } finally {

            if (senderInput != null) {

                try {
                    senderInput.close();

                } catch (IOException e) {
                    System.err.println("ERROR: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            if (sender != null) {

                try {
                    sender.close();

                } catch (IOException e) {
                    System.err.println("ERROR: " + e.getMessage());
                    e.printStackTrace();
                }
            }

        }
    }
}
