/**
 *
 */
package it.caladyon.akka.molla.tools;

import it.caladyon.akka.molla.msg.HeartBeat;

/**
 * Actor bean per i test di integrazione del sistema di monitoraggio.
 *
 * @author 16800028
 *
 */
public class HeartBeatTestingActor extends HeartBeatListeningActor {

	/** Conteggio degli HeartBeat arrivati. */
	protected long total = 0;

	/**
	 * @return the total
	 */
	public final long getTotal() {
		return total;
	}

	/**
	 * @param total the total to set
	 */
	public final void setTotal(long total) {
		this.total = total;
	}

	/* (non-Javadoc)
	 * @see it.infoblu.bit.trk.postfus.akka.tools.HeartBeatListeningActor#onReceive(java.lang.Object)
	 */
	@Override
	public void onReceive(Object message) throws Exception {
		super.onReceive(message);
		if (message instanceof HeartBeat) {
			++total;
		}
	}

}
