/**
 *
 */
package it.caladyon.akka.spring;

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


import org.springframework.context.ApplicationContext;

import akka.actor.Actor;
import akka.actor.IndirectActorProducer;

/**
 * @author Luciano Boschi 16800028
 * @since 0.1.0
 *
 */
public class SpringActorProducer implements IndirectActorProducer {

	private final ApplicationContext applicationContext;

	private final String actorBeanName;

	/**
	 * Costruttore con tutti i campi.
	 *
	 * @param applicationContext
	 * @param actorBeanName
	 */
	public SpringActorProducer(ApplicationContext applicationContext, String actorBeanName) {
		super();
		this.applicationContext = applicationContext;
		this.actorBeanName = actorBeanName;
	}

	/* (non-Javadoc)
	 * @see akka.actor.IndirectActorProducer#actorClass()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends Actor> actorClass() {
		return (Class<? extends Actor>) applicationContext.getType(actorBeanName);
	}

	/* (non-Javadoc)
	 * @see akka.actor.IndirectActorProducer#produce()
	 */
	@Override
	public Actor produce() {
		return (Actor) applicationContext.getBean(actorBeanName);
	}

}
