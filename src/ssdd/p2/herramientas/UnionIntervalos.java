/*
 * AUTORES: Juan Vela Garcia / Marta Frias Zapater
 * NIA: 643821 / 535621
 * FICHERO: UnionIntervalos.java
 * TIEMPO: 10 min
 * DESCRIPCION: define la union de dos Intervalos
 */

package ssdd.p2.herramientas;

/**
 * Define la union de dos intervalos de numeros enteros.
 * 
 * @author Juan Vela y Marta Frias
 * 
 */
public class UnionIntervalos {

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
    public String toString() {
        if(intervaloA != null && intervaloB != null){
            return String.format("([%d, %d], [%d, %d])", intervaloA.getMin(),
                    intervaloA.getMax(), intervaloB.getMin(), intervaloB.getMax());
        } else {
            return String.format("([%d, %d], [null])", intervaloA.getMin(),
                    intervaloA.getMax());
        }
        
    }

}
