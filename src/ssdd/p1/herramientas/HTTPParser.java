/*
 * AUTORES: Juan Vela Garcia / Marta Frias Zapater
 * NIA: 643821 / 535621
 * FICHERO: ServidorThreadLanzador.java
 * TIEMPO: 15 minutos
 * DESCRIPCION: Metodos comunes a los analizadores HTTP (no / bloqueantes).
 */

package ssdd.p1.herramientas;

import java.nio.ByteBuffer;

/**
 * Conjunto de metodos comunes a los analizadores HTTP, tanto bloqueantes como
 * no bloqueantes.
 * 
 * @author Juan Vela, Marta Frias
 *
 * @param <T> : Es el tipo de la entrada suministrada al analizador
 */
public interface HTTPParser<T> {

    /**
     * Analiza una peticion HTTP
     * 
     * @param entrada : Fuente de datos para analizar
     * 
     */
    public void parseRequest(T entrada);

    /**
     * Comprueba si una peticion HTTP ha sido recibida completamente y con exito
     * 
     * @return cierto si y solo si la peticion se ha completado correctamente
     * 
     */
    public boolean isComplete();

    /**
     * Comprueba si una peticion HTTP ha fallado
     * 
     * @return cierto si y solo si la peticion ha fallado
     * 
     */
    public boolean failed();

    /**
     * Devuelve la operacion (GET o POST) contenida en la peticion HTTP
     * 
     * @return cadena que contiene la operacion solicitada
     * 
     */
    public String getMethod();

    /**
     * Devuelve el fichero solicitado en la peticion HTTP
     * 
     * @return cadena que contiene la ruta relativa del fichero solicitado, con
     *         respecto a la localizacion del servidor
     * 
     */
    public String getPath();

    /**
     * Devuelve el cuerpo de la peticion HTTP
     * 
     * @return bufer de bytes que contiene el cuerpo enviado
     */
    public ByteBuffer getBody();
}
