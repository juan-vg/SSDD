/*
 * AUTORES: Juan Vela Garcia / Marta Frias Zapater
 * NIA: 643821 / 535621
 * FICHERO: Registrador.java
 * TIEMPO: 1 hora
 * DESCRIPCION: Permite registrar servidores remotos en el registro RMI local.
 */

package ssdd.p2.herramientas;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ssdd.p2.interfase.Register;
import ssdd.p2.interfase.Worker;
import ssdd.p2.interfase.WorkerFactory;

/**
 * Servidor que permite registrar servidores remotos en el registro RMI local.
 * 
 * @author Juan Vela y Marta Frias
 *
 */
@SuppressWarnings("serial")
public class Registrador extends UnicastRemoteObject implements Register {

    /** Nombre con el que se registrara en el registro RMI */
    public static final String RMI_NAME = "REGISTRADOR";

    /** Referencia al registro RMI local */
    private final Registry registroRMI;

    /**
     * Crea un registrador de servidores remotos
     * 
     * @param registroRMI - Referencia al registro RMI local
     * @throws RemoteException
     */
    public Registrador(Registry registroRMI) throws RemoteException {
        super();
        this.registroRMI = registroRMI;
    }

    @Override
    public void registrarServidorAsignacion(WorkerFactory servidor,
            String nombre) throws RemoteException {
        try {
            registroRMI.rebind(nombre, servidor);
            System.out.printf(
                    "Servidor de asignacion (%s)" + " registrado con exito.\n",
                    nombre);
        } catch (RemoteException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public String registrarServidorCalculo(Worker servidor, String nombre)
            throws RemoteException {

        String nombreTmp = "";

        boolean registrado = false;
        String[] nombresRegistrados = registroRMI.list();

        int cuenta = 0;

        final Pattern workerPatt = Pattern.compile("Worker\\d+");

        // cuenta workers registrados
        for (int i = 0; i < nombresRegistrados.length; i++) {
            Matcher esWorker = workerPatt.matcher(nombresRegistrados[i]);
            if (esWorker.matches()) {
                cuenta++;
            }
        }

        // elige siguiente numero para el nuevo worker
        cuenta++;

        // mientras no se haya registrado
        while (!registrado) {

            try {
                nombreTmp = nombre + (cuenta);
                registroRMI.bind(nombreTmp, servidor);
                registrado = true;

                System.out.printf(
                        "Servidor de calculo (%s)" + " registrado con exito.\n",
                        nombreTmp);

            } catch (AlreadyBoundException e) {
                cuenta++;
            }
        }

        return nombreTmp;
    }
}
