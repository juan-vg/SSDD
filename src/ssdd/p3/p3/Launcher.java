/*
 * AUTOR: Juan Vela y Marta Frias
 * NIA: 643821 - 535621
 * FICHERO: Launcher.java
 * TIEMPO: 4 horas
 * DESCRIPCION: Aplicacion distribuida que implementa el sistema de mensajes
 * diseñado en la clase MessageSystem
 */

package ssdd.p3.p3;

import java.io.FileNotFoundException;

import ssdd.p3.ms.Envelope;
import ssdd.p3.ms.MessageSystem;
import ssdd.p3.ms.ProcessNotFoundException;
import ssdd.p3.ms.WrongFormatException;

/**
 * Clase que permite generar una simulacion de aplicacion distribuida que 
 * implementa el sistema de mensajes diseñado en la clase MessageSystem.
 * El proceso con id == 1 se convierte en el gestor de la base de datos.
 * El resto de procesos se convierten en clientes haciendo consultas cada cierto
 * tiempo a la base de datos gestionada por el proceso 1. Estas acciones se 
 * pueden entrelazar y conllevar a resultados diferentes.
 *
 * @author Juan Vela
 * @author Marta Frias
 *
 */
public class Launcher {

	/**
	 * Gestor de la base de datos.
	 * 
	 * @param networkFile
	 *            : Nombre del fichero en donde se encuentra la localizacion de
	 *            cada proceso.
	 * 
	 * @param debug
	 *            : Bandera de depuracion.
	 */
	private static void manager(String networkFile, boolean debug) {

		try {
			MessageSystem ms = new MessageSystem(1, networkFile, debug);
			SGBD db = new SGBD(100);

			db.insert("usuarios", "");
			db.insert("ultimoEnSalir", "");

			Query query;
			Envelope e;
			boolean res = false;

			while (true) {
				e = ms.receive();
				query = (Query) e.getPayload();

				String op = query.getOp();
				if (op.equals("insert")) {
					res = db.insert(query.getKey(), query.getVal());

					if (res) {
						ms.send(e.getSource(), "OK");

					} else {
						ms.send(e.getSource(), "KO");

					}

				} else if (op.equals("update")) {
					res = db.update(query.getKey(), query.getVal());

					if (res) {
						ms.send(e.getSource(), "OK");

					} else {
						ms.send(e.getSource(), "KO");

					}
				} else if (op.equals("delete")) {
					res = db.delete(query.getKey());

					if (res) {
						ms.send(e.getSource(), "OK");

					} else {
						ms.send(e.getSource(), "KO");
					}
				} else if (op.equals("select")) {
					String sel = db.select(query.getKey());

					if (sel != null) {
						ms.send(e.getSource(), sel);

					} else {
						ms.send(e.getSource(), "KO");
					}
				}
			}
		} catch (FileNotFoundException e) {
			System.err.println("ERROR: The file " + networkFile
					+ " doesn't exist");

		} catch (ProcessNotFoundException e) {
			System.err.println("ERROR: " + e.getMessage());

		} catch (WrongFormatException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
	}

	/**
	 * Simula la ejecucion de una tarea durante un tiempo aleatorio.
	 * Detiene la ejecucion un numero aleatorio de milisegundos en el intervalo
	 * (0, 10000).
	 */
	private static void doSth() {
		try {
			Thread.sleep((int) (Math.random() * 10000.0));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Cliente de la base de datos.
	 * 
	 * @param numProc
	 *            : Identificador del proceso actual.
	 * 
	 * @param networkFile
	 *            : Nombre del fichero en donde se encuentra la localizacion de
	 *            cada proceso.
	 * 
	 * @param debug
	 *            : Bandera de depuracion.
	 */
	private static void client(int numProc, String networkFile, boolean debug) {

		try {
			MessageSystem ms = new MessageSystem(numProc, networkFile, debug);

			Query query = new Query("insert", "primeroEnEntrar", "(" 
								+ numProc + ") ");

			ms.send(1, query);
			String resp = (String) ms.receive().getPayload();

			doSth();

			query = new Query("insert", "usuarios", "(" + numProc + ") ");
			ms.send(1, query);
			resp = (String) ms.receive().getPayload();

			doSth();

			if (!resp.equals("OK")) {

				query = new Query("select", "usuarios", null);
				ms.send(1, query);
				resp = (String) ms.receive().getPayload();

				doSth();

				query = new Query("update", "usuarios", resp + "(" + numProc
						+ ") ");

				ms.send(1, query);
				resp = (String) ms.receive().getPayload();
			}

			doSth();

			query = new Query("update", "ultimoEnSalir", "(" + numProc + ") ");
			ms.send(1, query);
			resp = (String) ms.receive().getPayload();

			ms.stopMailbox();

		} catch (FileNotFoundException e) {
			System.err.println("ERROR: The file " + networkFile
					+ " doesn't exist");

		} catch (ProcessNotFoundException e) {
			System.err.println("ERROR: " + e.getMessage());

		} catch (WrongFormatException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
	}

	/**
	 * Metodo principal que tras comprobar la correccion de los argumentos, si
	 * el identificador de proceso es 1 inicia el gestor, si no inicia un
	 * cliente.
	 * 
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

				if (numProc == 1) { // inicia el gestor

					manager(networkFile, debug);

				} else { // inicia un cliente

					client(numProc, networkFile, debug);
				}
			}
		} else {
			System.err.println("Syntax: [-d] num_proc network_file");
		}
	}

}
