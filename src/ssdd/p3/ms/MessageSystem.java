/*
 * AUTOR: Juan Vela y Marta Frias
 * NIA: 643821 - 535621
 * FICHERO: MessageSystem.java
 * TIEMPO: 8 horas
 * DESCRIPCION: Sistema de mensajes
 */

package ssdd.p3.ms;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Clase que gestiona un sistema de mensajes que comunica procesos que se
 * conocen entre si. Cada instancia dispone de un buzon de mensajes, con las
 * operaciones de enviar, recibir y parar buzon, y un fichero que indica la 
 * localizacion de los procesos vecinos.
 * 
 * @author Juan Vela
 * @author Marta Frias
 *
 */
public class MessageSystem {

	/** Maximo numero de elementos en la bandeja de entrada por defecto*/
	private final static int DEFAULT_SIZE = 100;

	/** Identificador del sistema */
	private int src;
	
	/** Numero de puerto */
	private int port;
	
	/** Fichero de localizaciones de procesos */
	private String netFile;
	
	/** Bandera de depuracion */
	private boolean debug;
	
	/** Esclavo encargado de recibir mensajes */
	private MessageSystemSlave slave;
	
	/** Lista de vecinos conocidos */
	private LinkedList<Neighbor> neighbors;
	
	/** Buzon de mensajes */
	private InBox inBox;
	
	
	/**
	 * 
	 * Crea una instancia de MessageSystem.
	 * 
	 * @param source
	 *            : Identificador del sistema
	 * @param networkFile
	 *            : Fichero de localizaciones de procesos
	 * @param debug
	 *            : Habilita el modo depuracion. Es decir, se mostraran todos
	 *            los mensajes enviados y recibidos
	 * @param size
	 *            : Maximo numero de elementos en la bandeja de entrada.
	 * 
	 * @throws FileNotFoundException
	 *             Si <b>networkFile</b> no corresponde a un fichero valido.
	 *
	 * @throws ProcessNotFoundException
	 *             Si <b>networkFile</b> no contiene el identificador de este
	 *             proceso.
	 * 
	 * @throws WrongFormatException
	 *             Si <b>networkFile</b> contiene errores de formato.
	 * 
	 * @see {@link #MessageSystem(int source, String networkFile, boolean debug)}
	 * 
	 */
	public MessageSystem(int source, String networkFile, boolean debug, int size)
			throws FileNotFoundException, ProcessNotFoundException,
			WrongFormatException {

		init(source, networkFile, debug, size);
	}

	/**
	 * 
	 * Crea una instancia de MessageSystem cuya bandeja de entrada puede
	 * albergar un numero de elementos hasta un valor por defecto (100).
	 * 
	 * @param source
	 *            : Identificador del sistema
	 * @param networkFile
	 *            : Fichero de localizaciones de procesos
	 * @param debug
	 *            : Habilita el modo depuracion. Es decir, se mostraran todos
	 *            los mensajes enviados y recibidos
	 * 
	 * @throws FileNotFoundException
	 *             Si <b>networkFile</b> no corresponde a un fichero valido.
	 * 
	 * @throws ProcessNotFoundException
	 *             Si <b>networkFile</b> no contiene el identificador de este
	 *             proceso.
	 * 
	 * @throws WrongFormatException
	 *             Si <b>networkFile</b> contiene errores de formato.
	 * 
	 * @see {@link #MessageSystem(int source, String networkFile, boolean debug, int size)}
	 * 
	 */
	public MessageSystem(int source, String networkFile, boolean debug)
			throws FileNotFoundException, ProcessNotFoundException,
			WrongFormatException {

		init(source, networkFile, debug, DEFAULT_SIZE);
	}
	

