/*
 * AUTORES: Juan Vela Garcia / Marta Frias Zapater
 * NIA: 643821 / 535621
 * FICHERO: Utiles.java
 * TIEMPO: 7 horas
 * DESCRIPCION: Informacion y herramientas asociadas a una conexion HTTP
 */

package ssdd.p1.herramientas;

import java.io.File;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Clase que gestiona objetos Utiles. Proporcionan operaciones comunes a varias
 * implementaciones y pueden almacenar informacion asociada a una conexion.
 * 
 * @author Juan Vela, Marta Frias
 *
 */
public class Utiles {

    /**
     * Atributo que almacena un objeto Pattern. Se emplea para verificar que la
     * ruta al fichero solicitado cumple el requisito de estar en la misma
     * localizacion que el servidor
     */
    public static final Pattern patronRutaFichero = Pattern
            .compile("[/]?[a-zA-Z0-9_-]+(.[a-zA-Z0-9]+)?");

    /**
     * Atributo que almacena un objeto HTTPParser. Solo se utiliza cuando el
     * servidor funciona en modo selector (no bloqueante)
     */
    private NonBlockingHTTPParser analizador;

    /** Atributo que almacena un objeto ByteBuffer */
    private ByteBuffer bufer;

    /** Atributo que almacena un objeto Scanner. */
    private Scanner lector;

    /** Atributo que almacena un objeto String */
    private String porcionFichero;

    /** Atributo que almacena un objeto String */
    private String respuesta;

    /**
     * Metodo constructor de la clase. Crea un objeto con los atributos vacios.
     */
    public Utiles() {
        analizador = null;
        bufer = null;
        lector = null;
        porcionFichero = "";
        respuesta = null;
    }

    /**
     * Metodo que actualiza la referencia del atributo parser
     * 
     * @param p Objeto HTTPParser
     */
    public void setAnalizador(NonBlockingHTTPParser p) {
        analizador = p;
    }

    /**
     * Metodo que actualiza la referencia del atributo buf
     * 
     * @param b Objeto ByteBufer
     */
    public void setBuffer(ByteBuffer b) {
        bufer = b;
    }

    /**
     * Metodo que actualiza la referencia del atributo file
     * 
     * @param f Objeto Scanner que hace referencia a un fichero
     */
    public void setLector(Scanner f) {
        lector = f;
    }

    /**
     * Metodo que actualiza la referencia del atributo string
     * 
     * @param s Cadena de texto
     */
    public void setRespuesta(String s) {
        respuesta = s;
    }

    /**
     * Metodo que devuelve el valor del atributo parser
     * 
     * @return HTTPParser asociado al objeto Utils
     */
    public NonBlockingHTTPParser getAnalizador() {
        return analizador;
    }

    /**
     * Metodo que devuelve el valor del atributo buf
     * 
     * @return ByteBuffer asociado al objeto Utils
     */
    public ByteBuffer getBuffer() {
        return bufer;
    }

    /**
     * Metodo que devuelve el valor del atributo str
     * 
     * @return String asociado al objeto Utils
     */
    public String getRespuesta() {
        return respuesta;
    }

    /**
     * Metodo que devuelve true si y solo si el atributo str es diferente de
     * null.
     * 
     * @return true si el atributo str no es null
     */
    public boolean isSetRespuesta() {
        return respuesta != null;
    }

    /**
     * Metodo que devuelve true si y solo si el atributo file es diferente de
     * null.
     * 
     * @return true si el atributo file no es null
     */
    public boolean isSetLector() {
        return lector != null;
    }

    /**
     * Metodo que devuelve true si se ha llegado al final del fichero asociado
     * al atributo file. Si el atributo file esta a null se interpreta como que
     * esta finalizado
     * 
     * @return true si el ha llegado al final del fichero asociado al atributo
     *         file
     */
    private boolean isLectorFinalizado() {

        // si se ha inicializado un lector
        if (isSetLector()) {

            // queda contenido por devolver?
            return !lector.hasNextLine();
        } else {

            // si no se ha inicializado un lector es equivalente
            // a haber finalizado
            return true;
        }
    }

