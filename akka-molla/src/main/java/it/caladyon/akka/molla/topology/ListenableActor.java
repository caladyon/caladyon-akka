/**
 *
 */
package it.caladyon.akka.molla.topology;

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


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import akka.actor.ActorRef;

/**
 * Attore ascoltabile: puo' essere immaginato come una sorgente di dati.
 *
 * @author Luciano Boschi
 * @since 1.0.4
 *
 */
abstract public class ListenableActor extends SpringActor {

	/**
	 * Messaggi gestiti dall'ascoltabile.
	 * @see 	ListenableMessage
	 */
	public static enum ListenableMessageType {
		/** Inviato da un ascoltatore che vuole ascoltare qualcuno. */
		I_WANNA_LISTEN,
		/** Inviato da un ascoltatore che non vuole piu' ascoltare qualcuno. */
		I_DONT_WANNA_LISTEN,
		/** Inviato da un ascoltabile che e' pronto ad essere ascoltato. */
		I_AM_LISTENABLE
	}

	/**
	 * Messaggio con indicazione di chi deve essere ascoltato.
	 * Immutabile.
	 *
	 * @author Luciano Boschi
	 * @since 05/feb/2015
	 *
	 */
	public static final class ListenableMessage {

		private final ActorRef ref;
		private final ListenableMessageType type;
		private final String listenableBeanName;

		public ListenableMessage(ActorRef ref, ListenableMessageType type, String listenableBeanName) {
			this.ref = ref;
			this.type = type;
			this.listenableBeanName = listenableBeanName;
		}

		/**
		 * @return the ref
		 */
		public final ActorRef getRef() {
			return ref;
		}

		/**
		 * @return the type
		 */
		public final ListenableMessageType getType() {
			return type;
		}

		/**
		 * @return the listenableBeanName
		 */
		public final String getListenableBeanName() {
			return listenableBeanName;
		}

	}


	/**
	 * Nella comunicazione ascoltatore-ascoltato, decora il messaggio con il bean-name del mittente.
	 * Le istanze sono immutabili.
	 */
	public static final class MessageWrapper {

		/** Bean name del sender (etichetta di riconoscimento). */
		private final String senderLabel;

		private final Date dateRef;

		/** Messaggio originale. */
		private final Object msg;

		/**
		 * Istanzia il messaggio wrappato.
		 *
		 * @param senderLabel
		 * @param dateRef
		 * @param msg
		 */
		public MessageWrapper(String senderLabel, Date dateRef, Object msg) {
			super();
			this.senderLabel = senderLabel;
			this.dateRef = dateRef;
			this.msg = msg;
		}

		/**
		 * @return the senderLabel
		 */
		public final String getSenderLabel() {
			return senderLabel;
		}

		/**
		 * @return the dateRef
		 */
		public final Date getDateRef() {
			return dateRef;
		}

		/**
		 * @return the msg
		 */
		public final Object getMsg() {
			return msg;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "MessageWrapper [sender=" + senderLabel + " @\"" + dateRef + "\" : " + msg.getClass().getSimpleName() + "]";
		}

	}

	/** Lista degli ascoltatori registrati. */
	private final List<ActorRef> listeners = new ArrayList<ActorRef>();

	/**
	 * Iscrizione all'event stream e pubblicazione di un "avviso di ascolto".
	 *
	 * @see akka.actor.UntypedActor#preStart()
	 */
	@Override
	public void preStart() throws Exception {
		super.preStart();
		eventStream.subscribe(getSelf(), ListenableMessage.class);
		eventStream.publish(new ListenableMessage(getSelf(), ListenableMessageType.I_AM_LISTENABLE, name));
	}

	/**
	 * Disiscrizione dall'event stream.
	 *
	 * @see akka.actor.UntypedActor#postStop()
	 */
	@Override
	public void postStop() throws Exception {
		super.postStop();
		eventStream.unsubscribe(getSelf());
	}

	/**
	 * Gestisce i messaggi per la registrazione/deregistrazione degli ascoltatori,
	 * cioe' messaggi di tipo {@link ListenableMessage}.
	 * Gli altri messaggi vengono passati a {@link #onReceive2(Object)}.
	 *
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	@Override
	protected final void onReceive1(Object msg) throws Exception {
		if (msg instanceof ListenableMessage) {
			ListenableMessage lmsg = (ListenableMessage) msg;
			switch (lmsg.getType()) {
			case I_WANNA_LISTEN:
				onIWannaListen(lmsg);
				break;
			case I_DONT_WANNA_LISTEN:
				onIDontWannaListen(lmsg);
				break;
			case I_AM_LISTENABLE:
				onIAmListening(lmsg);
				break;
			default:
				// do nothing
				break;
			}
		}
		else {
			onReceive2(msg);
		}
	}

	/**
	 * @param lmsg
	 *
	 * @since 05/feb/2015
	 */
	private final void onIWannaListen(ListenableMessage lmsg) {
		if (lmsg.getListenableBeanName().equals(name)) {
			ActorRef sender = lmsg.getRef();
			if (!listeners.contains(sender)) {
				listeners.add(sender);
				log.info(" is LISTENED by " + sender.toString());
			}
		}
	}

	/**
	 * @param lmsg
	 *
	 * @since 05/feb/2015
	 */
	private final void onIDontWannaListen(ListenableMessage lmsg) {
		if (lmsg.getListenableBeanName().equals(name)) {
			ActorRef sender = lmsg.getRef();
			listeners.remove(sender);
		}
	}

	/**
	 * Deve essere sovrascritto da {@link ListenerActor}.
	 *
	 * @param lmsg
	 *
	 * @since 05/feb/2015
	 */
	protected void onIAmListening(ListenableMessage lmsg) {
	}

	/**
	 * Sostituisce {@link #onReceive(Object)} per i messaggi non di tipo {@link ListenableMessage}.
	 *
	 * @param msg
	 *
	 * @since 14/gen/2015
	 */
	abstract protected void onReceive2(Object msg) throws Exception;

	/**
	 * Metodo del pattern.
	 * Il messaggio indicato viene in realta' inviato all'interno di un {@link MessageWrapper}.
	 *
	 * @param msg	Messaggio da wrappare e mandare a tutti gli ascoltatori.
	 *
	 * @since 14/gen/2015
	 */
	protected void tellAll(Date dRef, Object msg) {
		if (listeners != null && listeners.size() > 0) {
			MessageWrapper actual = new MessageWrapper(this.name, dRef, msg);
			for (ActorRef l : listeners) {
				l.tell(actual, getSelf());
			}
		}
	}

	@Override
	protected boolean isBeatable(Object message) {
		return  super.isBeatable(message)
				&& !(message instanceof ListenableMessage); // onReceive1
	}


}
