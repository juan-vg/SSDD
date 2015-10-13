/*
 * AUTORES: Juan Vela Garcia / Marta Frias Zapater
 * NIA: 643821 / 535621
 * FICHERO: Utils.java
 * TIEMPO: 5 horas
 * DESCRIPCION: Informacion asociada a una conexion
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
     * Atributo que almacena un objeto HTTPParser. Solo se utiliza en el
     * servidor funciona en modo selector (no bloqueante)
     */
    private HTTPParser parser;

    /** Atributo que almacena un objeto ByteBuffer */
    private ByteBuffer buf;

    /** Atributo que almacena un objeto Scanner. */
    private Scanner file;

    /** Atributo que almacena un objeto String */
    private String line;

    /** Atributo que almacena un objeto String */
    private String str;

    /**
     * Metodo constructor de la clase. Crea un objeto con los atributos vacios.
     */
    public Utiles() {
        parser = null;
        buf = null;
        file = null;
        line = "";
        str = null;
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
        File path = new File("");
        File fichero = new File(path.getAbsolutePath() + "/" + contP1);
        PrintWriter escritor;
        try {
            escritor = new PrintWriter(fichero);

            // evita errores con el printf
            String tmp = contP2.replace("%", "%%");
            escritor.printf(tmp);
            escritor.flush();
            escritor.close();
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
        }

    }

    /**
     * Metodo que actualiza la referencia del atributo parser
     * 
     * @param p Objeto HTTPParser
     */
    public void setParser(HTTPParser p) {
        parser = p;
    }

    /**
     * Metodo que actualiza la referencia del atributo buf
     * 
     * @param b Objeto ByteBufer
     */
    public void setBuffer(ByteBuffer b) {
        buf = b;
    }

    /**
     * Metodo que actualiza la referencia del atributo file
     * 
     * @param f Objeto Scanner que hace referencia a un fichero
     */
    public void setFile(Scanner f) {
        file = f;
    }

    /**
     * Metodo que actualiza la referencia del atributo string
     * 
     * @param s Cadena de texto
     */
    public void setString(String s) {
        str = s;
    }

    /**
     * Metodo que devuelve el valor del atributo parser
     * 
     * @return HTTPParser asociado al objeto Utils
     */
    public HTTPParser getParser() {
        return parser;
    }

    /**
     * Metodo que devuelve el valor del atributo buf
     * 
     * @return ByteBuffer asociado al objeto Utils
     */
    public ByteBuffer getBuffer() {
        return buf;
    }

    /**
     * Metodo que devuelve el valor del atributo str
     * 
     * @return String asociado al objeto Utils
     */
    public String getString() {
        return str;
    }

    /**
     * Metodo que devuelve true si y solo si el atributo str es diferente de
     * null.
     * 
     * @return true si el atributo str no es null
     */
    public boolean isSetString() {
        return str != null;
    }

    /**
     * Metodo que devuelve true si y solo si el atributo file es diferente de
     * null.
     * 
     * @return true si el atributo file no es null
     */
    public boolean isSetFile() {
        return file != null;
    }

    /**
     * Metodo que devuelve true si se ha llegado al final del fichero asociado
     * al atributo file. Si el atributo file esta a null se interpreta como que
     * esta finalizado
     * 
     * @return true si el ha llegado al final del fichero asociado al atributo
     *         file
     */
    private boolean isFinishedFile() {
        if (isSetFile()) {
            return !file.hasNextLine();
        } else {

            // Si no hay file, es como si estuviera finalizado
            return true;
        }
    }

    /**
     * Metodo que comprueba si queda informacion por escribir del cuerpo de la
     * respuesta
     * 
     * @return true si queda informacion por escribir
     */
    public boolean hayBody() {
        if (isSetString() && isSetFile()) {
            return (str.length() > 0) || (!isFinishedFile())
                    || (line.length() > 0);
        } else if (isSetString()) {
            return str.length() > 0;
        } else {
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
    public String getBody(int max) {

        if (isSetString() && !str.equals("")) {
            if (str.length() <= max) {
                String tmp = new String(str);
                str = "";
                return tmp;
            } else {
                String tmp = str.substring(0, max);
                str = str.substring(max);
                return tmp;
            }
        }

        if (isSetFile()) {
            if (line.equals("") && !isFinishedFile()) {
                line = file.nextLine() + "\n";
            }

            if (!line.equals("")) {
                if (line.length() <= max) {
                    String tmp = line;
                    line = "";
                    return tmp;
                } else {
                    String tmp = line.substring(0, max);
                    line = line.substring(max);
                    return tmp;
                }
            }
        }
        return null;
    }

    /**
     * Metodo que genera el cuerpo de una respuesta en caso de error
     * 
     * @param codigo Codigo de error asociado a la respuesta
     * @param texto Texto asociado a la respuesta
     * @return Cadena de texto correspondiente al cuerpo de una respuesta
     */
    public static String cuerpo(int codigo, String texto) {
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
    public static String cuerpoExito(String fichero, String contenido) {
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
    public static String respuesta(int codigo, String cuerpo) {
        return respuesta(codigo, cuerpo, 0);
    }

    /**
     * Metodo que genera un paquete HTTP
     * 
     * @param codigo Codigo de error asociado
     * @param cuerpo Cuerpo de la respuesta
     * @param len Longitud del cuerpo que se envia por separado
     * @return paquete HTTP
     */
    public static String respuesta(int codigo, String cuerpo, long len) {
        String respuesta = "HTTP/1.1 " + codigo + " ";
        String texto = "";
        String body = "";

        if (codigo == 200) {
            texto = "OK";
            if (cuerpo != null) {
                body = cuerpo;
            }
        } else {
            if (codigo == 400) {
                texto = "Bad Request";
            } else if (codigo == 403) {
                texto = "Forbidden";
            } else if (codigo == 404) {
                texto = "Not Found";
            } else if (codigo == 501) {
                texto = "Not Implemented";
            }
            body = cuerpo(codigo, texto);
        }

        respuesta += texto + "\n";
        if (!body.equals("")) {
            respuesta += "Content-Length: " + body.length() + "\n";
        } else {
            respuesta += "Content-Length: " + len + "\n";
        }
        respuesta += "\n";
        respuesta += body;

        return respuesta;
    }
}
