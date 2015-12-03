/*
 * AUTOR: Juan Vela y Marta Frias
 * NIA: 643821 - 535621
 * FICHERO: Launcher.java
 * TIEMPO: 1 horas
 * DESCRIPCION: Aplicacion que crea una sala virtual de chat
 */

package ssdd.p4.p4;

import java.awt.event.*;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import ssdd.p4.ms.AloneProcessException;
import ssdd.p4.ms.Envelope;
import ssdd.p4.ms.MessageSystem;
import ssdd.p4.ms.ProcessNotFoundException;
import ssdd.p4.ms.TotalOrderMulticast;
import ssdd.p4.ms.WrongFormatException;

/**
 * Clase que permite crear sala virtual de chat
 *
 * @author Juan Vela
 * @author Marta Frias
 *
 */
public class Launcher {

	private static ChatDialog chat;
	private static TotalOrderMulticast tom;

	private final String ERROR1 = "The following processes"
			+ " are DISCONNECTED:\n";

	private final String ERROR2 = "\nPlease, CHECK: \n"
			+ "    -- Your network file.\n"
			+ "    -- The processes's state.\n\n";

	/**
	 * Gestiona la interfaz grafica de usuario (gui)
	 * 
	 */
	public Launcher() {

		// crea la ventana de chat
		chat = new ChatDialog(new ActionListener() {

			// Definicion al vuelo de actionPerformed
			// Se ejecuta al pulsar el boton "Enviar".
			public void actionPerformed(ActionEvent e) {

				final String m = chat.text();

				if (!m.isEmpty()) {

					// Crea proceso paralelo
					// Se encarga de gestionar el envio o los errores
					// sin bloquear la GUI
					SwingUtilities.invokeLater(new Runnable() {

						// Definicion al vuelo de run
						public void run() {

							LinkedList<Integer> disconnected = tom
									.sendMulticast(m);

							if (disconnected.size() > 0) {

								Collections.sort(disconnected);

								chat.addMessage("\t******* ERROR *******");
								chat.addMessage(ERROR1);

								for (Integer id : disconnected) {
									chat.addMessage("-----> Process ID " + id);
								}

								chat.addMessage(ERROR2);

							}
						}
						// Fin definicion run
					});

				}
			}
			// Fin definicion actionPerformed
		});

		chat.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * Comprueba params, inicia la gui y se queda en un bucle infinito
	 * recibiendo mensajes. Termina cuando se cierra la ventana.
	 * 
	 * @param args
	 *            : [-d] num_proc network_file
	 */
	public static void main(String[] args) {

		boolean debug = false;
		String networkFile = "peers.txt";
		int numProc = 0;

		if (args.length > 1) {
			if (args[0].equals("-d")) { // debug

				debug = true;
				try {
					numProc = Integer.parseInt(args[1]);
					networkFile = args[2];

				} catch (NumberFormatException e) {
					System.err.println("ERROR: " + e.getMessage());
				}

			} else {

				try {
					numProc = Integer.parseInt(args[0]);
					networkFile = args[1];

				} catch (NumberFormatException e) {
					System.err.println("ERROR: " + e.getMessage());
				}
			}

			if (numProc > 0) {

				MessageSystem ms = null;

				try {
					ms = new MessageSystem(numProc, networkFile, debug);
					tom = new TotalOrderMulticast(ms);

					new Launcher();
					chat.setTitle("Process ID " + numProc);

					while (true) {

						// Todo mensaje presentado en el chat debe ser recibido
						// Asegura ordenacion correcta (la misma para todos)
						// incluso con mensajes propios
						Envelope msg = tom.receiveMulticast();

						if (msg.getSource() == numProc) {
							chat.addMessage("Yo: " + msg.getPayload());

						} else {
							chat.addMessage(msg.getSource() + ": "
									+ msg.getPayload());
						}
					}

				} catch (FileNotFoundException e) {
					System.err.println("ERROR: " + e.getMessage());

				} catch (ProcessNotFoundException e) {
					System.err.println("ERROR: " + e.getMessage());

				} catch (WrongFormatException e) {
					System.err.println("ERROR: " + e.getMessage());

				} catch (AloneProcessException e) {
					System.err.println("ERROR: " + e.getMessage());

				} finally {

					if (ms != null) {
						ms.stopMailbox();
					}
				}
			}

		} else {
			System.err.println("Syntax: [-d] num_proc network_file");
			System.err.println("   num_proc : (number) Process ID");
			System.err
					.println("   network_file : (txt file) List of processes (ID:location:port)");
		}
	}
}
