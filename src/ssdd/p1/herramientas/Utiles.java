/*
 * AUTORES: Juan Vela Garcia / Marta Frias Zapater
 * NIA: 643821 / 535621
 * FICHERO: Utiles.java
 * TIEMPO: 7 horas
 * DESCRIPCION: Informacion y herramientas asociadas a una conexion HTTP
 */

package ssdd.p1.herramientas;

import java.io.File;
import java.io.FileNotFoundException;
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

    /** Atributo que almacena un objeto String */
    private String respuesta;

    /**
     * Metodo constructor de la clase. Crea un objeto con los atributos vacios.
     */
    public Utiles() {
        analizador = null;
        bufer = null;
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
     * Metodo que devuelve true si y solo si el atributo str es diferente de
     * null.
     * 
     * @return true si el atributo str no es null
     */
    public boolean isSetRespuesta() {
        return respuesta != null;
    }

    /**
     * Metodo que comprueba si queda informacion por escribir del cuerpo de la
     * respuesta
     * 
     * @return true si queda informacion por escribir
     */
    public boolean isCuerpo() {

        // si hay una respuesta que devolver
        if (isSetRespuesta()) {

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

        // si queda contenido por devolver
        if (isCuerpo()) {

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

        // si no, devuelve error
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
        s = s.replace("�", "&ntilde;");
        s = s.replace("�", "&Ntilde;");
        s = s.replace("�", "&aacute;");
        s = s.replace("�", "&eacute;");
        s = s.replace("�", "&iacute;");
        s = s.replace("�", "&oacute;");
        s = s.replace("�", "&uacute;");
        s = s.replace("�", "&auml;");
        s = s.replace("�", "&euml;");
        s = s.replace("�", "&iuml;");
        s = s.replace("�", "&ouml;");
        s = s.replace("�", "&uuml;");
        s = s.replace("�", "&Aacute;");
        s = s.replace("�", "&Eacute;");
        s = s.replace("�", "&Iacute;");
        s = s.replace("�", "&Oacute;");
        s = s.replace("�", "&Uacute;");
        s = s.replace("�", "&Auml;");
        s = s.replace("�", "&Euml;");
        s = s.replace("�", "&Iuml;");
        s = s.replace("�", "&Ouml;");
        s = s.replace("�", "&Uuml;");
        s = s.replace("�", "&iexcl;");
        s = s.replace("�", "&iquest;");
        s = s.replace("�", "&raquo;");
        s = s.replace("�", "&laquo;");

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
    
    public static String generaRespuesta(int codigo){
        return generaRespuesta(codigo, "");
    }

    public static String generaRespuesta(int codigo, File fichero)
            throws FileNotFoundException {

        Scanner lectorFich;

        lectorFich = new Scanner(fichero);

        // lee el fichero linea a linea, por lo que se usa el
        // constructor de cadenas para que el proceso sea mas
        // eficiente
        StringBuilder cuerpo = new StringBuilder();
        while (lectorFich.hasNextLine()) {
            cuerpo.append(lectorFich.nextLine() + "\n");
        }
        lectorFich.close();

        return generaRespuesta(codigo, cuerpo.toString());
    }

    /**
     * Metodo que genera un paquete HTTP
     * 
     * @param codigo Codigo de respuesta HTTP asociado
     * @param cuerpo Cuerpo de la respuesta
     * @param len Longitud del cuerpo que se envia por separado
     * @return paquete HTTP
     */
    public static String generaRespuesta(int codigo, String contenido) {

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

        respuesta += "Content-Length: " + cuerpo.length() + "\n";

        respuesta += "\n";

        // si no se recibido ni generado un cuerpo, se a�ade la cadena vacia
        respuesta += cuerpo;

        return respuesta;
    }
}
