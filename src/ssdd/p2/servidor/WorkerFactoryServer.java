/*
 * AUTORES: Juan Vela Garcia / Marta Frias Zapater
 * NIA: 643821 / 535621
 * FICHERO: WorkerFactoryServer.java
 * TIEMPO: 1 hora
 * DESCRIPCION: Proporciona la implementacion del metodo remoto 
 * dameWorkers, que obtiene referencias a servidores de calculo.
 */

package ssdd.p2.servidor;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ssdd.p2.interfase.Worker;
import ssdd.p2.interfase.WorkerFactory;

/**
 * Implementacion del metodo remoto dameWorkers,que obtiene
 * referencias a servidores de calculo.
 * 
 * @author Juan Vela y Marta Frias
 * 
 */
@SuppressWarnings("serial")
public class WorkerFactoryServer extends UnicastRemoteObject
        implements WorkerFactory {

    /** Direccion ip donde se encuentra el servidor de registro*/
	private String host;

	/**
     * Metodo constructor
     * 
     * @param ipRegistro direccion ip donde se encuentra el servidor
     *           de registro
     * @throws RemoteException
     */
	public WorkerFactoryServer(String ipRegistro)
            throws RemoteException {
        super();
        host = ipRegistro;
    }

	@Override
    public ArrayList<Worker> dameWorkers(int n)
            throws RemoteException {
		
		ArrayList<Worker> workers;		
		Registry registry = LocateRegistry.getRegistry(host);
		String[] nombres = registry.list();
		int numServers = 0;
		final Pattern wPattern = Pattern.compile("Worker\\d+");
		
		for (int i = 0; i < nombres.length && numServers < n; i++) {
            Matcher matcher = wPattern.matcher(nombres[i]);
            
            if (matcher.matches()) {
                numServers++;
            }
        }
		workers = new ArrayList<Worker>(numServers);

		int asignados = 0;
		int i = 0;
		
		while (asignados < numServers && i < nombres.length) {
			Matcher matcher = wPattern.matcher(nombres[i]);
			
			if (matcher.matches()) {
				try {
					workers.add((Worker)registry.lookup(nombres[i]));
					asignados++;
				} catch (NotBoundException e) {
				    
					// no deberia ocurrir
					System.err.println("ERROR: " + nombres[i]
							+ " no encontrado.");
				}
			}
			i++;
		}

		return workers;
	}

}
