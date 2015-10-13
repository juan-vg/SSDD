/*
 * AUTORES: Juan Vela Garcia / Marta Frias Zapater
 * NIA: 643821 / 535621
 * FICHERO: ServidorThreadHTTP.java
 * TIEMPO: 15 horas
 * DESCRIPCION: implementacion en la que segestiona un servidor HTTP utilizando threads.
 */
package ssdd.p1.servidor;

import java.io.File;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Scanner;
import java.util.regex.Matcher;

/**
 * Clase que gestiona un servidor HTTP utilizando threads
 * 
 * @author Juan Vela, Marta Frias
 *
 */
public class ServidorThreadHTTP implements Runnable {

	/** Atributo que hace referencia al socket asociado al cliente */
	private Socket clientSocket;

	/** Metodo constructor de la clase ServidorThreadHTTP */
	public ServidorThreadHTTP(Socket c) {
		clientSocket = c;
	}

	/**
	 * Metodo auxiliar que sustituye una serie de caracteres problematicos en
	 * HTML (vocales con acento, eñes y exclamacion)
	 * 
	 * @param s Cadena de texto que se quiere recodificar
	 * @return Cadena de texto recodificada en formato html
	 */
	private static String reEncode(String s) {
		s = s.replace("ñ", "&ntilde;");
		s = s.replace("Ñ", "&Ntilde;");
		s = s.replace("á", "&aacute;");
		s = s.replace("é", "&eacute;");
		s = s.replace("í", "&iacute;");
		s = s.replace("ó", "&oacute;");
		s = s.replace("ú", "&uacute;");
		s = s.replace("Á", "&Aacute;");
		s = s.replace("É", "&Eacute;");
		s = s.replace("Í", "&Iacute;");
		s = s.replace("Ó", "&Oacute;");
		s = s.replace("Ú", "&Uacute;");
		s = s.replace("¡", "&iexcl;");

		return s;
	}

	/**
	 * Metodo auxiliar que escribe en un fichero
	 * 
	 * @param contP1
	 *            contenido del primer parametro recibido en el post
	 * @param contP2
	 *            contenido del segundo parametro recibido en el post
	 */
	private static void escribeFichero(String contP1, String contP2) {
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

	@Override
	public void run() {
		try {
			PrintWriter salida = new PrintWriter(clientSocket.getOutputStream(), true);

			BlockingHTTPParser parser = new BlockingHTTPParser();
			parser.parseRequest(clientSocket.getInputStream());

			if (parser.failed()) {

				// BAD REQUEST (400)
				salida.printf(Utils.respuesta(400, null));
			} else if (parser.isComplete()) {

				// REQUEST COMPLETA
				if (parser.getMethod().equals("GET")) {

					// METODO GET
					File path = new File("");
					File fichero = new File(path.getAbsolutePath() + parser.getPath());

					if (!fichero.exists()) {

						// NOT FOUND (404)
						salida.printf(Utils.respuesta(404, null));
					} else {
						Matcher matcher = Utils.pathPattern.matcher(parser.getPath());
						if (matcher.matches()) {

							// OK (200)
							Scanner target = new Scanner(fichero);
							String body = "";
							while (target.hasNextLine()) {
								body += target.nextLine() + "\n";
							}

							salida.println(Utils.respuesta(200, body));
						} else {

							// FORBIDDEN (403)
							salida.printf(Utils.respuesta(403, null));
						}
					}
				} else if (parser.getMethod().equals("POST")) {

					// METODO POST
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
					//sb.append(c);

					if (params.length == 2) {
						params[0] = URLDecoder.decode(params[0], "UTF-8");
						params[1] = URLDecoder.decode(params[1], "UTF-8");

						String nomP1 = params[0].substring(0, params[0].indexOf("="));
						String nomP2 = params[1].substring(0, params[1].indexOf("="));
						

						if (nomP1.compareTo("fname") == 0 && nomP2.compareTo("content") == 0) {

							String contP1 = params[0].substring(params[0].indexOf("=") + 1);
							Matcher matcher = Utils.pathPattern.matcher(contP1);
							
							if (matcher.matches()) {
								long t1 = System.currentTimeMillis();
								String contP2 = params[1].substring(params[1].indexOf("=") + 1);
								System.out.println(contP2.substring(contP2.length()-37, contP2.length()-1));
								System.out.println("Substring -> "+ (System.currentTimeMillis()-t1));
								
								t1 = System.currentTimeMillis();
								escribeFichero(contP1, contP2);
								System.out.println("escribeFichero -> "+ (System.currentTimeMillis()-t1));
								
								t1 = System.currentTimeMillis();
								contP2 = reEncode(contP2);
								System.out.println("reEncode -> "+ (System.currentTimeMillis()-t1));
								
								t1 = System.currentTimeMillis();
								salida.println(Utils.respuesta(200, Utils.cuerpoExito(contP1, contP2)));
								System.out.println("Respuesta 200 -> "+ (System.currentTimeMillis()-t1));
							} else {

								// FORBIDDEN (403)
								salida.printf(Utils.respuesta(403, null));
							}
						} else {

							// BAD REQUEST (400)
							salida.printf(Utils.respuesta(400, null));
						}
					} else {

						// BAD REQUEST (400)
						salida.printf(Utils.respuesta(400, null));
					}
				} else {

					// NOT IMPLEMENTED (501)
					salida.printf(Utils.respuesta(501, null));
				}

			} else {

				// REQUEST NO COMPLETA
				// No deberia ocurrir
			}

			clientSocket.close();
		} catch (Exception e) {
			System.out.printf("Error: %s", e.getMessage());
		}
	}

}
