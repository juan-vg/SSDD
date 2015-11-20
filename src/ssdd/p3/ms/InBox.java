/*
 * AUTOR: Juan Vela y Marta Frias
 * NIA: 643821 - 535621
 * FICHERO: InBox.java
 * TIEMPO: 30 min
 * DESCRIPCION: Clase que proporciona un buzon de mensajes.
 */
package ssdd.p3.ms;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Clase que proporciona un buzon de mensajes con acceso en exclusion mutua (es
 * una cola de mensajes recibidos pero no leidos).
 * 
 * @author Juan Vela
 * @author Marta Frias
 *
 */
public class InBox {

    /** Buzon de mensajes */
    private LinkedList<Serializable> inBox;

    /** Tamaño maximo del buzon */
    private int maxSize;

    /** Candado */
    private final Lock mutex;

    /** Condicion buzon vacio */
    private final Condition empty;

    /**
     * Crea un buzon de mensajes.
     * 
     * @param size tamaño del buzon
     */
    public InBox(int size) {

        maxSize = size;
        inBox = new LinkedList<Serializable>();
        mutex = new ReentrantLock();
        empty = mutex.newCondition();
    }

    /**
     * Devuelve true si y solo si el buzon de mensajes esta vacio.
     * 
     * @return true si el buzon esta vacio
     */
    public boolean isEmpty() {

        boolean resp = false;

        mutex.lock();

        resp = inBox.isEmpty();

        mutex.unlock();

        return resp;
    }

    /**
     * Devuelve true si y solo si el buzon de mensajes ha llegado a su capacidad
     * maxima. 
     * 
     * @return true si el buzon esta lleno
     */
    public boolean isFull() {

        boolean resp = false;

        mutex.lock();

        resp = inBox.size() == maxSize;

        mutex.unlock();

        return resp;
    }
    
    /**
     * Devuelve true si y solo si el buzon de mensajes ha llegado a su capacidad
     * maxima. No coge el mutex para evitar bloqueos circulares
     * 
     * @return true si el buzon esta lleno
     */
    private boolean isFullNoMutex() {

        return inBox.size() == maxSize;
    }

    /**
     * Añade un mensaje al buzon mientras no este lleno. Si esta lleno, el
     * mensaje es descartado.
     * 
     * @param msg Objeto serializable que se añadira al buzon
     */
    public boolean addMsg(Serializable msg) {

        boolean resp = false;

        mutex.lock();

        if (!isFullNoMutex()) {

            inBox.addLast(msg);
            empty.signal();

            resp = true;
        }

        mutex.unlock();

        return resp;
    }

    /**
     * <b>Bloqueante.</b><br/>
     * <br/>
     * Extrae el primer mensaje del buzon. Si esta vacio, el proceso que lo
     * invoca se queda bloqueado hasta que se reciba algun mensaje.
     * 
     * @return Objeto Serializable con el contenido del mensaje.
     * 
     */
    public Serializable getMsg() {

        mutex.lock();

        Serializable result = null;

        // espera pasivamente a que exista algun mensaje en el buzon
        while (isEmpty()) {
            try {
                empty.await();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        // extrae el mensaje del buzon
        try{
            result = inBox.removeFirst();
        } 
        
        // si no hay elementos en el buzon (no deberia ocurrir)
        catch (NoSuchElementException e){
            System.err.println("ERROR: Buzon vacio");
        }
        
        
        mutex.unlock();

        return result;
    }

}
