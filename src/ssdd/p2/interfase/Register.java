/*
 * AUTORES: Juan Vela Garcia / Marta Frias Zapater
 * NIA: 643821 / 535621
 * FICHERO: Register.java
 * TIEMPO: 10 minutos
 * DESCRIPCION: Interfaz que extiende Remote y que obliga a 
 * implementar el metodo registrarServidorAsignacion y registrarServidorCalculo,
 * los cuales registran servidores de asignacion y de calculo (respectivamente)
 * en el registro RMI local
 */

package ssdd.p2.interfase;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Register extends Remote {

    /**
     * Registra un servidor de asignacion en el registro RMI local
     * 
     * @param servidor - Objeto que gestiona el servidor de asignacion que se
     *            desea registrar
     * @param nombreRMI - Nombre con el que se debe registrar el servidor
     * @throws RemoteException
     */
    public void registrarServidorAsignacion(WorkerFactory servidor,
            String nombreRMI) throws RemoteException;

    /**
     * Registra un servidor de calculo en el registro RMI local
     * 
     * @param servidor - Objeto que gestiona el servidor de calculo que se desea
     *            registrar
     * @param nombreRMI - Nombre con el que se debe registrar el servidor
     * @return Nombre asignado en el registro RMI
     * @throws RemoteException
     */
    public String registrarServidorCalculo(Worker servidor, String nombreRMI)
            throws RemoteException;

}
