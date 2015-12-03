/*
 * AUTOR: Juan Vela y Marta Frias
 * NIA: 643821 - 535621
 * FICHERO: TotalOrderMulticast.java
 * TIEMPO: 2 horas
 * DESCRIPCION: Sistema de mensajes multicast con orden total
 */

package ssdd.p4.ms;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Clase que gestiona un sistema de mensajes multicast con orden total sobre un
 * sistema de mensajes simple. Cada instancia dispone de su propio sistema de
 * mensajes, con las operaciones de enviar multicast y de recibir multicast.
 * 
 * @author Juan Vela
 * @author Marta Frias
 *
 */
public class TotalOrderMulticast {

	private MessageSystem msystem;

	/**
	 * Mensaje que el usuario desea enviar (NO es un mensaje de sicronizacion).
	 * 
	 */
	Serializable msg;

	/** Estado de la peticion de entrada a la seccion critica. */
	private boolean reqCS;

	/** Tiempo logico de la peticion en curso. */
	private int reqTime;

	/** Numero de ACK's pendientes de envio para la peticion en curso. */
	private int reqLeftAcks;

	/**
	 * Lista que contiene los mensajes de sincronizacion pendientes de envio
	 * para una peticion en curso.
	 * 
	 */
	List<Envelope> deferred;

	/** Lista que contiene la localizacion del resto de procesos. */
	List<Neighbor> neighbors;

	/** Candado */
	private final Lock mutex;

	/** Condicion no ocupado */
	private final Condition libre;

	/**
	 * Crea una instancia de TotalOrderMulticast.
	 * 
	 * @param ms
	 *            MessageSystem del proceso.
	 */
	public TotalOrderMulticast(MessageSystem ms) {
		msystem = ms;
		neighbors = msystem.getNeighbors();
		deferred = new LinkedList<Envelope>();
		reqCS = false;
		reqLeftAcks = -1;
		reqTime = -1;

		mutex = new ReentrantLock();
		libre = mutex.newCondition();
	}

	/**
	 * <b>Bloqueante.</b><br/>
	 * <br/>
	 * 
	 * Envia un mensaje de solicitud de sincronizacion a cada proceso,
	 * incluyendo el identificador del origen, y el reloj logico del mismo.<br/>
	 * 
	 * El mensaje del usuario se guarda para su posterior envio, cuando haya
	 * consenso.<br/>
	 * 
	 * ( Esto ultimo se realiza en {@link #receiveMulticast()} )<br/>
	 * 
	 * 
	 * <br/>
	 * Si hay alguna peticion en curso, se <b>bloquea</b> hasta se termine la
	 * anterior.<br/>
	 * 
	 * @param message
	 *            : Mensaje que el usuario desea enviar.
	 * 
	 * @return Lista de identificadores de los procesos <b>NO</b> conectados.
	 * 
	 * @see {@link #receiveMulticast()}
	 * 
	 */
	public LinkedList<Integer> sendMulticast(Serializable message) {

		mutex.lock();

		// Espera mientras haya una peticion en curso (ocupado)
		while (reqCS) {
			try {
				libre.await();

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// Lista de identificadores de procesos desconectados
		LinkedList<Integer> disconnected = new LinkedList<Integer>();

		// Guarda el mensaje que el usuario quiere enviar
		// para su posterior envio
		msg = message;

		// Obtener reloj logico de MessageSystem
		reqTime = msystem.getLogicTime();

		// Peticion iniciada
		reqCS = true;

		// No hace falta descontar uno porque neighbors no incluye el actual
		reqLeftAcks = neighbors.size();

		for (Neighbor n : neighbors) {
			if (!msystem.send(n.getId(), new Req(), reqTime)) {
				disconnected.add(n.getId());
			}
		}

		// Si hay error no hace falta seguir con la peticion de SC
		if (disconnected.size() > 0) {
			reqCS = false;
			reqLeftAcks = -1;
			reqTime = -1;
		}

		mutex.unlock();

		return disconnected;
	}

	/**
	 * Gestiona la sincronizacion entre todos los procesos. <br/>
	 * Cuando hay consenso se envia a todos el mensaje del usuario.<br/>
	 * 
	 * @return Un mensaje recibido.
	 * 
	 * @see {@link #sendMulticast(Serializable message)}
	 * 
	 */
	public Envelope receiveMulticast() {
		while (true) {

			Envelope e = msystem.receive();

			// Llega peticion de entrada en SC
			if (e.getPayload() instanceof Req) {

				// Contestar o meter en cola

				boolean esMenor = reqTime < e.getTimeStamp()
						|| (reqTime == e.getTimeStamp() && (e.getDestination() < e
								.getSource()));

				if (reqCS && esMenor) {

					// Retrasa el envio del ACK
					deferred.add(e);
				} else {

					// Envia ACK
					msystem.send(e.getSource(), new Ack(), e.getTimeStamp());
				}

				// Llega confirmacion a peticion de entrada en SC
			} else if (e.getPayload() instanceof Ack) {

				reqLeftAcks--;

				if (reqLeftAcks == 0) {

					// SC
					mutex.lock();

					// Envia mensaje del usuario
					for (Neighbor n : neighbors) {
						msystem.send(n.getId(), msg, reqTime);
					}

					// Salir de SC
					for (Envelope d : deferred) {
						// Envio retrasado de ACK
						msystem.send(d.getSource(), new Ack(), d.getTimeStamp());
					}

					Envelope result = new Envelope(msystem.getId(),
							msystem.getId(), msg);
					result.setTimeStamp(reqTime);

					deferred.clear();
					reqLeftAcks = -1;
					reqTime = -1;

					reqCS = false;
					libre.signal();

					mutex.unlock();

					// Devuelve el mensaje enviado para que sea adjuntado
					// en el chat como cualquier otro
					return result;
				}

			} else {
				// Devuelve mensaje recibido para que sea adjuntado en el chat
				return e;
			}
		}
	}

}
