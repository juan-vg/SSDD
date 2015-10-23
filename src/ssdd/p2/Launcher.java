/*
 * AUTORES: Juan Vela Garcia / Marta Frias Zapater
 * NIA: 643821 / 535621
 * FICHERO: Launcher.java
 * TIEMPO: 11 horas
 * DESCRIPCION: Permite construir la infraestructura necesaria para que un 
 * cliente pueda determinar los numeros primos de un cierto 
 * intervalo, dividiendolo en subintervalos y preguntando de 
 * manera concurrente a tantos servidores de calculo como indique.
 */


package ssdd.p2;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Permite construir la infraestructura necesaria para que un cliente
 * pueda determinar los numeros primos de un cierto intervalo,
 * dividiendolo en subintervalos y preguntando de manera concurrente a
 * tantos servidores de calculo como indique.
 * 
 * La sintaxis es la siguiente:
 *      -c [ipRegistroRMI]          //servidor de calculo
 *      -a [ipRegistroRMI]          //servidor de asignacion
 *      -u min max numServidores  [ipRegistroRMI]       //cliente
 * 
 * Cada servidor de calculo obtiene los primos en un intervalo dado.
 * El servidor de asignacion encuentra (busca en el registro RMI)
 * tantos servidores de calculo como le pide el cliente. El cliente
 * proporciona el minimo y el maximo de un intervalo, y el numero de
 * servidores de calculo que necesita para obtener el resultado.
 * 
 * @author Juan Vela y Marta Frias
 * 
 */
public class Launcher {
	
    /**
     * Devuelve una cadena de texto con la sintaxis de las acciones
     * disponibles
     * 
     * @return sintaxis de las acciones disponibles
     */
	private static String sintaxisParams(){
	    
		String sintaxis = "Opciones: \n";
		sintaxis += "-c [ipRegistroRMI]\n";
		sintaxis += "-a [ipRegistroRMI]\n";
		sintaxis += "-u min max numServidores  [ipRegistroRMI]\n";
		
		return sintaxis;
	}
	
	/**
	 *  Cliente que calcula los numeros primos que hay en un intervalo de
	 *  enteros [min,max], a traves de un numero [n] de servidores de calculo y 
	 *  los muestra por pantalla.
	 *  
	 * @param min minimo entero del intervalo
	 * @param max maximo entero del intervalo
	 * @param n numero de servidores de calculo
	 * @param ipRegistro direccion ip donde se encuentra el servidor de registro
	 */
	private static void crearCliente(int min, int max, int n, String ipRegistro){
		
	    /** Rango maximo del subintervalo*/
		final int MAX_TROZO = 5;
		
		int rangoMax = ((max - min) + 1) / n;
		int rango;
		
		if(rangoMax >= MAX_TROZO){
			rango = MAX_TROZO;
		}
		else if (rangoMax > 0){
			rango = rangoMax;
		}
		else{
			rango = 1;
		}
		
		GestorIntervalos intervalo = new GestorIntervalos(new Intervalo(min, max), rango);	
		LinkedList<Integer> listaPrimos = new LinkedList<Integer>();
	
		try{
		    
		    //contacta con el registro RMI y obtiene la referencia a un
		    //servidor de asignaciom
			Registry registry = LocateRegistry.getRegistry(ipRegistro);
			WorkerFactory wf = (WorkerFactory) registry.lookup("WorkerFactoryServer");
			
			//solicita servidores de calculo
			ArrayList<Worker> workers = wf.dameWorkers(n);
			
			//n almacena el numero de workers devueltos
			//(puede ocurrir que haya menos de los que se piden)
			n = workers.size();
			ClienteThread[] cT = new ClienteThread[n];
			Thread[] t = new Thread[n];
			
			int pendientesPorLeer = 0;
			LinkedList<Intervalo> pendientesFallidos = new LinkedList<Intervalo>();
			LinkedList<Worker> workersFallidos = new LinkedList<Worker>();
			
			int i = 0;
			for(Worker w : workers){
				if(!intervalo.haAcabado()){
					Intervalo subIntervalo = intervalo.getSubIntervalo();
					cT[i] = new ClienteThread(w, subIntervalo);
					t[i] = new Thread(cT[i]);
					t[i].run();
					i++;
				} else{
					// si pide mas workers que elementos en el intervalo,
					// los que sobren se quedan sin hacer nada
				}
			}
			
			pendientesPorLeer = i;
			
			//n almacena el numero de workers empleados
			//(puede ocurrir que haya workers sin trabajo asignado)
			n = i;
			
			i = 0;
			boolean fin = false, error = false;
			
			while(!fin){
			    
				//si alguno acaba, mandar mas trabajo si queda
				if(cT[i].haAcabado() && !cT[i].atendido()){
				    
					//almacenar resultado
					ArrayList<Integer> parcial = cT[i].resultado();
					cT[i].setAtendido();
					for(Integer p : parcial){
						listaPrimos.add(p);
					}
					pendientesPorLeer--;
					
					if(!intervalo.haAcabado()){
					    
					    //mandar mas trabajo si queda
						Intervalo subIntervalo = intervalo.getSubIntervalo();
						cT[i].setIntervalo(subIntervalo);
						t[i].run();
						pendientesPorLeer++;
					} else if(pendientesFallidos.size() > 0){
					    
					    //si alguno ha fallado, añadir a la cola de pendientes
						Intervalo subIntervalo = pendientesFallidos.getFirst();
						pendientesFallidos.removeFirst();
						cT[i].setIntervalo(subIntervalo);
						t[i].run();
					}
					
				} else if(cT[i].haFallado() && !cT[i].atendido()){
				    
					//worker caido
					cT[i].setAtendido();
					pendientesFallidos.addLast(cT[i].getIntervalo());
					workersFallidos.add(cT[i].getWorker());
				}
				
				i = (i+1) % n;
				if(pendientesPorLeer == 0){
					fin = true;
					
				} else if(workersFallidos.size() == n){
				    error=true;
					fin = true;
					System.err.println("ERROR: conexion perdida con TODOS los workers.");
					
				} else{
				    
					//Si se sigue trabajando, forzar cambio de contexto
					//(permitir concurrencia)
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {}
				}
			}
			if (!error) {
                Collections.sort(listaPrimos);
                
                System.out.printf("Se han encontrado %d primos"
                        + " en el intervalo (%d, %d).\n", listaPrimos
                        .size(), min, max);
                for (Integer p : listaPrimos) {
                    System.out.println(p);
                }
            }
		} catch (Exception e) {
            System.err.println("ERROR: " + e.toString());
        }
	}
	
