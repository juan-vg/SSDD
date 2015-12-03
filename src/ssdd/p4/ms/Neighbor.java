/*
 * AUTOR: Juan Vela y Marta Frias
 * NIA: 643821 - 535621
 * FICHERO: Neighbor.java
 * TIEMPO: 10 min
 * DESCRIPCION: Clase que guarda la informacion relativa a un proceso
 */

package ssdd.p4.ms;

/**
 * Clase que guarda la informacion relativa a un proceso (identificador,
 * direccion y puerto en el que escucha).
 *
 */
public class Neighbor {

	/** Identificador del proceso */
	private int id;

	/** Direccion */
	private String addres;

	/** Numero de puerto en el que escucha*/
	private int port;

	/**
	 * Crea una instancia de Neighbor.
	 * 
	 * @param id
	 *            Identificador de proceso
	 * @param addr
	 *            Direccion 
	 * @param p
	 *            Puerto en el que escucha
	 */
	public Neighbor(int id, String addr, int p) {
		this.id = id;
		this.addres = addr;
		this.port = p;
	}

	/**
	 * Devuelve el identificador del proceso.
	 * 
	 * @return identificador de proceso
	 */
	public int getId() {
		return id;
	}

	/**
	 * Devuelve la direccion donde se encuentra el proceso.
	 * 
	 * @return direccion
	 */
	public String getAddres() {
		return addres;
	}

	/**
	 * Devuelve el numero de puerto donde esta escuchando el proceso.
	 * 
	 * @return numero de puerto
	 */
	public int getPort() {
		return port;
	}
}
