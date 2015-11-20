/*
 * AUTOR: Juan Vela y Marta Frias
 * NIA: 643821 - 535621
 * FICHERO: Launcher.java
 * TIEMPO: 4 horas
 * DESCRIPCION: Aplicacion distribuida que implementa el sistema de mensajes
 * diseñado en la clase MessageSystem
 */

package ssdd.p3.aplicacion;

import java.io.FileNotFoundException;

import ssdd.p3.ms.MessageSystem;
import ssdd.p3.ms.ProcessNotFoundException;
import ssdd.p3.ms.WrongFormatException;

/**
 * Clase que permite generar una simulacion de aplicacion distribuida que
 * implementa el sistema de mensajes diseñado en la clase MessageSystem. El
 * proceso con id == 1 se convierte en el gestor de un invernadero. El resto de
 * procesos se convierten en sensores que envian datos cada cierto tiempo a
 * gestor (proceso 1). Estas acciones se pueden entrelazar y conllevar a
 * resultados diferentes.
 *
 * @author Juan Vela
 * @author Marta Frias
 *
 */
public class Launcher implements Runnable {

    private static String[] valores = { "BAJA", "NORMAL", "ALTA" };

    /** Atributos */
    private int numProc;
    private String networkFile;
    private boolean debug;

    /**
     * Crea un nuevo objeto Launcher
     * 
     * @param numProc : Numero de proceso asignado
     * 
     * @param networkFile : Nombre del fichero en donde se encuentra la
     *            localizacion de cada proceso.
     * 
     * @param debug : Bandera de depuracion.
     */
    public Launcher(int numProc, String networkFile, boolean debug) {
        this.numProc = numProc;
        this.networkFile = networkFile;
        this.debug = debug;
    }

    /**
     * Gestiona y monitoriza las condiciones ambientales de un invernadero,
     * respondiendo activamente a variaciones en las mismas.
     * 
     * @param networkFile : Nombre del fichero en donde se encuentra la
     *            localizacion de cada proceso.
     * 
     * @param debug : Bandera de depuracion.
     */
    private static void gestorInvernadero(String networkFile, boolean debug) {

        // gestor invernadero
        // si temperatura elevada y humedad alta -> ventilar
        // si temperatura elevada y humedad no alta -> pulverizar
        // si pulverizando y humedad alta -> alerta + ventilar

        boolean temperaturaElevada = false;
        boolean humedadElevada = false;
        boolean pulverizando = false;

        try {
            MessageSystem ms = new MessageSystem(1, networkFile, debug);

            while(true){
                String info = (String) ms.receive().getPayload();

                if (info.equals("TEMPERATURA ALTA")) {
                    temperaturaElevada = true;
                    System.out.println("LA TEMPERATURA ES ALTA");
                    
                } else if (info.equals("HUMEDAD ALTA")) {
                    humedadElevada = true;
                    System.out.println("LA HUMEDAD ES ALTA");
                }

                if (temperaturaElevada && humedadElevada) {
                    System.out.println("SE HA ACTIVADO LA VENTILACION");
                    if (pulverizando) {
                        System.out.println("SE HA DETENIDO LA PULVERIZACION");
                        pulverizando = false;
                        alarmaInvernadero();
                    }
                } else if (temperaturaElevada && !pulverizando) {
                    System.out.println("SE HA ACTIVADO LA PULVERIZACION");
                    pulverizando = true;
                }
            }

        } catch (FileNotFoundException e) {
            System.err
                    .println("ERROR: El fichero " + networkFile + " no existe");

        } catch (ProcessNotFoundException e) {
            System.err.println("ERROR: " + e.getMessage());

        } catch (WrongFormatException e) {
            System.err.println("ERROR: " + e.getMessage());
        }

    }

    /**
     * Monitoriza la temperatura de un invernadero.
     * 
     * @param networkFile : Nombre del fichero en donde se encuentra la
     *            localizacion de cada proceso.
     * 
     * @param debug : Bandera de depuracion.
     */
    private static void sensorTemperatura(String networkFile, boolean debug) {
        // envia temperatura a gestor (baja, normal, alta)
        try {
            MessageSystem ms = new MessageSystem(2, networkFile, debug);

            for (int i = 0; i < 3; i++) {
                ms.send(1, "TEMPERATURA " + valores[i]);
                hacerAlgo();
            }

        } catch (FileNotFoundException e) {
            System.err
                    .println("ERROR: El fichero " + networkFile + " no existe");

        } catch (ProcessNotFoundException e) {
            System.err.println("ERROR: " + e.getMessage());

        } catch (WrongFormatException e) {
            System.err.println("ERROR: " + e.getMessage());
        }
    }

    /**
     * Monitoriza la humedad de un invernadero.
     * 
     * @param networkFile : Nombre del fichero en donde se encuentra la
     *            localizacion de cada proceso.
     * 
     * @param debug : Bandera de depuracion.
     */
    private static void sensorHumedad(String networkFile, boolean debug) {
        // envia humedad a gestor (baja, normal, alta)

        try {
            MessageSystem ms = new MessageSystem(3, networkFile, debug);

            for (int i = 0; i < 3; i++) {
                ms.send(1, "HUMEDAD " + valores[i]);
                hacerAlgo();
            }

        } catch (FileNotFoundException e) {
            System.err
                    .println("ERROR: El fichero " + networkFile + " no existe");

        } catch (ProcessNotFoundException e) {
            System.err.println("ERROR: " + e.getMessage());

        } catch (WrongFormatException e) {
            System.err.println("ERROR: " + e.getMessage());
        }
    }

    /**
     * Simula la ejecucion de una tarea durante un tiempo aleatorio. Detiene la
     * ejecucion un numero aleatorio de milisegundos en el intervalo (0, 10000).
     */
    private static void hacerAlgo() {
        try {
            //Thread.sleep((int) (Math.random() * 1000.0));
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void alarmaInvernadero() {
        System.out.println("AVISO: La cosecha esta en peligro!!!");
    }

    /**
     * Metodo principal que tras comprobar la correccion de los argumentos, si
     * el identificador de proceso es 1 inicia el gestor, si no inicia un
     * cliente.
     * 
     */
    public static void main(String[] args) {

        boolean debug = false;
        String networkFile = "peers.txt";

        if (args.length >= 1) {

            // identificar parametros
            if (args[0].equals("-d")) { // debug

                debug = true;
                networkFile = args[1];
            } else {
                networkFile = args[0];
            }

            // lanzar procesos
            for (int numProc = 1; numProc <= 3; numProc++) {

                Launcher l = new Launcher(numProc, networkFile, debug);

                Thread t = new Thread(l);
                t.start();
            }
        } else {
            System.err.println("Syntax: [-d] network_file");
        }
    }

    @Override
    public void run() {
        if(numProc == 1){
            gestorInvernadero(networkFile, debug);
        } else if (numProc == 2){
            sensorHumedad(networkFile, debug);
        } else if (numProc == 3){
            sensorTemperatura(networkFile, debug);
        }

    }

}
