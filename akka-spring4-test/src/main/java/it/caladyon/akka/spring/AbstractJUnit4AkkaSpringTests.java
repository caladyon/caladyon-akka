/**
 *
 */
package it.caladyon.akka.spring;

/*
 * #%L
 * akka-spring4-test
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
import org.junit.AfterClass;
import org.junit.Before;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;

/**
 * @author 16800028
 *
 */
public abstract class AbstractJUnit4AkkaSpringTests extends AbstractJUnit4SpringContextTests {

	protected final Log log = LogFactory.getLog(getClass());

	protected static ActorSystem system = null;

	/**
	 * Non posso usare <code>@BeforeClass</code> perche' applicationContext non e' statico,
	 * pero' system va inizializzato una sola volta.
	 *
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		if (system == null)
			system = (ActorSystem) applicationContext.getBean("actorSystem");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDown() throws Exception {
		if (system != null) {
			JavaTestKit.shutdownActorSystem(system);
			system = null;
		}
	}

}
