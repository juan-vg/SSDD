/*
 * AUTORES: Juan Vela Garcia / Marta Frias Zapater
 * NIA: 643821 / 535621
 * FICHERO: Resultado.java
 * TIEMPO: 10 min
 * DESCRIPCION: Gestiona los resultados de una invocacion a un servidor de calculo remoto.
 */

package ssdd.p2.herramientas;

import java.util.ArrayList;

/**
 * Gestiona los resultados de una invocacion a un servidor de calculo remoto.
 *
 * @author Juan Vela y Marta Frias
 *
 */
public class Resultado {

    /** Listas de numeros primos (resultados de calculos separados) */
    private ArrayList<Integer> primos1;
    private ArrayList<Integer> primos2;

    /** Lista de resultados completa */
    private ArrayList<Integer> primos;

    /** Tiempo de ejecucion */
    private double tiempo;

    /**
     * Constructor
     */
    public Resultado() {
        primos1 = null;
        primos2 = null;
        primos = null;
        tiempo = 0;
    }

    /**
     * Devuelve el tiempo de ejecucion asociado
     * 
     * @return tiempo - tiempo de ejecucion en ms
     */
    public double getTiempo() {
        return tiempo;
    }

    /**
     * Establece el tiempo de ejecucion asociado
     * 
     * @param tiempo - tiempo de ejecucion en ms
     */
    public void setTiempo(double tiempo) {
        this.tiempo = tiempo;
    }

    /**
     * Establece el resultado del primer intervalo
     * 
     * @param primos1 - lista de primos
     */
    public void setResultado1(ArrayList<Integer> primos1) {
        this.primos1 = primos1;
    }

    /**
     * Establece el resultado del segundo intervalo
     * 
     * @param primos2 - lista de primos
     */
    public void setResultado2(ArrayList<Integer> primos2) {
        this.primos2 = primos2;
    }

    /**
     * Devuelve una lista de numeros primos
     * 
     * @return lista de primos
     */
    public ArrayList<Integer> getResultado() {

        // si no se habia generado antes
        if (primos == null) {

            // si hay 2 resultados que unir
            if (primos1 != null && primos2 != null) {

                int num = primos1.size() + primos2.size();
                primos = new ArrayList<Integer>(num);

                for (Integer primo : primos1) {
                    primos.add(primo);
                }

                for (Integer primo : primos2) {
                    primos.add(primo);
                }

                return primos;

            } else if (primos1 != null) {

                return primos1;

            } else if (primos2 != null) {

                return primos2;
            }
        }

        return primos;
    }

}
