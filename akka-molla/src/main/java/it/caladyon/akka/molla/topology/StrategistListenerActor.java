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


import java.util.Collections;

import javax.annotation.PostConstruct;

import it.caladyon.akka.molla.msg.HeartBeat;
import it.caladyon.akka.molla.topology.help.Listening;
import it.caladyon.akka.molla.topology.help.TimedListening;

/**
 * <p>
 * Questo bean va inizializzato con
 * <ul>
 * <li>{@link #strategy listening}: strategia di ascolto (opzionale, default definito in {@link #newListeningStrategy()});
 * <li>parametri di {@link ListenerActor}.
 * </ul>
 * @author Luciano Boschi
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
	 * @return		Default per {@link #doPostConstruct()}.
	 */
	protected Listening newListeningStrategy() {
		return new TimedListening();
	}

	@Override
	protected void onListening(MessageWrapper message) {
		strategy.onListening(message);
	}

	/**
	 * Invia lo {@link HeartBeat} solo quando viene invocato execute0(Date)
	 * (che resetta inputs):
	 * per attori multi-ascoltatore, questo equivale a dire che il battito viene inviato solo quando
	 * sono arrivati tutti i messaggi.
	 *
	 * @see ListenerActor#isBeatable(java.lang.Object)
	 */
	@Override
	protected boolean isBeatable(Object message) {
		return strategy.isBeatable(message) && super.isBeatable(message);
	}

	/**
	 * Prende uno dei <b>{@link it.caladyon.akka.molla.topology.ListenableActor.MessageWrapper}</b> arrivati.
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
	 * Prende uno dei <b>messaggi</b> di input,
	 * gia' estratto dal {@link it.caladyon.akka.molla.topology.ListenableActor.MessageWrapper}.
	 * Per ottenere il <code>MessageWrapper</code>, si usi {@link #getMessageWrapper(int)}.
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
	 * Prende uno dei <b>{@link it.caladyon.akka.molla.topology.ListenableActor.MessageWrapper}</b> arrivati.
	 *
	 * @param index		Indice dell'attore ascoltato (si veda <code>listenedActors</code>).
	 * @return
	 * @throws		IndexOutOfBoundsException		L'indice fornito non e' valido.
	 */
	protected final MessageWrapper getMessageWrapper(int index) {
		return getMessageWrapper(listenedActors.get(index));
	}

	/**
	 * Restituisce l'unico <b>messaggio</b> di input registrato,
	 * gia' estratto dal {@link it.caladyon.akka.molla.topology.ListenableActor.MessageWrapper}.
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
