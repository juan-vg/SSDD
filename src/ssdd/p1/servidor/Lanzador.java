/*
 * AUTORES: Juan Vela Garcia / Marta Frias Zapater
 * NIA: 643821 / 535621
 * FICHERO: Lanzador.java
 * TIEMPO: 30 minutos
 * DESCRIPCION: Lanzador de un servidor web (HTTP) con dos implementaciones:
 *  una usando hilos y otra utilizando un Selector.
 */

package ssdd.p1.servidor;

/**
 * Lanzador de un servidor web (HTTP) con dos implementaciones: una usando hilos
 * y otra utilizando un Selector.
 * 
 * @author Juan Vela, Marta Frias
 *
 */
public class Lanzador {

    /**
     * Imprime por pantalla las opciones de ejecucion disponibles.
     * 
     */
    private static void uso() {
        System.out.println("Uso: <modo> <puerto>");
        System.out.println("modo:");
        System.out.println("-t : usando threads");
        System.out.println("-s : usando un selector");
    }

    /**
     * En funcion de los parametros introducidos, se inicia una u otra version
     * del servidor HTTP (hilos o selector). Si surge algun error se informa de
     * ello por pantalla, adjuntando unas breves instrucciones de uso.
     * 
     */
    public static void main(String[] args) {
        // TODO thread que escuche teclado, para cerrar servidor

        // si se recibe el numero adecuado de parametros
        if (args.length == 2) {

            try {

                // obtener el puerto en el que debe escuchar el servidor
                int puerto = Integer.parseInt(args[1]);

                // determinar el modo de funcionamiento del servidor:

                // con un hilo por cada cliente
                if (args[0].equals("-t")) {
                    System.out.println("Iniciando servidor en modo thread");
                    ServidorHilosLanzador.iniciar(puerto);
                }
                // con un selector sobre todos los clientes
                else if (args[0].equals("-s")) {
                    System.out.println("Iniciando servidor en modo selector");
                    ServidorSelector.iniciar(puerto);
                }
                // error en cualquier otro caso
                else {
                    System.err.println("ERROR: Opcion no valida.");
                    uso();
                }
            }
            // si el puerto no es un numero
            catch (NumberFormatException e) {
                System.err.println("ERROR: El puerto no es valido.");
                uso();
            }
        }
        // si se introducen mas o menos de dos parametros
        else {
            System.err.println("ERROR: Numero de parametros incorrecto.");
            uso();
        }
    }
}
