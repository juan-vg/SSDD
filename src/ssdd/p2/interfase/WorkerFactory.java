/*
 * AUTORES: Juan Vela Garcia / Marta Frias Zapater
 * NIA: 643821 / 535621
 * FICHERO: WorkerFactory.java
 * TIEMPO: 10 min
 * DESCRIPCION: Interfaz que extiende Remote y que obliga a 
 * implementar el metodo dameWorkers, el cual devuelve un vector de
 * referencias a servidores de calculo.
 */

package ssdd.p2.interfase;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface WorkerFactory extends Remote {
    
    /**
     * Devuelve un vector de hasta n referencias a servidores de 
     * calculo
     *
     * @param n numero de servidores de calculo
     * @return vector de hasta [n] referencias a servidores de calculo
     * @throws RemoteException
     */
    ArrayList<Worker> dameWorkers(int n) throws RemoteException;
}
