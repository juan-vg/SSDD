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
 * Gestiona un intervalo de numeros enteros, permitiendo obtener subintervalos
 * 
 * @author Juan Vela y Marta Frias
 * 
 */
public class GestorIntervalos {

	public static void main(String[] args) {
		gestorDinamico1();
		System.out.println();
		
		//gestorDinamico2();
	}

	/** Intervalo inicial, introducido por el usuario */
	private Intervalo intervalo;

	/** Numero de elementos de los subintervalos devueltos */
	// private int numElementos;

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
	 * @param i
	 *            Intervalo de numeros enteros (si se introduce una union de
	 *            intervalos se tratara como un intervalo completo, es decir, se
	 *            tendran en cuenta todos sus elementos desde [i.getMin()] hasta
	 *            [i.getMax()]
	 * @param n
	 *            Rango maximo del subintervalo
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
	
	private static double dameTiempo(double factorPotencia){
		//Por ejemplo Tmedio de ejec = 5s = 5000ms
		
		// a mayor potencia, menos tiempo
		return 5000 * (1/factorPotencia);
	}

	private static void gestorDinamico1(){
    	Intervalo a = new Intervalo(1000, 1000000);
        GestorIntervalos gi = new GestorIntervalos(a);
        
        int NUM_WORKERS = 5;
        double[][] workers = new double[NUM_WORKERS][2];
        for(int i=0; i<NUM_WORKERS; i++){
        	workers[i][0] = 0.3 * ((double)i + 1.0);
        }
        double tiempoMedio = 0.0;
        int itWorker = 0;
        

        int numElems = 10000;

        int suma = 0;
        int primerNum = 0;

        double incr = 0.0;
        double incrMedio = 0.0;

        /**
         * A medida que se avanza en los subintervalos, el numero de iteraciones
         * crece. Para solucionarlo se incrementa el numero de elementos
         * descartados. Cuanto mas mayor, mas agresivo y mas bandazos da (al
         * intentar recuperarse mas rapido puede cometer mas error)
         */
        double factorDescuento = 0.5;

        // mientras quede intervalo
        while (!gi.haAcabado()) {

            System.out.print("numElements: " + numElems + "->");

            // descontar elementos en funcion del incremento medio
            if (incrMedio != 0.0) {
                numElems = numElems - (int) (numElems * (incrMedio / 100.0)
                        * (1.0 + factorDescuento));
            }

            System.out.println(numElems);

            Intervalo i = gi.getSubIntervalo(numElems);

            suma = 0;

            // calcular numero de iteraciones del subintervalo
            while (i.iteradorHaySiguiente()) {
                int it = i.iteradorSiguiente();

                if (it % 2 != 0 && it != 1) {
                    suma += Math.sqrt(it);
                }
            }
            
            workers[itWorker][1] = dameTiempo(workers[itWorker][0]);
            System.out.printf("Worker %d (potencia = %.2f) -> ",itWorker, workers[itWorker][0]);
            System.out.printf("Tiempo de ejecucion = %.2f ms\n",workers[itWorker][1]);
            if(tiempoMedio > 0.0){
            	System.out.printf("Tiempo de ejecucion medio = %.2f ms ",tiempoMedio);
            	double incrTiempo = (workers[itWorker][1] - tiempoMedio) * 100.0;
            	System.out.printf("(incremento = %.5f%%)\n",incrTiempo);
            }
            itWorker++;
            if(itWorker >= NUM_WORKERS){
            	itWorker = 0;
            	for(int j = 0; j < NUM_WORKERS; j++){
            		tiempoMedio += workers[j][1];
            	}
            	tiempoMedio /= (double)NUM_WORKERS;
            }

            // obtiene el primer numero de iteraciones, ajustando todos los
            // siguientes con respecto a el (modificando el incremento medio)
            if (primerNum == 0) {
                primerNum = suma;
                incr = 0.0;

            } else if (primerNum > 0) {
                
                incr = (1.0 - (double) primerNum / (double) suma) * 100.0;
                
                if (incrMedio == 0.0) {
                    incrMedio = incr;
                } else {
                    incrMedio = (incr + incrMedio) / 2;
                }

            }

            System.out.printf("Num Iteraciones = %d (incremento = %.5f%%)\n",
                    suma, incr);
            System.out.printf("Incremento medio = %.5f\n", incrMedio);
            System.out.println(
                    "-------------------------------------------------------");
        }
    }
	
	/*private static void gestorDinamico1(){
    	Intervalo a = new Intervalo(1000, 1000000);
        GestorIntervalos gi = new GestorIntervalos(a);
        
        

        int numElems = 10000;

        int suma = 0;
        int primerNum = 0;

        double incr = 0.0;
        double incrMedio = 0.0;

        /**
         * A medida que se avanza en los subintervalos, el numero de iteraciones
         * crece. Para solucionarlo se incrementa el numero de elementos
         * descartados. Cuanto mas mayor, mas agresivo y mas bandazos da (al
         * intentar recuperarse mas rapido puede cometer mas error)
         */
        /*double factorDescuento = 0.5;

        // mientras quede intervalo
        while (!gi.haAcabado()) {

            System.out.print("numElements: " + numElems + "->");

            // descontar elementos en funcion del incremento medio
            if (incrMedio != 0.0) {
                numElems = numElems - (int) (numElems * (incrMedio / 100.0)
                        * (1.0 + factorDescuento));
            }

            System.out.println(numElems);

            Intervalo i = gi.getSubIntervalo(numElems);

            suma = 0;

            // calcular numero de iteraciones del subintervalo
            while (i.iteradorHaySiguiente()) {
                int it = i.iteradorSiguiente();

                if (it % 2 != 0 && it != 1) {
                    suma += Math.sqrt(it);
                }
            }

            // obtiene el primer numero de iteraciones, ajustando todos los
            // siguientes con respecto a el (modificando el incremento medio)
            if (primerNum == 0) {
                primerNum = suma;
                incr = 0.0;

            } else if (primerNum > 0) {
                
                incr = (1.0 - (double) primerNum / (double) suma) * 100.0;
                
                if (incrMedio == 0.0) {
                    incrMedio = incr;
                } else {
                    incrMedio = (incr + incrMedio) / 2;
                }

            }

            System.out.printf("Num Iteraciones = %d (incremento = %.5f%%)\n",
                    suma, incr);
            System.out.printf("Incremento medio = %.5f\n", incrMedio);
            System.out.println(
                    "-------------------------------------------------------");
        }
    }*/
	
	private static void gestorDinamico2(){
    	Intervalo a = new Intervalo(1000, 1000000);
        GestorIntervalos gi = new GestorIntervalos(a);

        int numElems = 10000;

        int suma = 0;
        int primerNum = 0;

        double incr = 0.0;
        double incrMedio = 0.0;

        /**
         * A medida que se avanza en los subintervalos, el numero de iteraciones
         * crece. Para solucionarlo se incrementa el numero de elementos
         * descartados. Cuanto mas mayor, mas agresivo y mas bandazos da (al
         * intentar recuperarse mas rapido puede cometer mas error)
         */
        double factorDescuento = 0.7;

        // mientras quede intervalo
        while (!gi.haAcabado()) {

            System.out.print("numElements: " + numElems + "->");

            // descontar elementos en funcion del incremento medio
            if (incrMedio != 0.0) {
                numElems = numElems - (int) (numElems * (incrMedio / 100.0)
                        * (1.0 + factorDescuento));
            }

            System.out.println(numElems);

            Intervalo i = gi.getSubIntervalo(numElems);

            suma = 0;

            // calcular numero de iteraciones del subintervalo
            while (i.iteradorHaySiguiente()) {
                int it = i.iteradorSiguiente();

                if (it % 2 != 0 && it != 1) {
                    suma += Math.sqrt(it);
                }
            }

            // obtiene el primer numero de iteraciones, ajustando todos los
            // siguientes con respecto a el (modificando el incremento medio)
            if (primerNum == 0) {
                primerNum = suma;
                incr = 0.0;

            } else if (primerNum > 0) {
                
                //incr = (1.0 - (double) primerNum / (double) suma) * 100.0;
                incr = (((double)(suma - primerNum)) / (double)primerNum) * 100.0;
                
                if (incrMedio == 0.0) {
                    incrMedio = incr;
                } else {
                    incrMedio = (incr + incrMedio) / 2;
                }

            }

            System.out.printf("Num Iteraciones = %d (incremento = %.5f%%)\n",
                    suma, incr);
            System.out.printf("Incremento medio = %.5f\n", incrMedio);
            System.out.println(
                    "-------------------------------------------------------");
        }
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
	public Intervalo getSubIntervalo(int numElementos) {

		if (!haAcabado()) {

			int mitadNumElem = numElementos / 2;

			Intervalo subIntervalo = null;

			// si no quedan suficientes elementos
			// -> devuelve lo que quede
			if ((itInferior + mitadNumElem) >= (itSuperior - mitadNumElem)) {

				subIntervalo = new Intervalo(itInferior, itSuperior);

			}

			// si quedan suficientes elementos
			// -> devuelve el numero de elementos solicitados
			else {
				Intervalo inferior = new Intervalo(itInferior, itInferior + (mitadNumElem - 1));

				Intervalo superior = new Intervalo(itSuperior - (mitadNumElem - 1), itSuperior);

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
