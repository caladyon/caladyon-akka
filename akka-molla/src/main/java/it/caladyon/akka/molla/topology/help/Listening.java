/**
 *
 */
package it.caladyon.akka.molla.topology.help;

import it.caladyon.akka.molla.topology.ListenableActor.MessageWrapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import akka.event.LoggingAdapter;

/**
 * <p>
 * Definizione delle strategie di ascolto usate da StrategistListenerActor.
 * <p>
 * I bean che implementano questa interfaccia sono <i>stateful</i>,
 * per cui e' bene che vengano definiti come <b>prototype</b>.
 *
 * @author 16800028
 *
 */
public interface Listening {

	/**
	 * E' lo stesso StrategistListenerActor ad implementare questa interfaccia.
	 *
	 * @author 16800028
	 *
	 */
	public interface Callback {

		/**
		 * Metodo chiamato da {@link Listening#onListening(MessageWrapper)}.
		 *
		 * @param dateRef			Timestamp dell'<b>ultimo</b> messaggio arrivato.
		 */
		public void execute(Date dateRef);

	}

	/**
	 * Metodo usato da StrategistListenerActor.
	 * @param callback
	 */
	public void setCallback(Callback callback);

	/**
	 * Metodo usato da StrategistListenerActor.
	 * @param listenedActors
	 */
	public void setListenedActors(List<String> listenedActors);

	/**
	 * Metodo usato da StrategistListenerActor.
	 * @param log
	 */
	public void setLog(LoggingAdapter log);

	/**
	 * Metodo usato da StrategistListenerActor.
	 * @return
	 */
	public abstract Map<String, MessageWrapper> getInputs();

	/**
	 * Ascolto di un messaggio (metodo della strategia).
	 *
	 * @param message
	 */
	public void onListening(MessageWrapper message);

	/**
	 * Generazione di heartbeat (metodo della strategia).
	 *
	 * @param message
	 * @return
	 */
	public boolean isBeatable(Object message);

}
