/**
 *
 */
package it.caladyon.akka.molla.topology;

import it.caladyon.akka.molla.msg.HeartBeat;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Ascoltatore ingenuo, dato che suppone che i messaggi arrivino tutti e nell'ordine giusto.
 * <p>
 * Le classi derivate devono implementare la logica in {@link #execute(Date)}.
 * Tale metodo viene chiamato:
 * <ul>
 * <li>per mono-ascoltatori: ad ogni messaggio arrivato dall'ascoltato;
 * <li>per multi-ascoltatori: quando sono arrivati i messaggi di tutti gli ascoltati,
 * indipendentemente dall'ordine e dalle differenze di <code>dateRef</code>.
 * </ul>
 *
 * @author Luciano Boschi 16800028
 * @since 1.0.4
 * @deprecated		Sostituita da StrategicListenerActor con NaiveListening
 */
@Deprecated
abstract public class NaiveListenerActor extends ListenerActor {

	/**
	 * Mappa: listened actor bean name - its last message.
	 * Le classi derivate devono accedervi solo dai metodi
	 * {@link #getMessageWrapper(String)}, {@link #getMonoInput()}, inputs*, eccetera.
	 * @deprecated	Spostato in BaseListening
	 */
	@Deprecated
	private final Map<String, MessageWrapper> inputs = new HashMap<String, MessageWrapper>();

	/**
	 * @deprecated	Spostato in BaseListening
	 */
	@Deprecated
	private final void inputsPut(MessageWrapper message) {
		inputs.put(message.getSenderLabel(), message);
	}

	/**
	 * @deprecated	Spostato in BaseListening
	 */
	@Deprecated
	protected final boolean inputsIsEmpty() {
		return inputs.isEmpty();
	}

	/**
	 * @deprecated	Spostato in BaseListening
	 */
	@Deprecated
	protected final int inputsSize() {
		return inputs.size();
	}

	/**
	 * Indica se e' gia' presente un messaggio proveniente dal mittente indicato.
	 *
	 * @param sender
	 * @return
	 * @deprecated	Spostato in BaseListening
	 */
	@Deprecated
	protected final boolean hasPresentMessage(String sender) {
		return inputs.containsKey(sender);
	}

	/**
	 * Metodo utile per aggiungere info ai messaggi di log.
	 * Aggiunge una riga "mittente => messaggio" (includendo anche i mittenti senza messaggio).
	 *
	 * @param msg
	 * @return
	 * @deprecated	Spostato in BaseListening
	 */
	@Deprecated
	protected final String appendInputMessages(String msg) {
		StringBuilder sb = new StringBuilder(msg);
		for (String a : listenedActors) {
			sb.append("\n  ").append(a).append(" => ").append(inputs.get(a));
		}
		return sb.toString();
	}

	/**
	 * @deprecated	Spostato in BaseListening
	 */
	@Deprecated
	protected final void inputsReset() {
		//inputs = new HashMap<String, MessageWrapper>();
		inputs.clear();
	}

	/**
	 * Effettua {@link Map#remove(Object)} dopo aver controllato la presenza di label tra gli attori ascoltati.
	 *
	 * @param label
	 * @return		il valore ritornato da {@link Map#remove(Object)}
	 * @deprecated	Spostato in BaseListening
	 */
	@Deprecated
	protected final MessageWrapper inputsRemove(String label) {
		if (listenedActors.contains(label)) {
			return inputs.remove(label);
		} else {
			throw new IllegalStateException("No listened actor with name \"" + label + "\"!");
		}
	}

	/**
	 * @deprecated		Spostato in NaiveListening.
	 */
	@Deprecated
	@Override
	protected final void onListening(MessageWrapper message) {
		if (listenedActors.size() == 1) {
			onMonoListening(message);
		} else {
			onMultiListening(message);
		}
	}

	/**
	 * Logica di ricezione di un messaggio, in caso di multi-ascoltatore.
	 * Inserisce il messaggio e, quando ci sono tutti i messaggi attesi, chiama {@link #execute0(Date)}.
	 *
	 * @see it.infoblu.bit.trk.postfus.akka.tools.LoggerActor#onMultiListening(java.lang.String, java.util.Date, java.lang.Object)
	 * @deprecated		Spostato in NaiveListening.
	 */
	@Deprecated
	protected void onMultiListening(MessageWrapper message) {
		inputsPut(message);
		if (inputs.size() == listenedActors.size()) {
			execute0(message.getDateRef());
		}
	}

	/**
	 * Logica di ricezione di un messaggio, in caso di mono-ascoltatore.
	 * Inserisce il messaggio e chiama {@link #execute0(Date)}.
	 *
	 * @see it.infoblu.bit.trk.postfus.akka.tools.LoggerActor#onMonoListening(java.lang.String, java.util.Date, java.lang.Object)
	 * @deprecated		Spostato in NaiveListening.
	 */
	@Deprecated
	protected void onMonoListening(MessageWrapper message) {
		inputsPut(message);
		execute0(message.getDateRef());
	}

	/**
	 * Chiama {@link #execute(Date)} e poi resetta {@link #inputs}.
	 * Metodo invocato da {@link #onMonoListening(String, Date, Object)}
	 * e {@link #onMultiListening(String, Date, Object)}.
	 *
	 * @param dateRef			Timestamp dell'<b>ultimo</b> messaggio arrivato.
	 *
	 * @since 22/gen/2015
	 * @deprecated		Spostato in BaseListening.
	 */
	@Deprecated
	protected final void execute0(Date dateRef) {
		execute(dateRef);
		inputsReset();
	}

	/**
	 * Metodo chiamato da {@link #execute0(Date)}.
	 *
	 * @param dateRef			Timestamp dell'<b>ultimo</b> messaggio arrivato.
	 * @deprecated		Spostato in Listening.Callback.
	 */
	@Deprecated
	abstract protected void execute(Date dateRef);

	/**
	 * Restituisce l'unico <b>messaggio</b> di input registrato, gia' estratto dal {@link MessageWrapper}.
	 * Questo metodo serve solo in un attore "mono-ascoltatore";
	 * per attori "multi-ascoltatore", usare {@link #getInput(int)}.
	 *
	 * @return
	 *
	 * @throws		IllegalStateException		Se la lista degli ascoltatori non ha un singolo valore.
	 *
	 * @since 22/gen/2015
	 */
	protected final Object getMonoInput() {
		if (listenedActors.size() > 1)
			throw new IllegalStateException("getMonoInput called for a \"multi\"-listener!");
		else
			if (inputs.isEmpty())
				return null;
			else
				return inputs.values().iterator().next().getMsg();
	}

	/**
	 * Prende uno dei <b>{@link MessageWrapper}</b> arrivati.
	 * E' una funzione a sostegno di {@link #getInput(int)} e {@link #getMessageWrapper(int)}.
	 *
	 * @param label		Etichette dei mittenti (contenute in <code>listenedActors</code>).
	 * @return
	 *
	 * @throws		IllegalStateException		Quando l'etichetta data non e' contenuta in {@link ListenerActor#listenedActors}.
	 *
	 * @since 22/gen/2015
	 * @deprecated		Spostato in BaseListening.
	 */
	@Deprecated
	protected final MessageWrapper getMessageWrapper(String label) {
		if (listenedActors.contains(label)) {
			return inputs.get(label);
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
	 * @deprecated		Spostato in BaseListening.
	 */
	@Deprecated
	protected final Object getInput(int index) {
		return getMessageWrapper(index).getMsg();
	}

	/**
	 * Prende uno dei <b>{@link MessageWrapper}</b> arrivati.
	 *
	 * @param index		Indice dell'attore ascoltato (si veda <code>listenedActors</code>).
	 * @return
	 * @throws		IndexOutOfBoundsException		L'indice fornito non e' valido.
	 * @deprecated		Spostato in BaseListening.
	 */
	@Deprecated
	protected final MessageWrapper getMessageWrapper(int index) {
		return getMessageWrapper(listenedActors.get(index));
	}

	/**
	 * Invia lo {@link HeartBeat} solo quando viene invocato {@link #execute0(Date)}
	 * (che resetta {@link #inputs}):
	 * per attori multi-ascoltatore, questo equivale a dire che il battito viene inviato solo quando
	 * sono arrivati tutti i messaggi.
	 *
	 * @see it.infoblu.bit.trk.postfus.akka.topology.ListenerActor#isBeatable(java.lang.Object)
	 * @deprecated		Spostato in StrategistListenerActor & NaiveListening.
	 */
	@Deprecated
	@Override
	protected boolean isBeatable(Object message) {
		return inputs.size() == 0 && super.isBeatable(message);
	}


}
