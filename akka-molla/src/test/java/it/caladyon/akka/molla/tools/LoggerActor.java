package it.caladyon.akka.molla.tools;

import it.caladyon.akka.molla.msg.HeartBeat;

/**
 * Loggatore di battiti cardiaci.
 *
 * @author Luciano Boschi 16800028
 * @since 16/mar/2015
 *
 */
public class LoggerActor extends HeartBeatListeningActor {

	private int count = 0;

	private long totTime = 0;

	/* (non-Javadoc)
	 * @see it.infoblu.bit.trk.postfus.akka.tools.HeartBeatListeningActor#onReceive(java.lang.Object)
	 */
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