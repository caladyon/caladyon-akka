/**
 *
 */
package it.caladyon.akka.molla.topology.help;

import java.util.ArrayList;
import java.util.List;

import it.caladyon.akka.molla.topology.ListenableActor.MessageWrapper;


/**
 * Questa classe e' stata progettata per essere un'estensione di {@link TimedListening}:
 * con {@link #tolerance} a zero, i comportamenti devono essere gli stessi.
 * <p>
 * Questo bean va inizializzato con
 * <ul>
 * <li>{@link #tolerance}: Margine di differenza tra gli intervalli temporali dei nuovi messaggi
 * e di quelli gia' arrivati (opzionale, default = {@link #DEFAULT_TOLERANCE});
 * <li>parametri di {@link AbstractTimedListening}.
 * </ul>
 *
 * @author Luciano Boschi
 *
 */
public class TolerantTimedListening extends AbstractTimedListening {

	/** Default per {@link #tolerance}. */
	private static final short DEFAULT_TOLERANCE = 0;

	/** Margine di differenza tra gli intervalli temporali dei nuovi messaggi e di quelli gia' arrivati. */
	protected short tolerance = DEFAULT_TOLERANCE;

	/**
	 * @param tolerance the tolerance to set
	 */
	public final void setTolerance(short tolerance) {
		this.tolerance = tolerance;
	}

	/**
	 * Decide l'azione da compiere in base ai timestamp del messaggio e dei messaggi gia' arrivati.
	 *
	 * Si veda <code>NaiveListenerActor#onMultiListening(MessageWrapper)</code>
	 */
	@Override
	protected void onMultiListening(MessageWrapper message) {
		if (inputs.isEmpty()) {
			if (log.isDebugEnabled()) log.debug("First: inserted " + message);
			super.onMultiListening(message);
		} else {
			long intervalOfNewMessage = calcInterval(message);

			long timestampMin = Long.MAX_VALUE;
			long timestampMax = -1;
			for (String sender : listenedActors) {
				MessageWrapper mw = getMessageWrapper(sender);
				if (mw != null) {
					if (mw.getDateRef().getTime() < timestampMin) {
						timestampMin = mw.getDateRef().getTime();
					}
					if (mw.getDateRef().getTime() > timestampMax) {
						timestampMax = mw.getDateRef().getTime();
					}
				}
			}

			long intervalMin = calcInterval(timestampMin);
			long intervalMax = calcInterval(timestampMax);
			long intervalMinPlusTolerance = intervalMin + tolerance;
			long intervalMaxPlusTolerance = intervalMax + tolerance;
			//long intervalMinMinusTolerance = intervalMin - tolerance;
			long intervalMaxMinusTolerance = intervalMax - tolerance;

			if (intervalOfNewMessage > intervalMaxPlusTolerance) {
				// reset + insert
				log.warning(appendInputMessages("Early: discarding ALL " + inputs.size() + " prev messages!!! + " + message));
				inputsReset();
				super.onMultiListening(message);

			} else if (intervalOfNewMessage > intervalMinPlusTolerance) {
				// filter + insert
				List<String> removeable = new ArrayList<String>();
				for (String label : listenedActors) {
					MessageWrapper mw = getMessageWrapper(label);
					if (mw != null && calcInterval(mw) < intervalOfNewMessage - tolerance) {
						removeable.add(label);
					}
				}
				log.warning(appendInputMessages("Early: discarding " + removeable.size() + "/" + inputs.size() + " prev messages!!! + " + message));
				for (String label : removeable) {
					inputsRemove(label);
				}
				super.onMultiListening(message);
//				int oldSize = inputsSize();
//				for (String label : listenedActors) {
//					MessageWrapper mw = getMessageWrapper(label);
//					if (mw != null && calcInterval(mw) < intervalOfNewMessage - tolerance) {
//						inputsRemove(label);
//					}
//				}
//				log.warning(appendInputMessages("Early: discarded " + (oldSize - inputsSize()) + "/" + oldSize + " prev messages!!! + " + message));
//				super.onMultiListening(message);

			} else if (intervalOfNewMessage > intervalMax) {
				// insert
				if (log.isDebugEnabled()) log.debug(appendInputMessages("Newest: inserted " + message));
				super.onMultiListening(message);

			} else if (intervalOfNewMessage >= intervalMaxMinusTolerance) {
				// smart insert
				MessageWrapper oldmsg = getMessageWrapper(message.getSenderLabel());
				if (oldmsg == null || oldmsg.getDateRef().before(message.getDateRef())) {
					// insert
					if (log.isDebugEnabled()) log.debug(appendInputMessages("Relatively new: inserted " + message));
					super.onMultiListening(message);
				} else {
					// discard
					log.warning(appendInputMessages("Relatively late: discarded " + message + " !!!"));
				}

			} else {
				// discard
				log.warning(appendInputMessages("Absolutely late: discarded " + message + " !!!"));
			}
		}
	}

}