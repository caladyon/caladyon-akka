/**
 *
 */
package it.caladyon.akka.molla.msg;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe immutabile.
 *
 * @author Luciano Boschi
 *
 */
public class HeartBeat implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String beanname;

	private final long timestamp;

	private final long millis;

	/** http://en.wikipedia.org/wiki/Vital_signs */
	private final Map<String, Object> vitals;

	/**
	 * @param beanname
	 * @param timestamp
	 * @param millis
	 * @param vitals
	 */
	public HeartBeat(String beanname, long timestamp, long millis, Map<String, Object> vitals) {
		if (beanname == null) {
			throw new IllegalStateException("beanname is not set!");
		} else {
			this.beanname = beanname;
			this.timestamp = timestamp;
			this.millis = millis;
			if (vitals == null) {
				this.vitals = new HashMap<String, Object>();
			} else {
				this.vitals = vitals;
			}
		}
	}

	public HeartBeat(String beanname, long timestamp, long millis) {
		this(beanname, timestamp, millis, null);
	}

	/**
	 * @param beanname
	 * @param timestamp
	 */
	public HeartBeat(String beanname, long timestamp) {
		this(beanname, timestamp, -1, null);
	}

	public HeartBeat(String beanname) {
		this(beanname, System.currentTimeMillis(), -1, null);
	}

	/**
	 * @return the beanname
	 */
	public final String getBeanname() {
		return beanname;
	}

	/**
	 * @return the timestamp
	 */
	public final long getTimestamp() {
		return timestamp;
	}

	/**
	 * @return the vitals
	 */
	public final Map<String, Object> getVitals() {
		return vitals;
	}

	/**
	 * @return the millis
	 */
	public final long getMillis() {
		return millis;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "HeartBeat [beanname=" + beanname + ", timestamp=" + timestamp + ", millis=" + millis + ", vitals="
				+ vitals + "]";
	}

}
