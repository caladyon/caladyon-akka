/**
 *
 */
package it.caladyon.akka.molla.msg;

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


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Immutable class.
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
