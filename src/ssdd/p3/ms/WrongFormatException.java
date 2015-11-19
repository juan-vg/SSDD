package ssdd.p3.ms;

/**
 * Se lanza cuando un proceso encuentra un error en el fichero de red.
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
