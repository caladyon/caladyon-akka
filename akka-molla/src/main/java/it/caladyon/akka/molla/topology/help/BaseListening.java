/**
 *
 */
package it.caladyon.akka.molla.topology.help;

import it.caladyon.akka.molla.topology.ListenableActor.MessageWrapper;
import it.caladyon.akka.molla.topology.ListenerActor;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import akka.event.LoggingAdapter;


/**
 * @author Luciano Boschi
 *
 */
abstract public class BaseListening implements Listening {

	/** Adattatore di AKKA. */
	protected LoggingAdapter log;

	protected Callback callback;

	protected List<String> listenedActors;

	/**
	 * Mappa: listened actor bean name - its last message.
	 * Le classi derivate devono accedervi solo attraverso la sua vista non modificabile: {@link #inputs}
	 * e i metodi
	 * <ul>
	 * <li>{@link #inputsPut(MessageWrapper)}
	 * <li>{@link #getInput(int)}
	 * <li>{@link #getMessageWrapper(String) getMessageWrapper(*)}
	 * <li>...
	 * </ul>
	 */
	private final Map<String, MessageWrapper> myInputs;

	/** Unmodifiable view of {@link #myInputs}. */
	protected final Map<String, MessageWrapper> inputs;

	/**
	 * Costruttore base, istanzia le strutture dati interne.
	 */
	public BaseListening() {
		super();
		myInputs = new HashMap<String, MessageWrapper>();
		inputs = Collections.unmodifiableMap(myInputs);
	}

	@Override
	public final void setCallback(Callback callback) {
		this.callback = callback;
	}

	/**
	 * @param listenedActors the listenedActors to set
	 */
	@Override
	public final void setListenedActors(List<String> listenedActors) {
		this.listenedActors = listenedActors;
	}

	@Override
	public final void setLog(LoggingAdapter log) {
		this.log = log;
	}

	/**
	 * @return the inputs
	 */
	@Override
	public final Map<String, MessageWrapper> getInputs() {
		return inputs;
	}

	/**
	 * Chiama {@link it.caladyon.akka.molla.topology.help.Listening.Callback#execute(Date)} e poi resetta {@link #myInputs}.
	 *
	 * @param dateRef			Timestamp dell'<b>ultimo</b> messaggio arrivato.
	 *
	 */
	protected final void execute0(Date dateRef) {
		callback.execute(dateRef);
		inputsReset();
	}

	/////////////////////////////////////////////////////////////////
	//  gestione di inputs                                         //
	/////////////////////////////////////////////////////////////////

	/**
	 * @param message
	 */
	protected final void inputsPut(MessageWrapper message) {
		myInputs.put(message.getSenderLabel(), message);
	}

//	protected final boolean inputsIsEmpty() {
//		return myInputs.isEmpty();
//	}

//	protected final int inputsSize() {
//		return myInputs.size();
//	}

	/**
	 * Indica se e' gia' presente un messaggio proveniente dal mittente indicato.
	 *
	 * @param sender
	 * @return
	 */
	protected final boolean hasPresentMessage(String sender) {
		return myInputs.containsKey(sender);
	}

	/**
	 * Metodo utile per aggiungere info ai messaggi di log.
	 * Aggiunge una riga "mittente => messaggio" (includendo anche i mittenti senza messaggio).
	 *
	 * @param msg
	 * @return
	 */
	protected final String appendInputMessages(String msg) {
		StringBuilder sb = new StringBuilder(msg);
		for (String a : listenedActors) {
			sb.append("\n  ").append(a).append(" => ").append(myInputs.get(a));
		}
		return sb.toString();
	}

	/**
	 *
	 */
	protected final void inputsReset() {
		myInputs.clear();
	}

	/**
	 * Effettua {@link Map#remove(Object)} dopo aver controllato la presenza di label tra gli attori ascoltati.
	 *
	 * @param label
	 * @return		il valore ritornato da {@link Map#remove(Object)}
	 */
	protected final MessageWrapper inputsRemove(String label) {
		if (listenedActors.contains(label)) {
			return myInputs.remove(label);
		} else {
			throw new IllegalStateException("No listened actor with name \"" + label + "\"!");
		}
	}

	/**
	 * Prende uno dei <b>{@link MessageWrapper}</b> arrivati.
	 * E' una funzione a sostegno di {@link #getInput(int)} e {@link #getMessageWrapper(int)}.
	 *
	 * @param label		Etichette dei mittenti (contenute in <code>listenedActors</code>).
	 * @return
	 *
	 * @throws		IllegalStateException		Quando l'etichetta data non e' contenuta in {@link ListenerActor}<code>#listenedActors</code>.
	 *
	 * @since 22/gen/2015
	 */
	protected final MessageWrapper getMessageWrapper(String label) {
		if (listenedActors.contains(label)) {
			return myInputs.get(label);
		} else {
			throw new IllegalStateException("No listened actor with name \"" + label + "\"!");
		}
	}

	/**
	 * Prende uno dei <b>messaggi</b> di input, gia' estratto dal {@link MessageWrapper}.
	 * Per ottenere il {@link MessageWrapper}, si usi {@link #getMessageWrapper(int)}.
	 *
	 * @param index		Indice dell'attore ascoltato (si veda <code>listenedActors</code>).
	 * @return
	 *
	 * @throws		IndexOutOfBoundsException		L'indice fornito non e' valido.
	 *
	 */
	protected final Object getInput(int index) {
		return getMessageWrapper(index).getMsg();
	}

	/**
	 * Prende uno dei <b>{@link MessageWrapper}</b> arrivati.
	 *
	 * @param index		Indice dell'attore ascoltato (si veda <code>listenedActors</code>).
	 * @return
	 * @throws		IndexOutOfBoundsException		L'indice fornito non e' valido.
	 */
	protected final MessageWrapper getMessageWrapper(int index) {
		return getMessageWrapper(listenedActors.get(index));
	}

}
