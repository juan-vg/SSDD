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
 * Clase que se encarga de gestionar un buzon de mensajes. Recibe mensajes
 * y los añade al buzon mientras no este lleno y no este activada la peticion
 * de parada.
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
	 * @param inBox
	 *            buzon de mensajes
	 * @param port
	 *            puerto en el que debe esperar nuevas conexiones
	 */
	public MessageSystemSlave(InBox inBox, int port) {

		this.stopRequest = false;
		this.inBox = inBox;
		this.port = port;
	}

	/** Activa la peticion de parada*/
	public void stop() {
		stopRequest = true;
	}

	@Override
	public void run() {
		
		ServerSocket sSocket = null;

		try {
			sSocket = new ServerSocket(port);
			
		} catch (IOException e) {
			System.err.println("ERROR: " + e.getMessage());
			System.exit(1);
		}

		Socket socket = null;
		ObjectInputStream ois = null;

		while (!stopRequest) {

			try {
				socket = sSocket.accept();

				ois = new ObjectInputStream(socket.getInputStream());

				Serializable msg = (Serializable) ois.readObject();
				inBox.addMsg(msg);

			} catch (IOException e) {
				System.err.println("ERROR: " + e.getMessage());

			} catch (ClassNotFoundException e) {
				System.err.println("ERROR: " + e.getMessage());

			} finally {

				if (socket != null) {

					try {
						socket.close();

					} catch (IOException e) {
						System.err.println("ERROR: " + e.getMessage());
					}
				}
				if (ois != null) {

					try {
						ois.close();

					} catch (IOException e) {
						System.err.println("ERROR: " + e.getMessage());
					}
				}
			}
		}
	}
}
