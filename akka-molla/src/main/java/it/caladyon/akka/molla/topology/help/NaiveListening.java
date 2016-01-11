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
 * @author Luciano Boschi
 *
 */
public class NaiveListening extends BaseListening {

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
	 */
	protected void onMonoListening(MessageWrapper message) {
		inputsPut(message);
		execute0(message.getDateRef());
	}

	@Override
	public boolean isBeatable(Object message) {
		return inputs.isEmpty();
	}

}
