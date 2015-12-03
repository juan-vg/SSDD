package ssdd.p4.ms;

/**
 * Se lanza cuando un proceso encuentra un error en el fichero de red.
 * 
 * @author Juan Vela
 * @author Marta Frias
 * 
 */
public class WrongFormatException extends Exception{
	private static final long serialVersionUID = 1L;

	/**
	 * Se lanza cuando un proceso encuentra un error en el fichero de red.
	 * 
	 * @param msg Mensaje de error.
	 */
	public WrongFormatException(String msg){
		super(msg);
	}
}