	/**
	 * Crea y registra un servidor de asignacion en el registro RMI
	 * 
	 * @param ipRegistro direccion ip donde se encuentra el servidor
	 *      de registro
	 */
	private static void crearServidorAsignacion(String ipRegistro) {
		try {
			WorkerFactoryServer wfs = new WorkerFactoryServer(ipRegistro);

            Registry registry = LocateRegistry.getRegistry(ipRegistro);
            String nombre = "WorkerFactoryServer";
            registry.rebind(nombre, wfs);
            
			System.out.printf("Servidor de asignacion (%s)" +
					" registrado con exito.\n",	nombre);

        } catch (Exception e) {
            System.err.println("ERROR: " + e.toString());
        }
	}

	/**
     * Crea y registra un servidor de calculo en el registro RMI,
     * teniendo en cuenta para la eleccion del nombre los que ya han
     * sido registrados.
     * 
     * @param ipRegistro direccion ip donde se encuentra el servidor de
     *            registro
     */
	private static void crearServidorCalculo(String ipRegistro) {
		try {
            WorkerServer ws = new WorkerServer();
            
            Registry registry = LocateRegistry.getRegistry(ipRegistro);    
            String nombre = "Worker";
            boolean registrado = false;
            String[] nombres = registry.list();
            
            if(nombres.length > 1){
            	Arrays.sort(nombres);
            }
            int i = nombres.length-1;
            
            final Pattern wPattern = Pattern.compile("Worker\\d+");
            
            while(!registrado && i >= 0){
            	Matcher matcher = wPattern.matcher(nombres[i]);
                if (matcher.matches()) {            		
            		String num = nombres[i].substring(6);
            		String nuevoNum = "" + (Integer.parseInt(num) + 1);
            		
            		if(num.length() >= nuevoNum.length()){
            			nombre += nombres[i].substring(6, 
            					nombres[i].length() - nuevoNum.length());
            		}
            		nombre += nuevoNum;
            		
            		registry.bind(nombre, ws);
            		registrado = true;
            		
					System.out.printf("Servidor de calculo (%s)" +
							" registrado con exito.\n",	nombre);
            	} else{
            		i--;
            	}
            }
            
            if(!registrado){
                
            	//no hay workers registrados previamente
            	nombre += "00000";
        		registry.bind(nombre, ws);
        		
				System.out.printf("Servidor de calculo (%s)" +
						" registrado con exito.\n",	nombre);
            }
        } catch (Exception e) {
            System.err.println("ERROR: " + e.toString());
        }
	}

	public static void main(String[] args) {

		if (args.length > 0) {
			
			String opcion = args[0];
			
			if (opcion.equals("-c")) {			    
				String host = null;	
				
				if(args.length <= 2){				    
					if(args.length == 2){
						host = args[1];
					}
					crearServidorCalculo(host);
					
				} else if(args.length > 2){				    
					System.err.println("ERROR: Numero de parametros incorrecto.");
					System.err.printf(sintaxisParams());
				}
			} else if (opcion.equals("-a")) {			 
				String host = null;
				
				if(args.length <= 2){
					if(args.length == 2){
						host = args[1];
					}
					crearServidorAsignacion(host);
					
				} else if(args.length > 2){
					System.err.println("ERROR: Numero de parametros incorrecto.");
					System.err.printf(sintaxisParams());
				}				
			} else if (opcion.equals("-u")) {
			    
				if(args.length == 4 || args.length == 5){
					try{
						String host = null;
						int min = Integer.parseInt(args[1]);
						int max = Integer.parseInt(args[2]);
						int n = Integer.parseInt(args[3]);
						
						if(args.length == 5){
							host = args[4];
						}
						if(min < max){					    
							crearCliente(min, max, n, host);							
						} else{
							System.err.println("ERROR: sintaxis incorrecta. (min >= max)");
							System.err.printf(sintaxisParams());
						}
						
					} catch(NumberFormatException e){
						System.err.println("ERROR: sintaxis incorrecta.");
						System.err.printf(sintaxisParams());
					}
				} else{
					System.err.println("ERROR: Numero de parametros incorrecto.");
					System.err.printf(sintaxisParams());
				}
				
			} else{
				System.err.println("ERROR: sintaxis incorrecta.");
				System.err.printf(sintaxisParams());
			}
		} else {
			System.err.println("ERROR: sintaxis incorrecta.");
			System.err.printf(sintaxisParams());
		}
	}
}
