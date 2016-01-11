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
 * Loggatore di battiti cardiaci.
 *
 * @author Luciano Boschi
 * @since 16/mar/2015
 *
 */
public class LoggerActor extends HeartBeatListeningActor {

	private int count = 0;

	private long totTime = 0;

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof HeartBeat) {
			++count;
			totTime += ((HeartBeat) message).getMillis();

			if (count <= 10 || count % 1000 == 0)
				log.info(count + ") " + message.toString());
		} else {
			log.warning("UNHANDLED: " + message);
			unhandled(message);
		}
	}

	public void printStats() {
		log.info("Stats:\ncount = " + count + "\ntotal time = " + totTime + " ms\naverage time = "
				+ (totTime / (double) count) + " ms");
	}
}