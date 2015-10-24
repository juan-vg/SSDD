/*
 * AUTORES: Juan Vela Garcia / Marta Frias Zapater
 * NIA: 643821 / 535621
 * FICHERO: ServidorThreadLanzador.java
 * TIEMPO: 5 horas
 * DESCRIPCION: Abstraccion de un servidor web (HTTP).
 */

package ssdd.p1.servidor;

import java.io.File;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.util.regex.Matcher;

import ssdd.p1.herramientas.HTTPParser;
import ssdd.p1.herramientas.Utiles;

/**
 * Abstraccion del comportamiento de un servidor web (HTTP) a la hora de
 * procesar las peticiones, independientemente de su implementacion con threads
 * o con un selector.
 * 
 * @author Juan Vela, Marta Frias
 *
 */
public abstract class ServidorHTTP {

    /**
     * Metodo que gestiona una peticion HTTP de tipo GET.
     * 
     * @param analizador : Analizador HTTP
     * @return Respuesta HTTP para enviar al cliente
     * 
     */
    @SuppressWarnings("rawtypes")
    protected final static String httpGet(HTTPParser analizador) {

        File rutaRaiz = new File("");
        File fichero = new File(
                rutaRaiz.getAbsolutePath() + analizador.getPath());

        // si no existe el fichero solicitado
        if (!fichero.exists()) {

            // NO ENCONTRADO (NOT FOUND - 404)
            return Utiles.generaRespuesta(404);

        }

        // si existe el fichero solicitado
        else {

            // analiza la ruta del fichero solicitado con el objetivo de
            // determinar si esta alojado en la zona permitida
            Matcher analizadorSintactico = Utiles.patronRutaFichero
                    .matcher(analizador.getPath());

            // si el fichero solicitado es un fichero (y no un directorio), y
            // ademas esta alojado en la zona permitida
            if (fichero.isFile() && analizadorSintactico.matches()) {

                // CORRECTO (OK - 200)
                return Utiles.generaRespuesta(200, fichero);

            }

            // si es un directorio o esta alojado en la zona restringida
            else {

                // NO PERMITIDO (FORBIDDEN - 403)
                return Utiles.generaRespuesta(403);
            }
        }
    }

    /**
     * Metodo que gestiona una peticion HTTP de tipo POST.
     * 
     * @param analizador : Analizador HTTP
     * @return Respuesta HTTP para enviar al cliente
     * 
     */
    @SuppressWarnings("rawtypes")
    protected final static String httpPost(HTTPParser analizador) {

        // obtiene el cuerpo de la peticion
        // (contiene los datos enviados por el cliente)
        ByteBuffer bodyBuf = analizador.getBody();
        String body = "";

        // si el bufer del cuerpo tiene un vector de respaldo
        if (bodyBuf.hasArray()) {
            body = new String(bodyBuf.array());

            // separar los parametros antes de decodificar por si se
            // incluye el caracter '&' en el contenido de alguno
            String[] params = body.split("&");

            // si y solo si se envian dos parametros
            if (params.length == 2) {

                try {

                    // convertir parametros a texto plano
                    params[0] = URLDecoder.decode(params[0], "UTF-8");
                    params[1] = URLDecoder.decode(params[1], "UTF-8");

                    // separar nombres de parametros de su contenido
                    String nomP1 = params[0].substring(0,
                            params[0].indexOf("="));
                    String nomP2 = params[1].substring(0,
                            params[1].indexOf("="));

                    // si y solo si el nombre de los dos parametros coinciden
                    // con los esperados
                    if (nomP1.compareTo("fname") == 0
                            && nomP2.compareTo("content") == 0) {

                        // obtener nombre del fichero
                        String contP1 = params[0]
                                .substring(params[0].indexOf("=") + 1);

                        // analiza la ruta del fichero solicitado con el
                        // objetivo de determinar si esta alojado en la zona
                        // permitida
                        Matcher matcher = Utiles.patronRutaFichero
                                .matcher(contP1);

                        // si el fichero solicitado esta alojado en la zona
                        // permitida
                        if (matcher.matches()) {

                            // obtener contenido del fichero
                            String contP2 = params[1]
                                    .substring(params[1].indexOf("=") + 1);

                            // escribir contenido en el fichero
                            Utiles.escribeFichero(contP1, contP2);

                            // recodificar el contenido para que se muestre
                            // correctamente en la web
                            contP2 = Utiles.codificarHTML(contP2);

                            return Utiles.generaRespuesta(200,
                                    Utiles.generaCuerpoExito(contP1, contP2));
                        }

                        // si el fichero solicitado esta alojado en la zona
                        // restringida
                        else {

                            // NO PERMITIDO (FORBIDDEN - 403)
                            return Utiles.generaRespuesta(403);
                        }
                    }

                    // si los nombres de los dos parametros NO coinciden con los
                    // esperados
                    else {

                        // BAD REQUEST (400)
                        return Utiles.generaRespuesta(400);
                    }
                }

                // (no deberia ocurrir) si no se encuentra la codificacion UTF-8
                catch (Exception e) {
                    
                    System.err.println("ERROR: " + e.getMessage());
                    e.printStackTrace();

                    // ERROR INTERNO DEL SERVIDOR (INTERNAL SERVER ERROR - 500)
                    return Utiles.generaRespuesta(500);
                }
            }

            // si el numero de los parametros no es exactamente dos
            else {

                // BAD REQUEST (400)
                return Utiles.generaRespuesta(400);
            }
        }

        // (no deberia ocurrir) si el bufer del cuerpo no tiene un vector de
        // respaldo
        else {

            // ERROR INTERNO DEL SERVIDOR (INTERNAL SERVER ERROR - 500)
            return Utiles.generaRespuesta(500);
        }
    }

}
