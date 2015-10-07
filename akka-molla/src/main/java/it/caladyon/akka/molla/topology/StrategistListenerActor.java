/**
 *
 */
package it.caladyon.akka.molla.topology;

import it.caladyon.akka.molla.msg.HeartBeat;
import it.caladyon.akka.molla.topology.help.Listening;
import it.caladyon.akka.molla.topology.help.TimedListening;

import java.util.Collections;
import java.util.Date;

import javax.annotation.PostConstruct;

/**
 * <p>
 * Questo bean va inizializzato con
 * <ul>
 * <li>{@link #strategy listening}: strategia di ascolto (opzionale, default definito in {@link #doPostConstruct()});
 * <li>parametri di {@link ListenerActor}.
 * </ul>
 * @author 16800028
 *
 */
public abstract class StrategistListenerActor extends ListenerActor implements Listening.Callback {

	/** Deve rimanere privato, impostato con {@link #setListening(Listening)}. */
	private Listening strategy = null;

	/**
	 * @param strategy the listening to set
	 */
	public final void setListening(Listening strategy) {
		this.strategy = strategy;
		strategy.setCallback(this);
		strategy.setListenedActors(Collections.unmodifiableList(listenedActors));
		strategy.setLog(log);
	}

	/**
	 * Definisce e imposta la strategia di default.
	 * Per definire un diverso default: sovrascrivere {@link #newListeningStrategy()}.
	 */
	@PostConstruct
	protected final void doPostConstruct() {
		log.debug("StrategistListenerActor post construct");
		if (strategy == null) {
			Listening l = newListeningStrategy();
			setListening(l);
		}
	}

	/**
	 * @return
	 */
	protected Listening newListeningStrategy() {
		return new TimedListening();
	}

	/* (non-Javadoc)
	 * @see it.infoblu.bit.trk.postfus.akka.topology.ListenerActor#onListening(it.infoblu.bit.trk.postfus.akka.topology.ListenableActor.MessageWrapper)
	 */
	@Override
	protected void onListening(MessageWrapper message) {
		strategy.onListening(message);
	}

	/**
	 * Invia lo {@link HeartBeat} solo quando viene invocato {@link #execute0(Date)}
	 * (che resetta {@link #inputs}):
	 * per attori multi-ascoltatore, questo equivale a dire che il battito viene inviato solo quando
	 * sono arrivati tutti i messaggi.
	 *
	 * @see it.infoblu.bit.trk.postfus.akka.topology.ListenerActor#isBeatable(java.lang.Object)
	 */
	@Override
	protected boolean isBeatable(Object message) {
		return strategy.isBeatable(message) && super.isBeatable(message);
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
	 */
	protected final MessageWrapper getMessageWrapper(String label) {
		if (listenedActors.contains(label)) {
			return strategy.getInputs().get(label);
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
			if (strategy.getInputs().isEmpty())
				return null;
			else
				return strategy.getInputs().values().iterator().next().getMsg();
	}

}
