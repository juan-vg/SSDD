/*
 * AUTOR: Juan Vela y Marta Frias
 * NIA: 643821 - 535621
 * FICHERO: Ejercicio3Thread.java
 * TIEMPO: 10 min
 * DESCRIPCION: Clase que gestiona un proceso que envia y recibe mensajes
 */

package ssdd.p4.aplicacion;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Formatter;

import ssdd.p4.ms.TotalOrderMulticast;

/**
 * Clase que se encarga de gestionar un proceso. Si sender es cierto, envia un
 * mensaje. Recibe mensajes y los almacena en un fichero
 * 
 * @author Juan Vela
 * @author Marta Frias
 *
 */
public class Ejercicio3Thread implements Runnable {

	private boolean sender;
	private TotalOrderMulticast tom;
	private int id;

	private Formatter f;

	public Ejercicio3Thread(int i, boolean s, TotalOrderMulticast t) {
		id = i;
		sender = s;
		tom = t;

		try {

			f = new Formatter(new File("salidaTOM" + id + ".txt"));

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {

		if (sender) {
			tom.sendMulticast("Soy " + id);
		}

		while (true) {
			// System.out.println(tom.receiveMulticast());
			f.format("%s\n", tom.receiveMulticast().toString());
			f.flush();
		}

	}

}
