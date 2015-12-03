package ssdd.p4.ms;

/**
 * Se lanza cuando un proceso se encuentra en el fichero de red
 * pero no hay ningun otro proceso en ese mismo fichero.
 * 
 * @author Juan Vela
 * @author Marta Frias
 * 
 */
public class AloneProcessException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * Se lanza cuando un proceso se encuentra en el fichero de red 
	 * pero no hay ningun otro proceso en ese mismo fichero
	 * 
	 * @param msg Mensaje de error.
	 * 
	 */
	public AloneProcessException(String msg){
		super(msg);
	}
}

