/*
 * AUTOR: Juan Vela y Marta Frias
 * NIA: 643821 - 535621
 * FICHERO: SGBD.java
 * TIEMPO: 20 min
 * DESCRIPCION: Clase de prueba que simula la gestion de una base de datos.
 */

package ssdd.p3.p3;

import java.util.HashMap;

/**
 * Clase que simula la gestion de una base de datos con la informacion
 * organizada en tuplas (clave,valor) y operaciones tipicas (insertar, eliminar,
 * actualizar, consultar).
 *
 */
public class SGBD {
	
	/** Base de datos*/
	private HashMap<String,String> db;

	/**
	 * Crea una instancia de SGBD
	 * 
	 * @param num
	 *            tamaño inicial de la base de datos
	 */
	public SGBD(int num){
		db = new HashMap<String,String>(num);
	}
	
	/**
	 * Si en la base de datos no existe ninguna clave == [key], la inserta y
	 * devuelve true. Si ya existia no la inserta y devuelve false.
	 * 
	 * @param key
	 *            clave que a insertar en la base de datos
	 * @param value
	 *            valor de la clave a insertar
	 * @return true si se ha insertado la clave [key] con valor [value]
	 */
	public boolean insert(String key, String value){
		
		if(!db.containsKey(key)){
			db.put(key, value);
			return true;
			
		} else{
			return false;
		}
		
	}
	
	/**
	 * Si en la base de datos existe una clave == [key] la elimina y devuelve
	 * true. Devuelve false en caso contrario.
	 * 
	 * @param key
	 *            clave a eliminar de la base de datos
	 * @return true si elimina la clave [key] de la base de datos
	 */
	public boolean delete(String key){
		
		if(db.containsKey(key)){
			db.remove(key);
			return true;
		}
		else{
			return false;
		}
		
	}
	
	/**
	 * Si en la base de datos existe una clave == [key] la actualiza y devuelve
	 * true. Devuelve false en caso contrario.
	 * 
	 * @param key
	 *            clave a actualizar en la base de datos
	 * @param value
	 *            valor de la clave a actualizar
	 * @return true si actualiza la clave [key] con el valor [value]
	 */
	public boolean update(String key, String value){
		
		if(db.containsKey(key)){
			db.put(key, value);
			return true;
		}
		else{
			return false;
		}
		
	}
	
	/**
	 * Devuelve una cadena que contiene el valor la clave [key] de la base de
	 *  datos.
	 * 
	 * @param key
	 *            clave a consultar de la base de datos
	 * @return cadena que contiene el valor de la clave [key]
	 */
	public String select(String key){
		
		if(db.containsKey(key)){
			return db.get(key);
		}
		else{
			return null;
		}
	}
}
