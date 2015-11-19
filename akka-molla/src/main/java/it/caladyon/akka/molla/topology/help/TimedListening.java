/**
 *
 */
package it.caladyon.akka.molla.topology.help;

import it.caladyon.akka.molla.topology.ListenableActor.MessageWrapper;

/**
 * <p>
 * Questa strategia ridefinisce la logica di gestione degli arrivi dei messaggi, per il caso "multi ascoltatore":
 * viene assicurata la consistenza dei dateRef dei vari messaggi mantenuti nel buffer interno a {@link NaiveListening}
 * cioe' in ogni momento, i dateRef di tutti i messaggi appartengono <b>ad uno stesso intervallo temporale</b>,
 * (la cui ampiezza e' data da {@link #milliDeltaT}) oppure il buffer e' vuoto.
 * <p>
 * All'arrivo di un nuovo messaggio, viene calcolato l'intervallo temporale di appartenenza e confrontato con quello
 * dei messaggi gia' presenti nel buffer:
 * <ul>
 * <li>se il buffer e' vuoto, il messaggio viene inserito nel buffer;
 * <li>se uguale, il messaggio viene aggiunto nel buffer;
 * <li>se precedente, il messaggio viene scartato;
 * <li>se successivo, il buffer viene svuotato e il nuovo messaggio viene inserito nel buffer.
 * </ul>
 * Nell'ultimo caso, non viene invocato il metodo <code>execute</code>:
 * questo significa che le esecuzioni vengono avviate se e solo se arrivano i messaggi di <b>tutti</b> gli ascoltati
 * relativi ad uno stesso intervallo temporale, prima che arrivino messaggi relativi a intervalli temporali successivi.
 * <p>
 * Questo bean va inizializzato con
 * <ul>
 * <li>parametri di {@link AbstractTimedListening}.
 * </ul>
 *
 * @author Luciano Boschi
 *
 */
public class TimedListening extends AbstractTimedListening {

	/**
	 * Decide l'azione da compiere in base ai timestamp del messaggio e dei messaggi gia' arrivati.
	 *
	 * @see NaiveListening#onMultiListening(MessageWrapper)
	 */
	@Override
	protected void onMultiListening(MessageWrapper message) {
		if (inputs.isEmpty()) {
			super.onMultiListening(message);
		} else {
			long intervalOfNewMessage = calcInterval(message);
			long intervalOfPresentMessages = getIntervalOfPresentMessages();

			if (intervalOfNewMessage == intervalOfPresentMessages) {
				super.onMultiListening(message);
			} else if (intervalOfNewMessage > intervalOfPresentMessages) {
				log.warning(appendInputMessages("Early: discarding " + inputs.size() + " prev messages!!! + " + message));
				inputsReset();
				super.onMultiListening(message);
			} else { // intervalOfNewMessage < intervalOfPresentMessages
				log.warning(appendInputMessages("Late: discarded " + message + " !!!"));
				// new message discarded
			}
		}
	}

	/**
	 * Viene chiamato nell'ipotesi che ci sia almeno un messaggio dentro <code>inputs</code>.
	 *
	 * @return
	 */
	private long getIntervalOfPresentMessages() {
		long rv = -1;
		for (String name : listenedActors) {
			MessageWrapper mw = getMessageWrapper(name);
			if (mw != null) {
				rv = calcInterval(mw);
				break;
			}
		}
		return rv;
	}

}
