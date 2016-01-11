/**
 *
 */
package it.caladyon.akka.spring.bean;

/*
 * #%L
 * AKKA-Spring4 Integration
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


import it.caladyon.akka.spring.SpringExtensionProvider;
import it.caladyon.akka.spring.SpringExtensionProvider.SpringExt;

import java.util.List;

import javax.annotation.PreDestroy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

/**
 * <p>
 * Bean per creare gli attori indicati.
 * <p>
 * Gli attori da creare devono essere definiti come bean "prototype", ma vengono logicamente gestiti come singleton.
 * Agli attori creati viene assegnato il nome del bean.
 *
 * @author Luciano Boschi 16800028
 * @since 0.1.0
 *
 */
public class ActorSystemStarter {

	/** Logger. */
	protected final Log log = LogFactory.getLog(getClass());

	@Autowired(required = true)
	protected ActorSystem system;

	public void setActors(List<String> actors) {
		// topology
		final SpringExt springExtProvider = SpringExtensionProvider.springExtProvider.get(system);

		for (String actorBeanName : actors) {
			ActorRef actor = system.actorOf(springExtProvider.props(actorBeanName), actorBeanName);
			log.info("Starting +++ " + actorBeanName + " + " + actor);
		}
	}

	@PreDestroy
	public void terminate() {
		log.info("Shutting down the AKKA actor system");
		system.shutdown();
		system.awaitTermination();
		log.info("AKKA actor system shut down");
	}
}
