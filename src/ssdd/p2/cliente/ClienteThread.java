/*
 * AUTORES: Juan Vela Garcia / Marta Frias Zapater
 * NIA: 643821 / 535621
 * FICHERO: ClienteThread.java
 * TIEMPO: 2 horas
 * DESCRIPCION: cada ClienteThread gestiona un intervalo de numeros
 * enteros y obtiene los que son primos mediante la invocacion al 
 * metodo remoto de un servidor de calculo.
 */

package ssdd.p2.cliente;

import java.rmi.RemoteException;
import java.util.ArrayList;

import ssdd.p2.herramientas.Intervalo;
import ssdd.p2.herramientas.Resultado;
import ssdd.p2.herramientas.UnionIntervalos;
import ssdd.p2.interfase.Worker;

/**
 * Gestiona un intervalo de numeros enteros y obtiene los que son primos
 * mediante la invocacion al metodo remoto de un servidor de calculo.
 * 
 * @author Juan Vela y Marta Frias
 * 
 */
public class ClienteThread implements Runnable {

    /** Union de dos intervalos de numeros enteros */
    private UnionIntervalos intervalo;

    /** Servidor de calculo */
    private Worker worker;

    /** Atributo que indica si ha sido atendido */
    private boolean atendido;

    /** Atributo que indica si ha terminado su trabajo */
    private boolean acabado;

    /**
     * Atributo que indica si ha fallado y no ha hecho su trabajo
     */
    private boolean fallido;

    private Resultado resultado;

    private int numElems;

    /**
     * Metodo constructor
     * 
     * @param w servidor de calculo
     * @param i union de dos intervalos
     */
    public ClienteThread(Worker w) {
        worker = w;
        intervalo = null;
        atendido = false;
        acabado = false;
        fallido = false;
        resultado = new Resultado();
        numElems = 0;
    }

    /**
     * @return the numElems
     */
    public int getNumElems() {
        return numElems;
    }

    /**
     * @param numElems the numElems to set
     */
    public void setNumElems(int numElems) {
        this.numElems = numElems;
    }

    /**
     * Devuelve la referencia al servidor de calculo
     * 
     * @return servidor de calculo
     */
    public Worker getWorker() {
        return worker;
    }

    /**
     * Devuelve la referencia al intervalo
     * 
     * @return intervalo
     */
    public UnionIntervalos getIntervalo() {
        return intervalo;
    }

    /**
     * Establece un nuevo intervalo
     * 
     * @param intervalo Union de dos intervalos de numero primos
     */
    public void setIntervalo(UnionIntervalos intervalo) {
        this.intervalo = intervalo;
        acabado = false;
        atendido = false;
        fallido = false;
        resultado = new Resultado();
    }

    /**
     * Devuelve la lista de primos que se encuentran en el intervalo
     * 
     * @return lista de numeros primos del intervalo
     */
    public Resultado getResultado() {

        return resultado;
    }

    /**
     * Informa de si ha terminado el trabajo con exito
     * 
     * @return true si ha terminado el trabajo
     */
    public boolean isAcabado() {
        return acabado;
    }

    /**
     * Informa de si ha fallado al realizar el trabajo
     * 
     * @return true si se ha producido algun fallo
     */
    public boolean isFallido() {
        return fallido;
    }

    /**
     * Establece que ha sido atendido
     */
    public void setAtendido() {
        atendido = true;
    }

    /**
     * Informa de si se ha atendido
     * 
     * @return true si se ha atendido
     */
    public boolean isAtendido() {
        return atendido;
    }

    @Override
    public void run() {
        try {
            ArrayList<Integer> primos1 = null;
            ArrayList<Integer> primos2 = null;

            long tIni = System.nanoTime();

            Intervalo intervaloA = intervalo.getIntervaloA();
            Intervalo intervaloB = intervalo.getIntervaloB();

            if (intervaloA != null) {
                primos1 = worker.encuentraPrimos(intervaloA.getMin(),
                        intervaloA.getMax());
            }

            if (intervaloB != null) {
                primos2 = worker.encuentraPrimos(intervaloB.getMin(),
                        intervaloB.getMax());
            }

            resultado.setResultado1(primos1);
            resultado.setResultado2(primos2);

            long tTotal = System.nanoTime() - tIni;

            resultado.setTiempo((double) tTotal / 1000000.0);

            acabado = true;

        } catch (RemoteException e) {
            fallido = true;
            System.err.println(
                    "ERROR: se ha perdido la conexion " + "con un worker.");
        }
    }
}