    /**
     * Metodo que comprueba si queda informacion por escribir del cuerpo de la
     * respuesta
     * 
     * @return true si queda informacion por escribir
     */
    public boolean isCuerpo() {

        // si se va a devolver una respuesta en
        // varias partes (cabecera + fichero)
        if (isSetRespuesta() && isSetLector()) {

            // queda contenido por devolver?
            return (respuesta.length() > 0) || (!isLectorFinalizado())
                    || (porcionFichero.length() > 0);
        }
        // si se va a devolver una unica respuesta
        else if (isSetRespuesta()) {

            // queda contenido por devolver?
            return respuesta.length() > 0;
        }
        // si no hay nada que devolver
        else {
            return false;
        }

    }

    /**
     * Metodo que devuelve una cadena de texto de longitud maxima [max]
     * correspondiente a parte del cuerpo de la respuesta
     * 
     * @param max Longitud maxima de la cadena a devolver
     * @return Cadena de texto correspondiente a parte del cuerpo de la
     *         respuesta
     */
    public String getCuerpo(int maxLong) {

        String parteRespuesta = "";

        // si ya se ha establecido una respuesta
        // -> devolver respuesta
        if (isSetRespuesta() && !respuesta.equals("")) {

            // y ademas su longitud no supera el limite [maxLong]
            // -> devuelve todo su contenido
            if (respuesta.length() <= maxLong) {
                parteRespuesta = new String(respuesta);
                respuesta = "";
                return parteRespuesta;
            }
            // pero su longitud supera el limite [maxLong]
            // -> devuelve una parte de su contenido
            else {
                parteRespuesta = respuesta.substring(0, maxLong);
                respuesta = respuesta.substring(maxLong);
                return parteRespuesta;
            }
        }

        // o bien, si no se ha establecido una respuesta (o ya ha sido devuelta)
        // y ademas existe un lector de ficheros
        // -> devolver fichero
        if (isSetLector()) {

            // si no hay contenido leido que devolver, pero queda por leer
            if (porcionFichero.equals("") && !isLectorFinalizado()) {
                porcionFichero = lector.nextLine() + "\n";
            }

            // si hay contenido que devolver
            if (!porcionFichero.equals("")) {

                // y ademas su longitud no supera el limite [maxLong]
                // -> devuelve todo su contenido
                if (porcionFichero.length() <= maxLong) {
                    parteRespuesta = porcionFichero;
                    porcionFichero = "";
                    return parteRespuesta;
                }
                // pero su longitud supera el limite [maxLong]
                // -> devuelve una parte de su contenido
                else {
                    parteRespuesta = porcionFichero.substring(0, maxLong);
                    porcionFichero = porcionFichero.substring(maxLong);
                    return parteRespuesta;
                }
            }
        }
        return null;
    }

    /**
     * Metodo auxiliar que sustituye una serie de caracteres problematicos en
     * HTML (vocales con acento, apertura de exclamacion, ...)
     * 
     * @param s Cadena de texto que se quiere recodificar
     * @return Cadena de texto recodificada en formato html
     */
    public static String reEncode(String s) {
        s = s.replace("ñ", "&ntilde;");
        s = s.replace("Ñ", "&Ntilde;");
        s = s.replace("á", "&aacute;");
        s = s.replace("é", "&eacute;");
        s = s.replace("í", "&iacute;");
        s = s.replace("ó", "&oacute;");
        s = s.replace("ú", "&uacute;");
        s = s.replace("ä", "&auml;");
        s = s.replace("ë", "&euml;");
        s = s.replace("ï", "&iuml;");
        s = s.replace("ö", "&ouml;");
        s = s.replace("ü", "&uuml;");
        s = s.replace("Á", "&Aacute;");
        s = s.replace("É", "&Eacute;");
        s = s.replace("Í", "&Iacute;");
        s = s.replace("Ó", "&Oacute;");
        s = s.replace("Ú", "&Uacute;");
        s = s.replace("Ä", "&Auml;");
        s = s.replace("Ë", "&Euml;");
        s = s.replace("Ï", "&Iuml;");
        s = s.replace("Ö", "&Ouml;");
        s = s.replace("Ü", "&Uuml;");
        s = s.replace("¡", "&iexcl;");
        s = s.replace("¿", "&iquest;");

        return s;
    }

    /**
     * Metodo auxiliar que escribe en un fichero
     * 
     * @param contP1 contenido del primer parametro recibido en el post
     * @param contP2 contenido del segundo parametro recibido en el post
     */
    public static void escribeFichero(String contP1, String contP2) {

        // crea un fichero vacio y sin nombre
        File ruta = new File("");

        // utiliza el fichero vacio para obtener la ruta completa
        File fichero = new File(ruta.getAbsolutePath() + "/" + contP1);

        PrintWriter escritor;
        try {
            escritor = new PrintWriter(fichero);

            // sustituye caracteres especiales, evitando errores con el printf
            String tmp = contP2.replace("%", "%%");

            // escribe contenido en el fichero
            escritor.printf(tmp);
            escritor.flush();
            escritor.close();
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
        }

    }

