package ssdd.p1.servidor;

public class Lanzador {

    public static void main(String[] args) {
        // TODO thread que escuche teclado, para cerrar servidor

        // si se recibe el numero adecuado de parametros
        if (args.length == 2) {

            try {

                // obtener el puerto en el que debe escuchar el servidor
                int puerto = Integer.parseInt(args[1]);

                // determinar el modo de funcionamiento del servidor

                // con un thread por cada cliente
                if (args[0].equals("-t")) {
                    ServidorThreadLanzador.iniciar(puerto);

                    // con un selector sobre todos los clientes
                } else if (args[0].equals("-s")) {
                    ServidorSelector.iniciar(puerto);

                } else {
                    System.err.println("ERROR: Opcion no valida.");
                }

            } catch (NumberFormatException e) {
                System.err.println("ERROR: El puerto no es valido.");
            }

        } else {
            System.err.println("ERROR: Numero de parametros incorrecto.");
        }
    }
}
