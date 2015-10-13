package ssdd.p1.cliente;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClienteHTTP {

	private static final String server = "192.168.1.41";
	private static final int port = 8000;

	private static String peticion(String op, String path) {
		return op + " /" + path + " HTTP/1.1";
	}

	public static void main(String[] args) {
		try{
			
			String op = "GET";
			String path ="n1.txt";
			
			System.out.println("INICIANDO CONEXION");

			Socket sock = new Socket(server, port);

			BufferedReader entrada = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			PrintWriter salida = new PrintWriter(sock.getOutputStream());
			
			String peticion = peticion(op, path);

			System.out.println("ENVIANDO PETICION: " + peticion);
			salida.println(peticion);
			salida.println();
			salida.flush();
			
			System.out.println("RESPUESTA: ");
			
			String t = "";

			while((t = entrada.readLine()) != null) {
				System.out.println(t);
			}
				
			sock.close();

		} catch (Exception e){
			System.out.println("Error: " + e.getMessage());
		}
	}

}
