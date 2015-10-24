/*
 * AUTORES: Juan Vela Garcia / Marta Frias Zapater
 * NIA: 643821 / 535621
 * FICHERO: UnionIntervalos.java
 * TIEMPO: 10 min
 * DESCRIPCION: define la union de dos Intervalos
 */

package ssdd.p2;

/**
 * Define la union de dos intervalos de numeros enteros.
 * 
 * @author Juan Vela y Marta Frias
 * 
 */
public class UnionIntervalos extends Intervalo{

    /** Intervalo A */
	private Intervalo intervaloA;
	
	/** Intervalo B */
	private Intervalo intervaloB;
	
	/**
     * Crea la union de dos intervalos de numeros enteros
     * 
     * @param a intervalo cuyos valores son menores que los de [b]
     * @param b intervalo cuyos valores son mayores que los de [a]
     */
    public UnionIntervalos(Intervalo a, Intervalo b) {
    	super(a.getMin(), b.getMax());
    	intervaloA = a;
    	intervaloB = b;
    }

	/**
	 * @return the intervaloA
	 */
	public Intervalo getIntervaloA() {
		return intervaloA;
	}

	/**
	 * @return the intervaloB
	 */
	public Intervalo getIntervaloB() {
		return intervaloB;
	}
	
	@Override
    public int iteradorSiguiente(){
        
        if(iteradorHaySiguiente()){
            
            int iterador = getIterador();
            
            if(iterador == intervaloA.getMax()){
                setIterador(intervaloB.getMin());
            } else {
                super.iteradorSiguiente();
            }
            
            return iterador;
            
        } else {
            return -1;
        }
        
    }
	
    @Override
    public String toString() {
        String respuesta = "";
        
        iteradorIniciar();
        
        while (iteradorHaySiguiente()){
            respuesta += iteradorSiguiente() + ",";
        }
        
        return respuesta;
    }
}
