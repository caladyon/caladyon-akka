/**
 *
 */
package it.caladyon.akka.molla.topology;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

/**
 * <p>
 * Attore ascoltatore/ascoltabile: agisce come un elemento di processamento dei dati all'interno (o al termine)
 * di una catena di operazioni.
 * <p>
 * Questo bean va inizializzato con
 * <ul>
 * <li>{@link #listenedActors}: lista ordinata di bean-name degli attori che devono essere ascoltati.
 * </ul>
 * L'ordine degli attori ascoltati nella lista serve a determinare il loro ruolo
 * (in ascoltatori che fanno questa distinzione, si veda ad esempio {@link BaseOldDeltaBenchmarkActor},
 * magari confrontandolo con attori che non fanno distinzione di ruoli, ad esempio {@link DummyListenerActor}).
 * <p>
 * Ci sono varie strategie per gestire gli arrivi dei messaggi da piu' ascoltatori,
 * implementate in varie classi <code>*ListenerActor</code>, in questo stesso package.
 * TODO: trasformare i vari ListenerActor in uno strategy pattern, come bean da iniettare in questo attore!!!
 *
 * @author Luciano Boschi 16800028
 * @since 1.0.4
 *
 */
abstract public class ListenerActor extends ListenableActor {

	/** Bean name degli attori ascoltati. */
	protected List<String> listenedActors;

	/**
	 * @param listenedActors the listenedActors to set
	 *
	 * @throws	ClassCastException		Se il bean associato al nome listenedActor non e' un attore.
	 * @throws	NullPointerException	Se non c'e' nessun bean associato al nome listenedActor.
	 *
	 */
	@Required
	public final void setListenedActors(String... listenedActors) {
		this.listenedActors = Arrays.asList(listenedActors);
	}

	/* (non-Javadoc)
	 * @see it.infoblu.bit.trk.postfus.akka.topology.ListenableActor#preStart()
	 */
	@Override
	public void preStart() throws Exception {
		super.preStart();
		for (String listenedActor : listenedActors) {
			eventStream.publish(new ListenableMessage(getSelf(), ListenableMessageType.I_WANNA_LISTEN, listenedActor));
		}
	}

	/* (non-Javadoc)
	 * @see it.infoblu.bit.trk.postfus.akka.topology.ListenableActor#postStop()
	 */
	@Override
	public void postStop() throws Exception {
		super.postStop();
		for (String listenedActor : listenedActors) {
			eventStream.publish(new ListenableMessage(getSelf(), ListenableMessageType.I_DONT_WANNA_LISTEN, listenedActor));
		}
	}

	@Override
	protected final void onIAmListening(ListenableMessage lmsg) {
		if (listenedActors.contains(lmsg.getListenableBeanName())) {
			lmsg.getRef().tell(new ListenableMessage(getSelf(), ListenableMessageType.I_WANNA_LISTEN,
					lmsg.getListenableBeanName()), getSelf());
		}
	}

	/* (non-Javadoc)
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	@Override
	protected final void onReceive2(Object message) throws Exception {
		if (message instanceof ListenableActor.MessageWrapper) {
			onMessageWrapper((ListenableActor.MessageWrapper) message);
		} else {
			onReceive3(message);
		}
	}

	/**
	 * Ricezione di un messaggio da parte di un {@link ListenableActor}.
	 *
	 * @see MessageWrapper
	 *
	 * @param message	MessageWrapper contenente il messaggio spedito dal {@link ListenableActor} ascoltato.
	 */
	private final void onMessageWrapper(MessageWrapper message) {
		if (listenedActors.contains(message.getSenderLabel())) {
			onListening(message);
		} else {
			onNotListening(message);
		}
	}

	/**
	 * @param message
	 */
	abstract protected void onListening(MessageWrapper message);

	/**
	 * Ricezione di un messaggio da un mittente qualsiasi: invio alla dead letter mailbox.
	 * Sovrascrivendo questo metodo, si puo' aggiungere la gestione di altri tipi di messaggi.
	 *
	 * @param message		Messaggio arrivato.
	 */
	protected void onReceive3(Object message) throws Exception {
		unhandled(message);
	}

	/**
	 * Gestione dei messaggi arrivati da mittenti non ascoltati:
	 * per i messaggi provenienti da mittenti non ascoltati, viene prodotto un log di warning.
	 * Naturalmente il metodo puo' essere sovrascritto (ad esempio: per usare la dead letter mailbox di AKKA).
	 *
	 * @see #onListening(String, Object)
	 *
	 * @param senderLabel
	 * @param msg
	 *
	 * @since 23/gen/2015
	 */
	protected void onNotListening(MessageWrapper message) {
		log.warning(name + ": WRONG SENDER " + message.getSenderLabel() + ": arrived " + message.getMsg()
				+ " ("+ message.getMsg().getClass() + ") @ " + message.getDateRef());
	}

	/* (non-Javadoc)
	 * @see it.infoblu.bit.trk.postfus.akka.topology.ListenableActor#isBeatable(java.lang.Object)
	 */
	@Override
	protected boolean isBeatable(Object message) {
		return  super.isBeatable(message)
				&& message instanceof MessageWrapper // onReceive2
				&& listenedActors.contains(((MessageWrapper) message).getSenderLabel()) // onListening
				;
	}

}
