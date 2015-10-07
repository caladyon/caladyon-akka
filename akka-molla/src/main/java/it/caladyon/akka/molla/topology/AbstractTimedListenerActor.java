package it.caladyon.akka.molla.topology;

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
 * @author 16800028
 * @deprecated
 */
@Deprecated
public abstract class AbstractTimedListenerActor extends NaiveListenerActor {

	/** Default per {@link #deltaT} [secondi]. */
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