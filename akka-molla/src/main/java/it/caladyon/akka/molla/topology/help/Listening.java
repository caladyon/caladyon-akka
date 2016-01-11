/**
 *
 */
package it.caladyon.akka.molla.topology.help;

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
 * @author Luciano Boschi
 *
 */
public interface Listening {

	/**
	 * E' lo stesso StrategistListenerActor ad implementare questa interfaccia.
	 *
	 * @author Luciano Boschi
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
