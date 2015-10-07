/**
 *
 */
package it.caladyon.akka.molla.topology.help;

import it.caladyon.akka.molla.topology.ListenableActor.MessageWrapper;

import java.util.Date;

/**
 * <p>
 * Strategia di ascolto ingenua, dato che suppone che i messaggi arrivino tutti e nell'ordine giusto.
 * <p>
 * Il metodo <code>execute</code> viene chiamato:
 * <ul>
 * <li>per mono-ascoltatori: ad ogni messaggio arrivato dall'ascoltato;
 * <li>per multi-ascoltatori: quando sono arrivati i messaggi di tutti gli ascoltati,
 * indipendentemente dall'ordine e dalle differenze di <code>dateRef</code>.
 * </ul>
 *
 * @author 16800028
 *
 */
public class NaiveListening extends BaseListening {

	/* (non-Javadoc)
	 * @see it.infoblu.bit.trk.postfus.akka.topology.help.Listening#onListening(it.infoblu.bit.trk.postfus.akka.topology.ListenableActor.MessageWrapper)
	 */
	@Override
	public void onListening(MessageWrapper message) {
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
	 */
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
	 */
	protected void onMonoListening(MessageWrapper message) {
		inputsPut(message);
		execute0(message.getDateRef());
	}

	/* (non-Javadoc)
	 * @see it.infoblu.bit.trk.postfus.akka.topology.help.Listening#isBeatable(java.lang.Object)
	 */
	@Override
	public boolean isBeatable(Object message) {
		return inputs.isEmpty();
	}

}
