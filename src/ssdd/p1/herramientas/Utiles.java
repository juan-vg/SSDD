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
     * 
     */
    public static final Pattern patronRutaFichero = Pattern
            .compile("[/]?[a-zA-Z0-9_-]+(.[a-zA-Z0-9]+)?");

    /**
     * Atributo que almacena un analizador HTTP (y su estado). Solo se utiliza
     * cuando el servidor funciona en modo selector (no bloqueante)
     * 
     */
    private NonBlockingHTTPParser analizador;

    /**
     * Atributo que almacena un bufer. Solo se utiliza cuando el servidor
     * funciona en modo selector (no bloqueante)
     * 
     */
    private ByteBuffer bufer;

    /**
     * Atributo que almacena la respuesta que se debe enviar al cliente. Solo se
     * utiliza cuando el servidor funciona en modo selector (no bloqueante)
     * 
     */
    private String respuesta;

    /**
     * Metodo constructor de la clase. Crea un objeto con los atributos vacios.
     * 
     */
    public Utiles() {
        analizador = null;
        bufer = null;
        respuesta = null;
    }

    /**
     * Almacena un nuevo analizador HTTP. Solo se utiliza cuando el servidor
     * funciona en modo selector (no bloqueante)
     * 
     * @param analizador : Analizador HTTP
     * 
     */
    public void setAnalizador(NonBlockingHTTPParser analizador) {
        this.analizador = analizador;
    }

    /**
     * Almacena un nuevo bufer ByteBuffer. Solo se utiliza cuando el servidor
     * funciona en modo selector (no bloqueante)
     * 
     * @param bufer : Bufer ByteBuffer
     * 
     */
    public void setBuffer(ByteBuffer bufer) {
        this.bufer = bufer;
    }

    /**
     * Almacena una nueva respuesta para el cliente. Solo se utiliza cuando el
     * servidor funciona en modo selector (no bloqueante)
     * 
     * @param respuesta : Cadena que contiene la respuesta para el cliente
     * 
     */
    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    /**
     * Devuelve el analizador HTTP asociado. Solo se utiliza cuando el servidor
     * funciona en modo selector (no bloqueante)
     * 
     * @return Analizador HTTP
     * 
     */
    public NonBlockingHTTPParser getAnalizador() {
        return analizador;
    }

    /**
     * Devuelve el bufer asociado. Solo se utiliza cuando el servidor funciona
     * en modo selector (no bloqueante)
     * 
     * @return bufer ByteBuffer
     * 
     */
    public ByteBuffer getBuffer() {
        return bufer;
    }

    /**
     * Devuelve cierto si y solo si se ha establecido una respuesta (valida)
     * para el cliente. Solo se utiliza cuando el servidor funciona en modo
     * selector (no bloqueante)
     * 
     * @return cierto si se ha establecido una respuesta valida
     */
    public boolean isSetRespuesta() {
        return respuesta != null;
    }

    /**
     * Comprueba si quedan datos por enviar al cliente. Solo se utiliza cuando
     * el servidor funciona en modo selector (no bloqueante)
     * 
     * @return cierto si quedan datos por enviar al cliente
     * 
     */
    public boolean quedaCuerpo() {

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
     * Devuelve una cadena de texto de longitud maxima [max] correspondiente a
     * parte del cuerpo de la respuesta. Solo se utiliza cuando el servidor
     * funciona en modo selector (no bloqueante)
     * 
     * @param maxLong : Longitud maxima de la cadena a devolver
     * @return Cadena de texto correspondiente a parte del cuerpo de la
     *         respuesta
     * 
     */
    public String getCuerpo(int maxLong) {

        String parteRespuesta = "";

        // si queda contenido por devolver
        if (quedaCuerpo()) {

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

    // METODOS COMUNES (ESTATICOS) A TODAS LAS IMPLEMENTACIONES

    /**
     * Sustituye una serie de caracteres problematicos en HTML (vocales con
     * acento, apertura de exclamacion, ...)
     * 
     * @param texto : Cadena de texto que se quiere recodificar
     * @return Cadena de texto recodificada en formato html
     * 
     */
    public static String codificarHTML(String texto) {
        texto = texto.replace("ñ", "&ntilde;");
        texto = texto.replace("Ñ", "&Ntilde;");
        texto = texto.replace("á", "&aacute;");
        texto = texto.replace("é", "&eacute;");
        texto = texto.replace("í", "&iacute;");
        texto = texto.replace("ó", "&oacute;");
        texto = texto.replace("ú", "&uacute;");
        texto = texto.replace("ä", "&auml;");
        texto = texto.replace("ë", "&euml;");
        texto = texto.replace("ï", "&iuml;");
        texto = texto.replace("ö", "&ouml;");
        texto = texto.replace("ü", "&uuml;");
        texto = texto.replace("Á", "&Aacute;");
        texto = texto.replace("É", "&Eacute;");
        texto = texto.replace("Í", "&Iacute;");
        texto = texto.replace("Ó", "&Oacute;");
        texto = texto.replace("Ú", "&Uacute;");
        texto = texto.replace("Ä", "&Auml;");
        texto = texto.replace("Ë", "&Euml;");
        texto = texto.replace("Ï", "&Iuml;");
        texto = texto.replace("Ö", "&Ouml;");
        texto = texto.replace("Ü", "&Uuml;");
        texto = texto.replace("¡", "&iexcl;");
        texto = texto.replace("¿", "&iquest;");
        texto = texto.replace("»", "&raquo;");
        texto = texto.replace("«", "&laquo;");

        return texto;
    }

    /**
     * Metodo auxiliar que escribe en un fichero de nombre [nombre] el contenido
     * [contenido]
     * 
     * @param nombre : Nombre del fichero
     * @param contenido : Contenido que debe ser escrito en el fichero
     * 
     */
    public static void escribeFichero(String nombre, String contenido) {

        // crea un fichero vacio y sin nombre
        File ruta = new File("");

        // utiliza el fichero vacio para obtener la ruta completa
        File fichero = new File(ruta.getAbsolutePath() + "/" + nombre);

        PrintWriter escritor;
        try {
            escritor = new PrintWriter(fichero);

            // sustituye caracteres especiales, evitando errores con el printf
            contenido = contenido.replace("%", "%%");

            // escribe contenido en el fichero
            escritor.printf(contenido);
            escritor.flush();
            escritor.close();
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
        }

    }

    /**
     * Devuelve el cuerpo de una respuesta HTTP en caso de error
     * 
     * @param codigo : Codigo de error asociado a la respuesta
     * @param texto : Texto asociado a la respuesta
     * @return Cadena de texto correspondiente al cuerpo de una respuesta
     * 
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
     * Devuelve el cuerpo de una respuesta HTTP en caso de exito
     * 
     * @param fichero : Nombre del fichero generado
     * @param contenido : Contenido del fichero generado
     * @return Cadena de texto correspondiente al cuerpo de una respuesta
     * 
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
     * Devuelve una respuesta HTTP completa a partir del codigo de error
     * [codigo].
     * 
     * @param codigo : Codigo de error HTTP
     * @return respuesta HTTP completa
     * 
     */
    public static String generaRespuesta(int codigo) {
        return generaRespuesta(codigo, "");
    }

    /**
     * Devuelve una respuesta HTTP completa a partir del codigo HTTP [codigo] y
     * el contenido del fichero [fichero].
     * 
     * @param codigo : Codigo HTTP
     * @param fichero : Fichero cuyo contenido sera incluido en la respuesta
     *            HTTP
     * @return respuesta HTTP completa
     * 
     */
    public static String generaRespuesta(int codigo, File fichero) {
        try {
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

        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            return null;
        }
    }

    /**
     * Devuelve una respuesta HTTP completa a partir del codigo HTTP [codigo] y
     * el contenido del fichero [fichero].
     * 
     * @param codigo : Codigo HTTP
     * @param contenido : Cadena cuyo contenido sera incluido en la respuesta
     *            HTTP
     * @return respuesta HTTP completa
     * 
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
            } else if (codigo == 500) {
                textoCodigo = "Internal Server Error";
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

        // si no se recibido ni generado un cuerpo, se añade la cadena vacia
        respuesta += cuerpo;

        return respuesta;
    }
}
