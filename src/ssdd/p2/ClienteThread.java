/*
 * AUTORES: Juan Vela Garcia / Marta Frias Zapater
 * NIA: 643821 / 535621
 * FICHERO: ClienteThread.java
 * TIEMPO: 2 horas
 * DESCRIPCION: cada ClienteThread gestiona un intervalo de numeros
 * enteros y obtiene los que son primos mediante la invocacion al 
 * metodo remoto de un servidor de calculo.
 */

package ssdd.p2;

import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Gestiona un intervalo de numeros enteros y obtiene los que son
 * primos mediante la invocacion al metodo remoto de un servidor de
 * calculo.
 * 
 * @author Juan Vela y Marta Frias
 * 
 */
public class ClienteThread implements Runnable{
	
    /** Intervalo de numeros enteros */
    private Intervalo intervalo;
    
    /** Servidor de calculo */
    private Worker worker;
    
    /** Lista de numeros primos */
    private ArrayList<Integer> primos;
    
    /** Atributo que indica si ha sido atendido */
    private boolean atendido;
    
    /** Atributo que indica si ha terminado su trabajo */
    private boolean acabado;
    
    /**
     * Atributo que indica si ha fallado y no ha hecho su
     * trabajo
     */
    private boolean fallido;

    /**
     * Metodo constructor
     * @param w servidor de calculo
     * @param i intervalo 
     */
	public ClienteThread(Worker w, Intervalo i){
		worker = w;
		intervalo = i;
		primos = null;
		atendido = false;
		acabado = false;
		fallido = false;
	}
	
	/**
	 * Devuelve la referencia al servidor de calculo
	 * @return servidor de calculo
	 */
	public Worker getWorker(){
		return worker;
	}
	
	/**
	 * Devuelve la referencia al intervalo
	 * @return intervalo
	 */
	public Intervalo getIntervalo(){
		return intervalo;
	}
	
	/**
	 * Modifica el intervalo
	 * @param i intervalo
	 */
	public void setIntervalo(Intervalo i){
		intervalo = i;
		primos = null;
		atendido = false;
		acabado = false;
		fallido = false;
	}
	
	/**
	 * Devuelve la lista de primos que se encuentran en el intervalo
	 * @return lista de numeros primos del intervalo
	 */
	public ArrayList<Integer> resultado(){
		return primos;
	}
	
	/**
	 * Informa de si ha terminado el trabajo con exito
	 * @return true si ha terminado el trabajo
	 */
	public boolean haAcabado(){
		return acabado;
	}
	
	/**
	 * Informa de si ha fallado al realizar el trabajo 
	 * @return true si se ha producido algun fallo
	 */
	public boolean haFallado(){
		return fallido;
	}
	
	/**
	 * Establece que ha sido atendido
	 */
	public void setAtendido(){
		atendido = true;
	}
	
	/**
	 * Informa de si se ha atendido
	 * @return true si se ha atendido
	 */
	public boolean atendido(){
		return atendido;
	}

	@Override
    public void run() {
        try {
            primos = worker.encuentraPrimos(intervalo.getMin(),
                    intervalo.getMax());
            acabado = true;
        } catch (RemoteException e) {
            fallido = true;
            System.err.println("ERROR: se ha perdido la conexion "+
                    "con un worker.");
        }
    }
}
