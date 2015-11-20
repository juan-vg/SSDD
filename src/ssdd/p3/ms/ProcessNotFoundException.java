package ssdd.p3.ms;

/**
 * Se lanza cuando un proceso no se encuentra en el fichero de red.
 * 
 */
public class ProcessNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Se lanza cuando un proceso no se encuentra en el fichero de red.
     * 
     * @param msg Mensaje de error.
     * 
     */
    public ProcessNotFoundException(String msg) {
        super(msg);
    }
}
