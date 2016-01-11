/**
 *
 */
package it.caladyon.akka.spring.config;

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


import static it.caladyon.akka.spring.SpringExtensionProvider.springExtProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import akka.actor.ActorSystem;

/**
 * Codice basato su:
 * <ul>
 * <li>https://github.com/typesafehub/activator-akka-java-spring
 * <li>http://blog.nemccarthy.me/?p=272 (non per il codice ma per alcune considerazioni)
 * <ul>
 *
 * @author Luciano Boschi 16800028
 * @since 0.1.0
 *
 */
@Configuration
public class AkkaConf {

	public static final String AKKA_SYSTEM_NAME = "AkkaJavaSpring";

	public static final String BEAN_NAME_ACTOR_SYSTEM = "actorSystem";

	/** The application context is needed to initialize the Akka Spring Extension. */
	@Autowired
	private ApplicationContext applicationContext;

	/**
	 * Actor system singleton for this application.
	 * @return
	 */
	@Bean(name = BEAN_NAME_ACTOR_SYSTEM)
	public ActorSystem actorSystem() {
		ActorSystem system = ActorSystem.create(AKKA_SYSTEM_NAME);
		// initialize the application context in the Akka Spring Extension
		springExtProvider.get(system).initialize(applicationContext);
		return system;
	}
}
