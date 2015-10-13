package ssdd.p1.servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorThreadLanzador {

    /**
     * Version del servidor HTTP utilizando hilos
     */
    public static void iniciar(int puerto) {

        ServerSocket servSock = null;
        Socket clntSock = null;

        try {
            servSock = new ServerSocket(puerto);
        } catch (IOException e) {
            System.err.println("ERROR: Fallo asociando puerto: " + puerto);
            System.exit(1);
        }

        try {
            while ((clntSock = servSock.accept()) != null) {
                ServidorThreadRun hijo = new ServidorThreadRun(clntSock);
                Thread thread = new Thread(hijo);
                thread.run();
            }
        } catch (IOException e) {
            System.err.println("ERROR: Fallo aceptando nueva conexion.");
            System.exit(2);
        }

        try {
            servSock.close();
        } catch (IOException e) {
            System.err.println("ERROR: Fallo cerrando conexion.");
            System.exit(3);
        }
    }
}