	/**
	 * Inicializa la estructura MessageSystem tras obtener los datos contenidos
	 * en el fichero acerca del resto de procesos.
	 * 
	 * @param source
	 *            : Identificador del sistema
	 * @param networkFile
	 *            : Fichero de localizaciones de procesos
	 * @param debug
	 *            : Habilita el modo depuracion. Es decir, se mostraran todos
	 *            los mensajes enviados y recibidos
	 * @param size
	 *            : Maximo numero de elementos en la bandeja de entrada.
	 * 
	 * @throws FileNotFoundException
	 *             Si <b>networkFile</b> no corresponde a un fichero valido.
	 * 
	 * @throws ProcessNotFoundException
	 *             Si <b>networkFile</b> no contiene el identificador de este
	 *             proceso.
	 * 
	 * @throws WrongFormatException
	 *             Si <b>networkFile</b> contiene errores de formato.
	 * 
	 */
	private void init(int source, String networkFile, boolean debug, int size)
			throws FileNotFoundException, ProcessNotFoundException,
			WrongFormatException {

		this.src = source;
		this.port = -1;
		this.netFile = networkFile;
		this.debug = debug;
		this.inBox = new InBox(size);
		this.neighbors = new LinkedList<Neighbor>();

		Scanner reader = null;

		try {
			// Leer fichero de neighbors
			reader = new Scanner(new File(netFile));

			while (reader.hasNextLine()) {

				String line = reader.nextLine();
				String[] lineFields = line.split(":");

				int id = Integer.parseInt(lineFields[0]);
				String addr = lineFields[1];
				int port = Integer.parseInt(lineFields[2]);

				if (id == this.src) {
					this.port = port;
					
				} else {
					neighbors.add(new Neighbor(id, addr, port));
				}
			}

			if (port == -1) {
				throw new ProcessNotFoundException(
						"Could't locate the process ID on the networkFile.");
			}

			// Iniciar esclavo que gestiona el buzon de mensajes
			slave = new MessageSystemSlave(inBox, port);
			
			Thread slaveThread = new Thread(slave);
			slaveThread.start();

		} catch (IndexOutOfBoundsException e) {
			// No hay 3 campos en una linea
			throw new WrongFormatException("Wrong networkFile format.");

		} catch (NumberFormatException e) {
			// Formato incorrecto: id o puerto
			throw new WrongFormatException("Wrong networkFile format.");

		} finally {

			if (reader != null) {
				reader.close();
			}
		}
	}

	/**
	 * Envia un objeto Serializable al proceso destino [dst] si este se 
	 * encuentra entre los procesos conocidos.
	 * 
	 * @param dst
	 *            : Identificador del proceso destino.
	 * @param message
	 *            : Contenido del mensaje.
	 */
	public void send(int dst, Serializable message) {

		Envelope msg = new Envelope(src, dst, message);
		Socket socket = null;
		ObjectOutputStream oos = null;

		try {
			Neighbor dstN = null;

			for (Neighbor n : neighbors) {

				if (n.getId() == dst) {

					dstN = n;
				}
			}

			if(dstN != null){
				
				socket = new Socket(dstN.getAddres(), dstN.getPort());
				oos = new ObjectOutputStream(socket.getOutputStream());

				if (debug) {
					System.out.printf("\n>>> SENDING >>>\n");
					System.out.println(msg.toString());
				}

				oos.writeObject(msg);

				if (debug) {
					System.out.println(">>> OK: Message sent >>>");
				}
			}
			
		} catch (IOException e) {
			System.err.println("ERROR: " + e.getMessage());

		} finally {

			if (socket != null) {
				try {
					socket.close();

				} catch (IOException e) {
					System.err.println("ERROR: " + e.getMessage());
				}
			}

			if (oos != null) {
				try {
					oos.close();

				} catch (IOException e) {
					System.err.println("ERROR: " + e.getMessage());
				}
			}
		}

	}

	/**
	 * <b>Bloqueante.</b><br/>
	 * <br/>
	 * Extrae un mensaje de la bandeja de entrada. Si la bandeja de entrada esta
	 * vacia, el proceso que lo invoque se quedara bloqueado hasta que se reciba
	 * algo.
	 * 
	 * @return Objeto Envelope con el contenido del mensaje, el emisor y el
	 *         receptor.
	 * 
	 */
	public Envelope receive() {

		Envelope msg = null;

		msg = (Envelope) inBox.getMsg();

		if (debug) {
			System.out.printf("\n<<< RECEIVING <<<\n");
			System.out.println(msg.toString());
		}

		return msg;
	}

	/**
	 * Comunica la orden de finalizacion al esclavo encargado de la gestion del
	 * buzon de mensajes. Es probable que no la reciba por estar esperando
	 * nuevas peticiones, por lo que se inicia una ultima conexion con dicho
	 * esclavo con un mensaje de terminacion para que la orden se haga efectiva.
	 */
	public void stopMailbox() {

		slave.stop();

		Socket slaveSock = null;
		try {
			slaveSock = new Socket("127.0.0.1", port);
			ObjectOutputStream oos = new ObjectOutputStream(
					slaveSock.getOutputStream());

			Envelope msg = new Envelope(src, src, "END");
			oos.writeObject(msg);

		} catch (UnknownHostException e) {
			// no deberia ocurrir, la direccion local siempre existe
		} catch (IOException e) {
			System.err.println("ERROR: " + e.getMessage());

		} finally {
			if (slaveSock != null) {

				try {
					slaveSock.close();

				} catch (IOException e) {
					System.err.println("ERROR: " + e.getMessage());
				}
			}
		}
	}

}
