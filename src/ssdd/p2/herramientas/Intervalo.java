/*
 * AUTORES: Juan Vela Garcia / Marta Frias Zapater
 * NIA: 643821 / 535621
 * FICHERO: Intervalo.java
 * TIEMPO: 10 min
 * DESCRIPCION: define un intervalo de numeros enteros entre un 
 * minimo y un maximo
 */

package ssdd.p2.herramientas;

/**
 * Define un intervalo de numeros enteros.
 * 
 * @author Juan Vela y Marta Frias
 * 
 */
public class Intervalo{

    /** Minimo entero del intervalo*/
	private int min;
	
	/** Maximo entero del intervalo*/
	private int max;

	
	/**
     * Metodo constructor
     * 
     * @param m minimo del intervalo
     * @param M maximo del intervalo
     */
    public Intervalo(int m, int M) {
        min = m;
        max = M;
    }
	
	/**
     * Obtiene el minimo del intervalo
     * 
     * @return minimo entero del intervalo
     */
    public int getMin() {
        return min;
    }
    
    /**
     * Obtiene el maximo del intervalo
     * 
     * @return maximo entero del intervalo
     */
    public int getMax() {
        return max;
    }
}
