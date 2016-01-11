/**
 *
 */
package it.caladyon.akka.commonslog;

/*
 * #%L
 * akka-commonslog
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


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import scala.collection.mutable.StringBuilder;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.Logging.Debug;
import akka.event.Logging.Error;
import akka.event.Logging.Info;
import akka.event.Logging.InitializeLogger;
import akka.event.Logging.LogEvent;
import akka.event.Logging.Warning;

/**
 * @author Luciano Boschi 16800028
 * @since 28/gen/2015
 *
 */
public class CommonsLoggingLogger extends UntypedActor {

	/** Logger. */
	private final Log loggerLog = LogFactory.getLog(this.getClass());

	/* (non-Javadoc)
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof InitializeLogger) {
			loggerLog.info(message);
			getSender().tell(Logging.loggerInitialized(), getSelf());
		} else if (message instanceof Error) {
			Log log = LogFactory.getLog(((Error) message).logClass());
			if (((Error) message).cause() == null) {
				log.error(composeMessage((LogEvent) message));
			} else {
				log.error(composeMessage((LogEvent) message), ((Error) message).cause());
			}
		} else if (message instanceof Warning) {
			Log log = LogFactory.getLog(((Warning) message).logClass());
			log.warn(composeMessage((LogEvent) message));
		} else if (message instanceof Info) {
			Log log = LogFactory.getLog(((Info) message).logClass());
			if (log.isInfoEnabled()) {
				log.info(composeMessage((LogEvent) message));
			}
		} else if (message instanceof Debug) {
			Log log = LogFactory.getLog(((Debug) message).logClass());
			if (log.isDebugEnabled()) {
				log.debug(composeMessage((LogEvent) message));
			}
		}
	}

	/**
	 * @param message
	 * @return
	 *
	 * @since 28/gen/2015
	 */
	private String composeMessage(Logging.LogEvent message) {
		return new StringBuilder().append('[').append(message.logSource()).append("] ").append(message.message()).toString();
	}

}
