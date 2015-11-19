/*
 * AUTOR: Juan Vela y Marta Frias
 * NIA: 643821 - 535621
 * FICHERO: Query.java
 * TIEMPO: 10 min
 * DESCRIPCION: Clase que encapsula objetos para consultar a una base
 *  de datos.
 */

package ssdd.p3.p3;

import java.io.Serializable;

/**
 * 
 * Clase que encapsula objetos consulta a una base de datos de tuplas.
 *
 * @author Juan Vela
 * @author Marta Frias
 *
 */
public class Query implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/** Operacion de consulta */
	private String op;
	
	/** Clave */
	private String key;
	
	/** Valor */
	private String value;

	/**
	 * Crea una instancia de Query que encapsula operacion, clave y valor.
	 * 
	 * @param operation
	 *            operacion de consulta a la base de datos
	 * @param key
	 *            clave
	 * @param value
	 *            valor
	 */
	public Query(String operation, String key, String value) {
		this.op = operation;
		this.key = key;
		this.value = value;
	}

	/**
	 * Devuelve la operacion de la consulta.
	 * 
	 * @return Operacion
	 */
	public String getOp() {
		return op;
	}
	
	/**
	 * Devuelve la clave de la tupla.
	 * 
	 * @return clave de la tupla
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Devuelve el valor de la tupla.
	 * 
	 * @return valor de la tupla
	 */
	public String getVal() {
		return value;
	}

	public String toString() {
		if(op.equals("select") || op.equals("delete")){
			return op + " : (" + key + ")";
		}
		else{
			return op + " : (" + key + ", " + value + ")";
		}
		
	}
}
