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
 * Questo bean va inizializzato con
 * <ul>
 * <li><code>deltaT</code>: discretizzazione temporale in secondi (opzionale, default {@link #DEFAULT_DELTA_T})
 * (internamente viene memorizzato il valore convertito in millisecondi: {@link #milliDeltaT});
 * <li>parametri di {@link NaiveListening}.
 * </ul>
 * <p>
 * TODO: prendere il default del <code>deltaT</code> da app.properties "globale"
 *
 * @author Luciano Boschi
 *
 */
public abstract class AbstractTimedListening extends NaiveListening {

	/** Default per {@link #milliDeltaT} [secondi]. */
	public static final long DEFAULT_DELTA_T = 60;

	/** Discretizzazione temporale [millisecondi]. */
	protected long milliDeltaT = DEFAULT_DELTA_T * 1000;

	/**
	 * @param deltaT the deltaT to set [seconds]
	 */
	public final void setDeltaT(int deltaT) {
		this.milliDeltaT = deltaT * 1000;
	}

	protected final long calcInterval(long millis) {
		return millis / milliDeltaT;
	}

	/**
	 * Returns the number of deltaT-wide time interval since January 1, 1970, 00:00:00 GMT
	 * represented by the given <code>Date</code> object.
	 *
	 * @see  Date#getTime()
	 *
	 * @param d
	 * @return
	 */
	protected final long calcInterval(Date d) {
		return calcInterval(d.getTime());
	}

	protected final long calcInterval(MessageWrapper mw) {
		return calcInterval(mw.getDateRef());
	}

}
