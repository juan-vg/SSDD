/*
 * AUTORES: Juan Vela Garcia / Marta Frias Zapater
 * NIA: 643821 / 535621
 * FICHERO: WorkerServer.java
 * TIEMPO: 1 hora
 * DESCRIPCION: Proporciona la implementacion del metodo remoto 
 * encuentraPrimos, que obtiene los numeros primos en un intervalo dado.
 */


package ssdd.p2.servidor;


import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.LinkedList;

import ssdd.p2.interfase.Worker;

/**
 * Implementacion del metodo remoto encuentraPrimos, que obtiene los
 * numeros primos en un intervalo dado.
 * 
 * @author Juan Vela y Marta Frias
 * 
 */
@SuppressWarnings("serial")
public class WorkerServer extends UnicastRemoteObject implements
        Worker {

    /** Metodo constructor*/
	public WorkerServer() throws RemoteException {
		super();
	}

	@Override
	public ArrayList<Integer> encuentraPrimos(int min, int max)
			throws RemoteException {

		LinkedList<Integer> primos = new LinkedList<Integer>();

		for (int i = min; i <= max; i++) {
		    
			// probar si es primo
			if (i == 2) {
			    
				// es primo
				primos.add(i);
				
				
			} else if (i % 2 != 0 && i != 1) {
				double raiz = Math.sqrt(i);

				int j = 3;
				boolean esCandidato = true;
				while (j <= raiz && esCandidato) {
					if (i % j == 0) {
						esCandidato = false;
					} else {
						j++;
					}
				}
				if (esCandidato) {
					// es primo
					primos.add(i);
				}
			}
		}
		
		ArrayList<Integer> listaPrimos = new ArrayList<Integer>(primos.size());
        for (Integer p : primos) {
            listaPrimos.add(p);
        }

		return listaPrimos;
	}
}
