/*
 * AUTOR: Juan Vela y Marta Frias
 * NIA: 643821 - 535621
 * FICHERO: Ejercicio3.java
 * TIEMPO: 30 min
 * DESCRIPCION: Clase que gestiona cuatro procesos
 * 				y almacena sus eventos de recepcion
 */

package ssdd.p4.p4;

import java.io.FileNotFoundException;

import ssdd.p4.ms.AloneProcessException;
import ssdd.p4.ms.MessageSystem;
import ssdd.p4.ms.ProcessNotFoundException;
import ssdd.p4.ms.TotalOrderMulticast;
import ssdd.p4.ms.WrongFormatException;

/**
 * Clase que se encarga de gestionar cuatro procesos. Dos de ellos envian un
 * mensaje multicast simultaneamente.
 * 
 * @author Juan Vela
 * @author Marta Frias
 *
 */
public class Ejercicio3 {

	public static void main(String[] args) {

		int numProcesos = 4;
		boolean debug = false;
		String networkFile = "peers.txt";

		MessageSystem[] mss = new MessageSystem[numProcesos];
		TotalOrderMulticast[] toms = new TotalOrderMulticast[numProcesos];
		Ejercicio3Thread[] procesos = new Ejercicio3Thread[numProcesos];

		for (int i = 0; i < mss.length; i++) {

			try {
				mss[i] = new MessageSystem(i, networkFile, debug);

				// activa depuracion en fichero para mejorar legibilidad
				mss[i].setDebugFile();

				toms[i] = new TotalOrderMulticast(mss[i]);

				if (i < 2) {

					// receiver
					procesos[i] = new Ejercicio3Thread(i, false, toms[i]);

				} else {

					// sender
					procesos[i] = new Ejercicio3Thread(i, true, toms[i]);
				}

				Thread t = new Thread(procesos[i]);
				t.start();

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ProcessNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WrongFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AloneProcessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}
}
