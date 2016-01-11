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


import static akka.pattern.Patterns.ask;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.springframework.test.context.ContextConfiguration;

import akka.actor.Props;
import akka.testkit.TestActorRef;
import it.caladyon.akka.molla.msg.HeartBeat;
import it.caladyon.akka.molla.tools.HeartBeatTestingActor;
import it.caladyon.akka.molla.topology.ListenableActor.MessageWrapper;
import it.caladyon.akka.spring.AbstractJUnit4AkkaSpringTests;
import it.caladyon.akka.spring.SpringExtensionProvider;
import it.caladyon.akka.spring.SpringExtensionProvider.SpringExt;
import it.caladyon.akka.spring.config.AkkaConf;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

/**
 * Si veda il manuale di Akka, capitolo 3.9.1 / Testing the Actor's Behavior.
 *
 * @author Luciano Boschi
 * @deprecated		Sostituito da Test_StrategistListenerActor_with_NaiveListening.
 */
@Deprecated
@ContextConfiguration(classes = Test_NaiveListenerActor.class)
@Configuration
@Import(AkkaConf.class)
public class Test_NaiveListenerActor extends AbstractJUnit4AkkaSpringTests {

	private static final int TIMEOUT = 500;

	/**
	 * Classe di test (concretizzazione di {@link TimedListenerActor}).
	 *
	 * @author Luciano Boschi
	 *
	 */
	public static class TestNaiveListenerActor extends NaiveListenerActor {

		private int execCount = 0;

		/**
		 * @return the execCount
		 */
		public final int getExecCount() {
			return execCount;
		}

		@Override
		protected void execute(Date dateRef) {
			log.info("EXECUTING: " + dateRef);
			++execCount;
			getSender().tell(dateRef, getSelf());
		}

	}

	// Spring Configuration - inizio

	public static final String SOURCE1_BEAN_NAME = "source1-actor";
	public static final String SOURCE2_BEAN_NAME = "source2-actor";
	public static final String SOURCE3_BEAN_NAME = "source3-actor";

	public static final String MONO_LISTENER_BEAN_NAME = "naive-mono-listener-actor";
	public static final String MULTI_LISTENER_BEAN_NAME = "naive-multi-listener-actor";

	public static final String PROBE_ACTOR_BEAN_NAME = "probe-actor";

	@Bean(name = MONO_LISTENER_BEAN_NAME)
	@Scope("prototype")
	public TestNaiveListenerActor getMonoListener() {
		TestNaiveListenerActor rv = new TestNaiveListenerActor();
		rv.setListenedActors(SOURCE1_BEAN_NAME);
		return rv;
	}

	@Bean(name = MULTI_LISTENER_BEAN_NAME)
	@Scope("prototype")
	public TestNaiveListenerActor getMultiListener() {
		TestNaiveListenerActor rv = new TestNaiveListenerActor();
		rv.setListenedActors(SOURCE1_BEAN_NAME, SOURCE2_BEAN_NAME, SOURCE3_BEAN_NAME);
		return rv;
	}

	@Bean(name = PROBE_ACTOR_BEAN_NAME)
	@Scope("prototype")
	public HeartBeatTestingActor getHBProbeActor() {
		HeartBeatTestingActor rv = new HeartBeatTestingActor();
		return rv;
	}

	// Spring Configuration - fine

	/**
	 * Controllo della generazione degli {@link HeartBeat} giusti.
	 */
	@Test
	public void test_HeartBeat_3x1() {
		log.info("---- test_HeartBeat_3x1 -------------------------------");

		// questo metodo di creazione delle Props e' ripreso da ActorSystemStarter
		final SpringExt springExtProvider = SpringExtensionProvider.springExtProvider.get(system);
		final Props props = springExtProvider.props(MULTI_LISTENER_BEAN_NAME);
		final TestActorRef<TestNaiveListenerActor> ref = TestActorRef.create(system, props, "test_3");
		final TestNaiveListenerActor actor = ref.underlyingActor();

		final TestActorRef<HeartBeatTestingActor> proberef = TestActorRef.create(system,
				springExtProvider.props(PROBE_ACTOR_BEAN_NAME), "probe_0");
		final HeartBeatTestingActor probe = proberef.underlyingActor();

		final Date d1 = new Date();
		final MessageWrapper mw1 = new MessageWrapper(SOURCE1_BEAN_NAME, d1, "message1");
		final Future<Object> future1 = ask(ref, mw1, TIMEOUT);

		final Date d2 = new Date();
		final MessageWrapper mw2 = new MessageWrapper(SOURCE2_BEAN_NAME, d2, "message2");
		final Future<Object> future2 = ask(ref, mw2, TIMEOUT);

		final Date d3 = new Date();
		final MessageWrapper mw3 = new MessageWrapper(SOURCE3_BEAN_NAME, d3, "message3");
		final Future<Object> future3 = ask(ref, mw3, TIMEOUT);

		try {
			Thread.sleep(TIMEOUT + 100);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		assertTrue("future1", future1.isCompleted());
		assertTrue("future2", future2.isCompleted());
		assertTrue("future3", future3.isCompleted());

		try {
			assertTrue("response3", Await.result(future3, Duration.Zero()) == d3);
		} catch (Exception e) {
			e.printStackTrace();
		}

		assertTrue("execution count", actor.getExecCount() == 1);

		assertTrue("number of beats: " + probe.getTotal(), probe.getTotal() == 1);

		proberef.stop();
		ref.stop();

	}

	/**
	 * Controllo della generazione degli {@link HeartBeat} giusti.
	 */
	@Test
	public void test_HeartBeat_1xN() {
		log.info("---- test_HeartBeat_1xN -------------------------------");

		// questo metodo di creazione delle Props e' ripreso da ActorSystemStarter
		final SpringExt springExtProvider = SpringExtensionProvider.springExtProvider.get(system);
		final Props props = springExtProvider.props(MONO_LISTENER_BEAN_NAME);
		final TestActorRef<TestNaiveListenerActor> ref = TestActorRef.create(system, props, "test");
		final TestNaiveListenerActor actor = ref.underlyingActor();

		final TestActorRef<HeartBeatTestingActor> proberef = TestActorRef.create(system,
				springExtProvider.props(PROBE_ACTOR_BEAN_NAME), "probe");
		final HeartBeatTestingActor probe = proberef.underlyingActor();

		int number = (int) (Math.random() * 20);
		log.info("number = " + number);
		List<Future<Object>> futures = new ArrayList<Future<Object>>();
		for (int i = 0; i < number; ++i) {
			final Date d1 = new Date();
			final MessageWrapper mw1 = new MessageWrapper(SOURCE1_BEAN_NAME, d1, "message1");
			futures.add(ask(ref, mw1, TIMEOUT));
		}

		try {
			Thread.sleep(TIMEOUT + 100);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		for (int i = 0; i < number; ++i) {
			assertTrue("future"+i, futures.get(i).isCompleted());
		}

		assertTrue("execution count", actor.getExecCount() == number);
		assertTrue("number of beats: " + probe.getTotal(), probe.getTotal() == actor.getExecCount());

		proberef.stop();
		ref.stop();

	}

}
