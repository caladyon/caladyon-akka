/**
 *
 */
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


import it.caladyon.akka.molla.msg.HeartBeat;

/**
 * Actor bean per i test di integrazione del sistema di monitoraggio.
 *
 * @author Luciano Boschi
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

	@Override
	public void onReceive(Object message) throws Exception {
		super.onReceive(message);
		if (message instanceof HeartBeat) {
			++total;
		}
	}

}
