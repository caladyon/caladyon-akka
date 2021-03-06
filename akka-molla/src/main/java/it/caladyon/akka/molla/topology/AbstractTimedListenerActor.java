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


import java.util.Date;

/**
 *
 * <p>
 * Questo bean va inizializzato con
 * <ul>
 * <li><code>deltaT</code>: discretizzazione temporale in secondi (opzionale, default {@link #DEFAULT_DELTA_T})
 * (internamente viene memorizzato il valore convertito in millisecondi: {@link #milliDeltaT});
 * <li>parametri di {@link NaiveListenerActor}.
 * </ul>
 *
 * @author Luciano Boschi
 * @deprecated
 */
@Deprecated
public abstract class AbstractTimedListenerActor extends NaiveListenerActor {

	/** Default per {@link #milliDeltaT} [secondi]. */
	public static final long DEFAULT_DELTA_T = 60;

	/** Discretizzazione temporale [millisecondi]. */
	protected long milliDeltaT = DEFAULT_DELTA_T * 1000;

	/**
	 * @param deltaT the deltaT to set [seconds]
	 * @deprecated Spostato in AbstractTimedListening
	 */
	@Deprecated
	public final void setDeltaT(int deltaT) {
		this.milliDeltaT = deltaT * 1000;
	}

	/**
	 * @deprecated Spostato in AbstractTimedListening
	 */
	@Deprecated
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
	 * @deprecated Spostato in AbstractTimedListening
	 */
	@Deprecated
	protected final long calcInterval(Date d) {
		return calcInterval(d.getTime());
	}

	/**
	 * @deprecated Spostato in AbstractTimedListening
	 */
	@Deprecated
	protected final long calcInterval(MessageWrapper mw) {
		return calcInterval(mw.getDateRef());
	}

}