/*
 * AUTOR: Juan Vela y Marta Frias
 * NIA: 643821 - 535621
 * FICHERO: Envelope.java
 * TIEMPO: 20 min
 * DESCRIPCION: Clase que gestiona y encapsula mensajes, adjuntando los campos 
 * emisor, receptor y marca de tiempo.
 */
package ssdd.p4.ms;

import java.io.Serializable;

/**
 * Clase que gestiona y encapsula mensajes, adjuntando los campos emisor,
 * receptor y marca de tiempo.
 *
 * @author Juan Vela
 * @author Marta Frias
 *
 */
public class Envelope implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** Marca de tiempo del mensaje */
	private int timestamp;

	/** Identificador del proceso emisor */
	private int source;
	
	/** Identificador del proceso receptor */
	private int destination;
	
	/** Contenido del mensaje encapsulado */
	private Serializable payload;

	/** Delimitador */ 
	private final static String delimiter = "================================="
			+ "===============================";

	/**
	 * Crea un objeto que encapsula una fuente, un destino y un objeto 
	 * Serializable, que puede ser enviado a traves de la red.
	 * 
	 * @param src Identificador del proceso emisor
	 * @param dst Identificador del proceso receptor 
	 * @param obj objeto Serializable a encapsular
	 */
	public Envelope(int src, int dst, Serializable obj) {
		source = src;
		destination = dst;
		payload = obj;
		timestamp = -1;
	}
	
	/**
	 * Establece la marca de tiempo del mensaje.
	 * 
	 * @param t Marca de tiempo del mensaje
	 */
	public void setTimeStamp(int t) {
		timestamp = t;
	}
	
	/**
	 * Devuelve la marca de tiempo del mensaje.
	 * 
	 * @return Marca de tiempo.
	 * 
	 */
	public int getTimeStamp() {
		return timestamp;
	}

	/**
	 * Devuelve el identificador del proceso que envia el mensaje encapsulado.
	 * 
	 * @return Identificador del proceso emisor.
	 * 
	 */
	public int getSource() {
		return source;
	}

	/**
	 * Devuelve el identificador del proceso que debe recibir el mensaje
	 * encapsulado.
	 * 
	 * @return Identificador del proceso receptor.
	 * 
	 */
	public int getDestination() {
		return destination;
	}

	/**
	 * Devuelve el contenido del mensaje.
	 * 
	 * @return Objeto Serializable contenido en el mensaje encapsulado.
	 * 
	 */
	public Serializable getPayload() {
		return payload;
	}

	public String toString() {

		String res = String.format("%s\n", delimiter);

		res += String.format("TIMESTAMP: %d\n", timestamp);
		res += String.format("FROM: %d\n", source);
		res += String.format("TO: %d\n", destination);
		res += String.format("BODY: %s\n", payload.toString());

		res += String.format("%s", delimiter);

		return res;
	}

}
