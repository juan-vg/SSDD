/*
 * AUTORES: Juan Vela Garcia / Marta Frias Zapater
 * NIA: 643821 / 535621
 * FICHERO: GestorIntervalos.java
 * TIEMPO: 2 horas
 * DESCRIPCION: gestiona un intervalo de numeros enteros, permitiendo
 *  dividirlo en subintervalos 
 */

package ssdd.p2.herramientas;

/**
 * Gestiona un intervalo de numeros enteros, permitiendo obtener subintervalos
 * 
 * @author Juan Vela y Marta Frias
 * 
 */
public class GestorIntervalos {

    /** Intervalo inicial, introducido por el usuario */
    private Intervalo intervalo;

    /** Iteradores del intervalo */
    private int itSuperior;
    private int itInferior;

    /**
     * Crea un gestor de intervalos, encargado de subdividir el intervalo [i] en
     * subintervalos con el mismo numero de elementos [n]. Los subitervalos
     * devueltos estan compuestos a su vez por dos subintervalos, el primero con
     * los valores inferiores de la recta de los numeros naturales, y el segundo
     * con los valores superiores. Mediante esta distribucion la carga de
     * trabajo se reparte mas equitativamente entre todos los subintervalos
     * devueltos.
     * 
     * @param i Intervalo de numeros enteros (si se introduce una union de
     *            intervalos se tratara como un intervalo completo, es decir, se
     *            tendran en cuenta todos sus elementos desde [i.getMin()] hasta
     *            [i.getMax()]
     * @param n Rango maximo del subintervalo
     */
    public GestorIntervalos(Intervalo i) {
        intervalo = i;
        itInferior = intervalo.getMin();
        itSuperior = intervalo.getMax();
    }

    /**
     * Informa de si se ha terminado de recorrer el intervalo
     * 
     * @return true si se ha terminado de recorrer el intervalo
     */
    public boolean haAcabado() {
        return itInferior >= itSuperior;
    }

    /**
     * Devuelve un subintervalo con el numero de elementos especificado en el
     * gestor de intervalos. Si el numero de elementos especificado es impar se
     * devuelve un elemento menos. Si se llega al final del intervalo se
     * devuelve lo que quede. En el caso de que ya se haya devuelto todo el
     * intervalo inicial, se devuelve un intervalo nulo (null)
     * 
     * @return subintervalo de numeros enteros
     */
    public UnionIntervalos getSubIntervalo(int numElementos) {

        // mientras queden elementos por asignar
        if (!haAcabado()) {

            // obtener la mitad del numero de elementos para repartir los dos
            // intervalos de forma homogenea
            int mitadNumElem = numElementos / 2;

            UnionIntervalos subIntervalo = null;

            // si no quedan suficientes elementos
            // -> devuelve lo que quede
            if ((itInferior + mitadNumElem) >= (itSuperior - mitadNumElem)) {

                Intervalo inferior = new Intervalo(itInferior, itSuperior);

                Intervalo superior = null;

                subIntervalo = new UnionIntervalos(inferior, superior);

            }

            // si quedan suficientes elementos
            // -> devuelve el numero de elementos solicitados
            else {
                Intervalo inferior = new Intervalo(itInferior,
                        itInferior + (mitadNumElem - 1));

                Intervalo superior = new Intervalo(
                        itSuperior - (mitadNumElem - 1), itSuperior);

                subIntervalo = new UnionIntervalos(inferior, superior);
            }

            // actualiza iteradores
            itInferior = itInferior + mitadNumElem;
            itSuperior = itSuperior - mitadNumElem;

            return subIntervalo;
        } else {
            return null;
        }
    }
}
