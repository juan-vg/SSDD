/*
 * AUTORES: Juan Vela Garcia / Marta Frias Zapater
 * NIA: 643821 / 535621
 * FICHERO: GestorIntervalos.java
 * TIEMPO: 1 hora
 * DESCRIPCION: gestiona un intervalo de numeros enteros, permitiendo
 *  dividirlo en subintervalos 
 */

package ssdd.p2;

/**
 * Gestiona un intervalo de numeros enteros, permitiendo obtener
 * subintervalos
 * 
 * @author Juan Vela y Marta Frias
 * 
 */
public class GestorIntervalos {
	
    /** Intervalo*/
	private Intervalo intervalo;
	
	/** Rango de los subintervalos*/
	private int num;

    
    /**
     * Metodo constructor
     * 
     * @param i  Intervalo de numeros enteros
     * @param n Rango maximo del subintervalo
     */
    public GestorIntervalos(Intervalo i, int n) {
        intervalo = i;
        num = n;
    }
	
    /**
     * Informa de si se ha terminado de recorrer el intervalo
     * 
     * @return true si se ha terminado de recorrer el intervalo
     */
    public boolean haAcabado() {
        return intervalo.getMin() >= intervalo.getMax();
    }
	
	/**
     * Devuelve un subintervalo con el rango especificado en el gestor
     * de intervalos, excepto si se llega al final del intervalo
     * 
     * @return subintervalo de numeros enteros
     */
	public Intervalo getSubIntervalo() {
        
        if (!haAcabado()) {
            Intervalo subIntervalo;
            
            if ((intervalo.getMin() + num) >= intervalo.getMax()) {
                subIntervalo = new Intervalo(intervalo.getMin(),
                        intervalo.getMax());
                
            } else {
                subIntervalo = new Intervalo(intervalo.getMin(),
                        intervalo.getMin() + num - 1);
            }
            
            intervalo.setMin(intervalo.getMin() + num);
            
            return subIntervalo;
        } else {
            return null;
        }
    }
}
