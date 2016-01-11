/**
 *
 */
package it.caladyon.akka.molla.topology;

/*
 * #%L
 * akka-molla
 * %%
 * Copyright (C) 2015 - 2016 Luciano 'caladyon' Boschi
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */


import it.caladyon.akka.molla.topology.help.TimedListening;


/**
 * <p>
 * Questo ascoltatore ridefinisce la logica di gestione degli arrivi dei messaggi, per il caso "multi ascoltatore":
 * viene assicurata la consistenza dei dateRef dei vari messaggi mantenuti nel buffer interno a {@link NaiveListenerActor}
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
 * Nell'ultimo caso, non viene invocato il metodo {@link #execute(java.util.Date)}:
 * questo significa che le esecuzioni vengono avviate se e solo se arrivano i messaggi di <b>tutti</b> gli ascoltati
 * relativi ad uno stesso intervallo temporale, prima che arrivino messaggi relativi a intervalli temporali successivi.
 * <p>
 * Questo bean va inizializzato con
 * <ul>
 * <li>parametri di {@link AbstractTimedListenerActor}.
 * </ul>
 *
 * @author Luciano Boschi
 * @since 1.0.4
 *
 * @deprecated		Usare {@link StrategistListenerActor} con {@link TimedListening}
 */
@Deprecated
abstract public class TimedListenerActor extends AbstractTimedListenerActor {

	/**
	 * Decide l'azione da compiere in base ai timestamp del messaggio e dei messaggi gia' arrivati.
	 *
	 * @deprecated		Spostato in {@link TimedListening}
	 */
	@Deprecated
	@Override
	protected void onMultiListening(MessageWrapper message) {
		if (inputsIsEmpty()) {
			super.onMultiListening(message);
		} else {
			long intervalOfNewMessage = calcInterval(message);
			long intervalOfPresentMessages = getIntervalOfPresentMessages();

			if (intervalOfNewMessage == intervalOfPresentMessages) {
				super.onMultiListening(message);
			} else if (intervalOfNewMessage > intervalOfPresentMessages) {
				onEarlyListened(message);
				inputsReset();
				super.onMultiListening(message);
			} else { // intervalOfNewMessage < intervalOfPresentMessages
				onLateListened(message);
				// new message discarded
			}
		}
	}

	/**
	 * Viene chiamato nell'ipotesi che ci sia almeno un messaggio dentro <code>inputs</code>.
	 *
	 * @return
	 * @deprecated		Spostato in {@link TimedListening}
	 */
	@Deprecated
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

	/**
	 * <p>
	 * Metodo invocato quando arriva un messaggio con un orario relativo ad un intervallo temporale
	 * antecedente a quello dei messaggi arrivati precedentemente.
	 * Questa implementazione invia un log di warning.
	 * <p>
	 * Questo metodo <b>non</b> deve modificare il buffer.
	 *
	 * @param message		Il messaggio anticipatario.
	 * @deprecated
	 */
	@Deprecated
	protected void onEarlyListened(MessageWrapper message) {
		log.warning(appendInputMessages("Early: discarding " + inputsSize() + " prev messages!!! + " + message));
	}

	/**
	 * <p>
	 * Metodo invocato quando arriva un messaggio con un orario relativo ad un intervallo temporale
	 * antecedente a quello dei messaggi arrivati precedentemente.
	 * Questa implementazione invia un log di warning.
	 * <p>
	 * Questo metodo <b>non</b> deve modificare il buffer.
	 *
	 * @param message		Il messaggio ritardatario.
	 * @deprecated
	 */
	@Deprecated
	protected void onLateListened(MessageWrapper message) {
		log.warning(appendInputMessages("Late: discarded " + message + " !!!"));
	}

}
