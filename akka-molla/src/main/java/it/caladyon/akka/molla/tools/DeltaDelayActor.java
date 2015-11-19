/**
 *
 */
package it.caladyon.akka.molla.tools;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * <p>
 * Attore che introduce un ritardo di <code>delay</code> intervalli temporali.
 * Il ritardo calcolato e' dato da <code>delay</code> volte <code>deltaT</code> piu' <code>shift</code>,
 * rispetto all'inizio dell'intervallo temporale del messaggio arrivato.
 * Il messaggio viene poi mandato, con timestamp nuovo (cioe' quello dato dal calcolo del ritardo)
 * e con mittente uguale a quello del bean name di questo attore.
 * <p>
 * Questo bean va inizializzato con
 * <ul>
 * <li>{@link #deltaT}: Durata di uno step temporale [secondi]
 * (opzionale, default = {@value #DEFAULT_DELTAT}).
 * <li>{@link #delay}: Numero di delta-T da aspettare
 * (opzionale, default = {@value #DEFAULT_DELAY}).
 * <li>{@link #shift}: Secondi di attesa dopo l'inizio del delta-T di invio
 * (opzionale, default = {@value #DEFAULT_SHIFT}).
 * </ul>
 *
 * @author Luciano Boschi
 * @since 16/feb/2015
 *
 */
public class DeltaDelayActor extends AbstractTicketyActor {

	/** Default per {@link #deltaT} [secondi]. */
	public static final int DEFAULT_DELTAT = 60;

	/** Default per {@link #delay}. */
	public static final int DEFAULT_DELAY = 1;

	/** Default per {@link #shift} [secondi]. */
	public static final int DEFAULT_SHIFT = 0;

	/** Messaggi in attesa; la chiave e' il timestamp di invio. */
	private final Map<Long, Object> waitings = new TreeMap<Long, Object>();

	/** Durata di uno step temporale [secondi]. */
	private int deltaT = DEFAULT_DELTAT;

	/** Numero di delta-T da aspettare. */
	private int delay = DEFAULT_DELAY;

	/** Secondi di attesa dopo l'inizio del delta-T di invio. */
	private int shift = DEFAULT_SHIFT;

	/**
	 * @param deltaT the deltaT to set
	 */
	public final void setDeltaT(int deltaT) {
		this.deltaT = deltaT;
	}

	/**
	 * @param delay the delay to set
	 */
	public final void setDelay(int delay) {
		this.delay = delay;
	}

	/**
	 * @param shift the shift to set
	 */
	public final void setShift(int shift) {
		this.shift = shift;
	}

	@Override
	protected void manageTime() {
		long now = System.currentTimeMillis();

		Iterator<Entry<Long, Object>> it = waitings.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Long, Object> w = it.next();
			if (w.getKey() >= now) {
				tellAll(new Date(w.getKey()), w.getValue()); // viene spedito con il nuovo dateRef
				it.remove();
			}
		}

	}

	private long composeSendTimestamp(Date dateRef) {
		long r = dateRef.getTime();
		return r - (r % deltaT) + (deltaT * delay) + shift;
	}

	private void onListening(String senderLabel, Date dateRef, Object msg) {
		long key = composeSendTimestamp(dateRef);
		waitings.put(key, msg);
	}

	@Override
	protected final void onListening(MessageWrapper message) {
		onListening(message.getSenderLabel(), message.getDateRef(), message.getMsg());
	}

}