    /**
     * Metodo que genera el cuerpo de una respuesta en caso de error
     * 
     * @param codigo Codigo de error asociado a la respuesta
     * @param texto Texto asociado a la respuesta
     * @return Cadena de texto correspondiente al cuerpo de una respuesta
     */
    public static String generaCuerpo(int codigo, String texto) {
        String cuerpo = "<html><head>\n";
        cuerpo += "<title>" + codigo + " " + texto + "</title>\n";
        cuerpo += "</head><body>\n";
        cuerpo += "<h1>" + texto + "</h1>\n";
        cuerpo += "</body></html>\n";

        return cuerpo;
    }

    /**
     * Metodo que genera el cuerpo de una respuesta en caso de exito
     * 
     * @param fichero Nombre del fichero generado
     * @param contenido Contenido del fichero generado
     * @return Cadena de texto correspondiente al cuerpo de una respuesta
     */
    public static String generaCuerpoExito(String fichero, String contenido) {
        String cuerpo = "<html><head><meta charset=\"UTF-8\">\n";
        cuerpo += "<title>&iexcl;&Eacute;xito!</title>\n";
        cuerpo += "</head><body>\n";
        cuerpo += "<h1>&iexcl;&Eacute;xito!</h1>\n";
        cuerpo += "<p>Se ha escrito lo siguiente en el fichero " + fichero
                + ":</p>\n";
        cuerpo += "<pre>\n";
        cuerpo += contenido + "\n";
        cuerpo += "</pre>\n";
        cuerpo += "</body></html>\n";

        return cuerpo;
    }

    /**
     * Metodo que genera un paquete HTTP
     * 
     * @param codigo Codigo de error asociado
     * @param cuerpo Cuerpo de la respuesta
     * @return paquete HTTP
     */
    public static String generaRespuesta(int codigo, String cuerpo) {
        return generaRespuesta(codigo, cuerpo, 0);
    }
    
    /**
     * Metodo que genera un paquete HTTP
     * 
     * @param codigo Codigo de error asociado
     * @param len longitud del cuerpo de la respuesta
     * @return paquete HTTP
     */
    public static String generaRespuesta(int codigo, long len) {
        return generaRespuesta(codigo, null, len);
    }

    /**
     * Metodo que genera un paquete HTTP
     * 
     * @param codigo Codigo de respuesta HTTP asociado
     * @param cuerpo Cuerpo de la respuesta
     * @param len Longitud del cuerpo que se envia por separado
     * @return paquete HTTP
     */
    private static String generaRespuesta(int codigo, String contenido,
            long len) {

        String textoCodigo = "";
        String cuerpo = "";

        // se comienza a generar la respuesta
        String respuesta = "HTTP/1.1 " + codigo + " ";

        // si se contesta con exito
        if (codigo == 200) {
            textoCodigo = "OK";

            // y si se recibe un contenido
            if (contenido != null) {
                // se utiliza el contenido recibido para completar el cuerpo
                cuerpo = contenido;
            }
        }
        // si se contesta con un error
        else {
            if (codigo == 400) {
                textoCodigo = "Bad Request";
            } else if (codigo == 403) {
                textoCodigo = "Forbidden";
            } else if (codigo == 404) {
                textoCodigo = "Not Found";
            } else if (codigo == 501) {
                textoCodigo = "Not Implemented";
            }
            // el cuerpo se genera dinamicamente
            cuerpo = generaCuerpo(codigo, textoCodigo);
        }

        // se completa la respuesta con el equivalente textual del codigo HTTP
        respuesta += textoCodigo + "\n";

        // si se ha recibido un cuerpo, o se ha generado
        if (!cuerpo.equals("")) {
            respuesta += "Content-Length: " + cuerpo.length() + "\n";
        }
        // si no se ha recibido un cuerpo, ni se ha generado
        // -> se completa la cabecera HTTP con la longitud,
        // adjuntando posteriormente el fichero leido
        else {
            respuesta += "Content-Length: " + len + "\n";
        }
        respuesta += "\n";

        // si no se recibido ni generado un cuerpo, se añade la cadena vacia
        respuesta += cuerpo;

        return respuesta;
    }
}
