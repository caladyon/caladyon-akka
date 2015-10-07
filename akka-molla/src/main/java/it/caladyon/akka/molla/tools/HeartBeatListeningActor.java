/**
 *
 */
package it.caladyon.akka.molla.tools;

import it.caladyon.akka.molla.msg.HeartBeat;
import it.caladyon.akka.molla.msg.StatRequest;

import java.util.HashMap;
import java.util.Map;

import akka.actor.UntypedActor;
import akka.event.EventStream;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * @author 16800028
 *
 */
public class HeartBeatListeningActor extends UntypedActor {

	/** Logger asincrono di AKKA. */
	protected final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	/** Ultimi {@link HeartBeat} arrivati, secondo il loro bean-name. */
	protected final Map<String, HeartBeat> beats = new HashMap<String, HeartBeat>();

	private EventStream eventStream;

	/* (non-Javadoc)
	 * @see akka.actor.UntypedActor#preStart()
	 */
	@Override
	public void preStart() throws Exception {
		super.preStart();
		eventStream = getContext().system().eventStream();
		eventStream.subscribe(getSelf(), HeartBeat.class);
	}

	/* (non-Javadoc)
	 * @see akka.actor.UntypedActor#postStop()
	 */
	@Override
	public void postStop() throws Exception {
		super.postStop();
		eventStream.unsubscribe(getSelf());
	}

	/* (non-Javadoc)
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof HeartBeat) {
			//log.debug("New heart beat: " + message);
			beats.put(((HeartBeat) message).getBeanname(), (HeartBeat) message);
		} else if (message instanceof StatRequest) {
			//log.debug("New Stat Request");
			getSender().tell(new HashMap<String, HeartBeat>(beats), getSelf());
			//log.debug("Stat Request processed");
//		} else {
//			unhandled(message);
		}
	}

}
