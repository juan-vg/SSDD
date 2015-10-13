/*
 * AUTORES: Juan Vela Garcia / Marta Frias Zapater
 * NIA: 643821 / 535621
 * FICHERO: ServidorThreadRun.java
 * TIEMPO: 25 horas
 * DESCRIPCION: Gestor de peticiones web (HTTP) en hilos separados.
 */

package ssdd.p1.servidor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.util.Scanner;
import java.util.regex.Matcher;

import ssdd.p1.herramientas.BlockingHTTPParser;
import ssdd.p1.herramientas.Utiles;

/**
 * Clase que gestiona un servidor HTTP utilizando threads
 * 
 * @author Juan Vela, Marta Frias
 *
 */
public class ServidorThreadRun implements Runnable {

    /** Atributo que hace referencia al socket asociado al cliente */
    private Socket clientSocket;

    /** Metodo constructor de la clase ServidorThreadHTTP */
    public ServidorThreadRun(Socket c) {
        clientSocket = c;
    }

    private void httpGet(BlockingHTTPParser parser, PrintWriter salida)
            throws FileNotFoundException {

        File path = new File("");
        File fichero = new File(path.getAbsolutePath() + parser.getPath());

        if (!fichero.exists()) {

            // NOT FOUND (404)
            salida.printf(Utiles.respuesta(404, null));
        } else {
            Matcher matcher = Utiles.patronRutaFichero
                    .matcher(parser.getPath());
            if (matcher.matches()) {

                // OK (200)
                Scanner target = new Scanner(fichero);
                String body = "";
                while (target.hasNextLine()) {
                    body += target.nextLine() + "\n";
                }

                target.close();

                salida.println(Utiles.respuesta(200, body));
            } else {

                // FORBIDDEN (403)
                salida.printf(Utiles.respuesta(403, null));
            }
        }
    }

    private static void httpPost(BlockingHTTPParser parser, PrintWriter salida)
            throws UnsupportedEncodingException {

        ByteBuffer bodyBuf = parser.getBody();
        String body = "";

        if (bodyBuf.hasArray()) {
            body = new String(bodyBuf.array());
        }

        // separar los parametros antes de decodificar por si se
        // incluye
        // el caracter '&' en el contenido de alguno
        String[] params = body.split("&");

        StringBuilder sb = new StringBuilder();
        // sb.append(c);

        if (params.length == 2) {
            params[0] = URLDecoder.decode(params[0], "UTF-8");
            params[1] = URLDecoder.decode(params[1], "UTF-8");

            String nomP1 = params[0].substring(0, params[0].indexOf("="));
            String nomP2 = params[1].substring(0, params[1].indexOf("="));

            if (nomP1.compareTo("fname") == 0
                    && nomP2.compareTo("content") == 0) {

                String contP1 = params[0].substring(params[0].indexOf("=") + 1);
                Matcher matcher = Utiles.patronRutaFichero.matcher(contP1);

                if (matcher.matches()) {
                    long t1 = System.currentTimeMillis();
                    String contP2 = params[1]
                            .substring(params[1].indexOf("=") + 1);
                    System.out.println(contP2.substring(contP2.length() - 37,
                            contP2.length() - 1));
                    System.out.println("Substring -> "
                            + (System.currentTimeMillis() - t1));

                    t1 = System.currentTimeMillis();
                    Utiles.escribeFichero(contP1, contP2);
                    System.out.println("escribeFichero -> "
                            + (System.currentTimeMillis() - t1));

                    t1 = System.currentTimeMillis();
                    contP2 = Utiles.reEncode(contP2);
                    System.out.println(
                            "reEncode -> " + (System.currentTimeMillis() - t1));

                    t1 = System.currentTimeMillis();
                    salida.println(Utiles.respuesta(200,
                            Utiles.cuerpoExito(contP1, contP2)));
                    System.out.println("Respuesta 200 -> "
                            + (System.currentTimeMillis() - t1));
                } else {

                    // FORBIDDEN (403)
                    salida.printf(Utiles.respuesta(403, null));
                }
            } else {

                // BAD REQUEST (400)
                salida.printf(Utiles.respuesta(400, null));
            }
        } else {

            // BAD REQUEST (400)
            salida.printf(Utiles.respuesta(400, null));
        }
    }

    @Override
    public void run() {
        try {
            PrintWriter salida = new PrintWriter(clientSocket.getOutputStream(),
                    true);

            BlockingHTTPParser parser = new BlockingHTTPParser();
            parser.parseRequest(clientSocket.getInputStream());

            if (parser.failed()) {

                // BAD REQUEST (400)
                salida.printf(Utiles.respuesta(400, null));
            } else if (parser.isComplete()) {

                // REQUEST COMPLETA
                if (parser.getMethod().equals("GET")) {

                    // METODO GET
                    httpGet(parser, salida);

                } else if (parser.getMethod().equals("POST")) {

                    // METODO POST
                    httpPost(parser, salida);

                } else {

                    // NOT IMPLEMENTED (501)
                    salida.printf(Utiles.respuesta(501, null));
                }

            } else {

                // REQUEST NO COMPLETA
                // No deberia ocurrir
            }

            // cierra conexion
            clientSocket.close();

        } catch (Exception e) {
            System.out.printf("Error: %s", e.getMessage());
        }
    }

}
