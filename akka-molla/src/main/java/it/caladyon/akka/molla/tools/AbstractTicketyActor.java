package it.caladyon.akka.molla.tools;

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


import it.caladyon.akka.molla.topology.ListenerActor;

import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import akka.actor.ActorSystem;
import akka.actor.Cancellable;

/**
 * <p>
 * Questo bean va inizializzato con
 * <ul>
 * <li>{@link #seconds}: numero di secondi per la schedulazione dei messaggi {@link TicketyToc}
 * (opzionale, default = {@value #DEFAULT_SECONDS}).
 * </ul>
 *
 *
 * @author Luciano Boschi
 * @since 16/feb/2015
 *
 */
public abstract class AbstractTicketyActor extends ListenerActor {

	/**
	 * Messaggio "Tic-tac".
	 */
	public static class TicketyToc {}

	/** Default per {@link #seconds}. */
	private static final long DEFAULT_SECONDS = 10;

	/** Numero di secondi per lo scheduling impostato in {@link #preStart()}. */
	private long seconds = DEFAULT_SECONDS;

	/** Riferimento alla schedulazione dei messaggi {@link TicketyToc}. */
	private Cancellable scheduling = null;

	/**
	 * @param seconds the seconds to set
	 */
	public final void setSeconds(long seconds) {
		this.seconds = seconds;
	}

	/**
	 * Usa lo scheduler di Akka per autoinviarsi messaggi {@link TicketyToc}.
	 */
	@Override
	public void preStart() throws Exception {
		super.preStart();
		final ActorSystem system = getContext().system();
		scheduling = system.scheduler().schedule(Duration.Zero(), Duration.create(seconds, TimeUnit.SECONDS),
				getSelf(), new TicketyToc(), system.dispatcher(), null);
	}

	/**
	 * Disabilita la schedulazione prima di morire.
	 */
	@Override
	public void postStop() throws Exception {
		super.postStop();
		if (scheduling != null) {
			scheduling.cancel();
			scheduling = null;
		}
	}

	/**
	 * Per messaggi di tipo {@link TicketyToc}, chiama {@link #manageTime()};
	 * altrimenti il metodo definito nella classe madre.
	 */
	@Override
	protected void onReceive3(Object message) throws Exception {
		if (message instanceof TicketyToc) {
			manageTime();
		} else {
			super.onReceive3(message);
		}
	}

	/**
	 * Chiamato ad ogni tic-tac.
	 *
	 *
	 * @since 16/feb/2015
	 */
	protected abstract void manageTime();

}