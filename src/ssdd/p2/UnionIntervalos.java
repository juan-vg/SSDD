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
     * Metodo constructor
     * 
     * @param m minimo del intervalo
     * @param M maximo del intervalo
     */
    public UnionIntervalos(Intervalo a, Intervalo b) {
    	super(a.getMin(), b.getMax());
    	intervaloA = a;
    	intervaloB = b;
    }
    
	/**
     * Modifica el minimo del intervalo
     * 
     * @param m nuevo minimo del intervalo
     */
    @Override
    public void setMin(int m) {
    	//TODO: comprobar si es correcto el nuevo min
        intervaloA.setMin(m);
    }
    
    /**
     * Modifica el maximo del intervalo
     * 
     * @param M nuevo maximo del intervalo
     */
    @Override
    public void setMax(int M) {
    	//TODO: comprobar si es correcto el nuevo min
        intervaloB.setMax(M);
    }

	/**
	 * @return the intervaloA
	 */
	public Intervalo getIntervaloA() {
		return intervaloA;
	}

	/**
	 * @param intervaloA the intervaloA to set
	 */
	public void setIntervaloA(Intervalo intervaloA) {
		this.intervaloA = intervaloA;
	}

	/**
	 * @return the intervaloB
	 */
	public Intervalo getIntervaloB() {
		return intervaloB;
	}

	/**
	 * @param intervaloB the intervaloB to set
	 */
	public void setIntervaloB(Intervalo intervaloB) {
		this.intervaloB = intervaloB;
	}

}
