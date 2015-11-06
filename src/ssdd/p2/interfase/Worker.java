/*
 * AUTORES: Juan Vela Garcia / Marta Frias Zapater
 * NIA: 643821 / 535621
 * FICHERO: Worker.java
 * TIEMPO: 10 min
 * DESCRIPCION: Interfaz que extiende Remote y que obliga a 
 * implementar el metodo encuentraPrimos, el cual devuelve una lista de
 * numeros enteros que son primos en un intervalo dado.
 */

package ssdd.p2.interfase;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface Worker extends Remote {
    /**
     * Devuelve un vector con los primos entre min y max (min<max)
     * 
     * @param min minimo entero del intervalo
     * @param max maximo entero del intervalo
     * @return vector con los numeros primos entre min y max
     * @throws RemoteException
     */
    ArrayList<Integer> encuentraPrimos(int min, int max) throws RemoteException;
}
