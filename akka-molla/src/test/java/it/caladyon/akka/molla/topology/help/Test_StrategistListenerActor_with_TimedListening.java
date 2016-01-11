/**
 *
 */
package it.caladyon.akka.molla.topology.help;

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

import it.caladyon.akka.spring.AbstractJUnit4AkkaSpringTests;
import it.caladyon.akka.spring.SpringExtensionProvider;
import it.caladyon.akka.spring.SpringExtensionProvider.SpringExt;
import it.caladyon.akka.spring.config.AkkaConf;
import it.caladyon.akka.molla.msg.HeartBeat;
import it.caladyon.akka.molla.tools.HeartBeatTestingActor;
import it.caladyon.akka.molla.tools.Test_DeltaDelayActor;
import it.caladyon.akka.molla.topology.ListenableActor.MessageWrapper;
import it.caladyon.akka.molla.topology.StrategistListenerActor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.springframework.test.context.ContextConfiguration;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.Props;
import akka.testkit.TestActorRef;

/**
 * Si veda il manuale di Akka, capitolo 3.9.1 / Testing the Actor's Behavior.
 *
 * @author Luciano Boschi
 *
 */
@ContextConfiguration(classes = Test_StrategistListenerActor_with_TimedListening.class)
@Configuration
@Import(AkkaConf.class)
public class Test_StrategistListenerActor_with_TimedListening extends AbstractJUnit4AkkaSpringTests {

	/** [millisecondi] */
	private static final int WAIT = 200;

	/** Aumentare in caso di debug! [millisecondi] */
	private static final int TIMEOUT = WAIT;

	/**
	 * Classe di test (concretizzazione di {@link StrategistListenerActor}).
	 *
	 * @author Luciano Boschi
	 *
	 */
	public static class TestTimedListenerActor
	extends Test_StrategistListenerActor_with_NaiveListening.TestNaiveListenerActor {

		@Override
		protected Listening newListeningStrategy() {
			strategy2 = new TimedListening();
			return strategy2;
		}

	}

	// Spring Configuration - inizio

	public static final String SOURCE1_BEAN_NAME = "source1-actor";
	public static final String SOURCE2_BEAN_NAME = "source2-actor";
	public static final String SOURCE3_BEAN_NAME = "source3-actor";

	public static final String LISTENER_BEAN_NAME = "timed-listener-actor";

	public static final String PROBE_ACTOR_BEAN_NAME = "probe-actor";

	@Bean(name = LISTENER_BEAN_NAME)
	@Scope("prototype")
	public TestTimedListenerActor getSegmCorrectionActor() {
		TestTimedListenerActor rv = new TestTimedListenerActor();
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
	 * Risposta a 3 messaggi contemporanei.
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void test_0() throws Exception {
		log.info("---- test 0 -------------------------------");

		// questo metodo di creazione delle Props e' ripreso da ActorSystemStarter
		final SpringExt springExtProvider = SpringExtensionProvider.springExtProvider.get(system);
		final Props props = springExtProvider.props(LISTENER_BEAN_NAME);
		final TestActorRef<TestTimedListenerActor> ref = TestActorRef.create(system, props, "test_0");
		final TestTimedListenerActor actor = ref.underlyingActor();

		try {
			final Date d1 = new Date(2015, Calendar.FEBRUARY, 27, 12, 00, 00);
			final MessageWrapper mw1 = new MessageWrapper(SOURCE1_BEAN_NAME, d1, "message1");
			final Future<Object> future1 = ask(ref, mw1, TIMEOUT);

			final Date d2 = new Date(2015, Calendar.FEBRUARY, 27, 12, 00, 01);
			final MessageWrapper mw2 = new MessageWrapper(SOURCE2_BEAN_NAME, d2, "message2");
			final Future<Object> future2 = ask(ref, mw2, TIMEOUT);

			final Date d3 = new Date(2015, Calendar.FEBRUARY, 27, 12, 00, 02);
			final MessageWrapper mw3 = new MessageWrapper(SOURCE3_BEAN_NAME, d3, "message3");
			final Future<Object> future3 = ask(ref, mw3, TIMEOUT);

			try {
				Thread.sleep(WAIT + 100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

			assertTrue("future1", future1.isCompleted());
			assertTrue("future2", future2.isCompleted());
			assertTrue("future3", future3.isCompleted());

			assertTrue("response3", Await.result(future3, Duration.Zero()) == d3);

			assertTrue("execution count: " + actor.getExecCount(), actor.getExecCount() == 1);
			int inputsSize = ((TimedListening) actor.getListening()).inputs.size();
			assertTrue("input size: " + inputsSize, inputsSize == 0);
		} finally{
			ref.stop();
		}
	}

	/**
	 * Arrivo di 3 messaggi dello stesso mittente nello stesso minuto (non dovrebbe capitare nella realta').
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void test_1() {
		log.info("---- test 1 -------------------------------");

		// questo metodo di creazione delle Props e' ripreso da ActorSystemStarter
		final SpringExt springExtProvider = SpringExtensionProvider.springExtProvider.get(system);
		final Props props = springExtProvider.props(LISTENER_BEAN_NAME);
		final TestActorRef<TestTimedListenerActor> ref = TestActorRef.create(system, props, "test_1");
		final TestTimedListenerActor actor = ref.underlyingActor();

		try {
			List<Date> rd = new ArrayList<Date>();
			List<MessageWrapper> mv = new ArrayList<MessageWrapper>();
			List<Future<Object>> f = new ArrayList<Future<Object>>();
			int c = 0;

			rd.add(new Date(2015, Calendar.FEBRUARY, 19, 17, 0, 10));
			mv.add(new MessageWrapper(SOURCE1_BEAN_NAME, rd.get(c), "message-"+c));
			f.add(ask(ref, mv.get(c), TIMEOUT));
			++c;

			rd.add(new Date(2015, Calendar.FEBRUARY, 19, 17, 0, 20));
			mv.add(new MessageWrapper(SOURCE1_BEAN_NAME, rd.get(c), "message-"+c));
			f.add(ask(ref, mv.get(c), TIMEOUT));
			++c;

			rd.add(new Date(2015, Calendar.FEBRUARY, 19, 17, 0, 30));
			mv.add(new MessageWrapper(SOURCE1_BEAN_NAME, rd.get(c), "message-"+c));
			f.add(ask(ref, mv.get(c), TIMEOUT));
			++c;

			try {
				Thread.sleep(WAIT + 100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

			for (int i = 0; i < f.size(); ++i) {
				assertTrue("future " + i, f.get(i).isCompleted());
			}

			assertTrue("execution count", actor.getExecCount() == 0);
			int inputsSize = ((TimedListening) actor.getListening()).inputs.size();
			assertTrue("input size: " + inputsSize, inputsSize == 1);
		} finally {
			ref.stop();
		}
	}

	/**
	 * Arrivo di 3 messaggi dei 3 mittenti in 3 minuti differenti.
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void test_2() {
		log.info("---- test 2 -------------------------------");

		// questo metodo di creazione delle Props e' ripreso da ActorSystemStarter
		final SpringExt springExtProvider = SpringExtensionProvider.springExtProvider.get(system);
		final Props props = springExtProvider.props(LISTENER_BEAN_NAME);
		final TestActorRef<TestTimedListenerActor> ref = TestActorRef.create(system, props, "test_2");
		final TestTimedListenerActor actor = ref.underlyingActor();

		try {
			List<Date> rd = new ArrayList<Date>();
			List<MessageWrapper> mv = new ArrayList<MessageWrapper>();
			List<Future<Object>> f = new ArrayList<Future<Object>>();
			int c = 0;

			rd.add(new Date(2015, Calendar.FEBRUARY, 19, 17, 0, 10));
			mv.add(new MessageWrapper(SOURCE1_BEAN_NAME, rd.get(c), "message-"+c));
			f.add(ask(ref, mv.get(c), TIMEOUT));
			++c;

			rd.add(new Date(2015, Calendar.FEBRUARY, 19, 17, 1, 20));
			mv.add(new MessageWrapper(SOURCE2_BEAN_NAME, rd.get(c), "message-"+c));
			f.add(ask(ref, mv.get(c), TIMEOUT));
			++c;

			rd.add(new Date(2015, Calendar.FEBRUARY, 19, 17, 2, 30));
			mv.add(new MessageWrapper(SOURCE3_BEAN_NAME, rd.get(c), "message-"+c));
			f.add(ask(ref, mv.get(c), TIMEOUT));
			++c;

			try {
				Thread.sleep(WAIT + 100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

			for (int i = 0; i < f.size(); ++i) {
				assertTrue("future " + i, f.get(i).isCompleted());
			}

			assertTrue("execution count", actor.getExecCount() == 0);
			int inputsSize = ((TimedListening) actor.getListening()).inputs.size();
			assertTrue("input size: " + inputsSize, inputsSize == 1);
		} finally {
			ref.stop();
		}
	}

	/**
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void test_3() throws Exception {
		log.info("---- test 3 -------------------------------");

		// questo metodo di creazione delle Props e' ripreso da ActorSystemStarter
		final SpringExt springExtProvider = SpringExtensionProvider.springExtProvider.get(system);
		final Props props = springExtProvider.props(LISTENER_BEAN_NAME);
		final TestActorRef<TestTimedListenerActor> ref = TestActorRef.create(system, props, "test_3");
		final TestTimedListenerActor actor = ref.underlyingActor();

		try {
			List<Date> rd = new ArrayList<Date>();
			List<MessageWrapper> mv = new ArrayList<MessageWrapper>();
			List<Future<Object>> f = new ArrayList<Future<Object>>();
			int c = 0;
			int m = 0;

			rd.add(new Date(2015, Calendar.FEBRUARY, 19, 17, m, 10));
			mv.add(new MessageWrapper(SOURCE1_BEAN_NAME, rd.get(c), "message-"+c));
			f.add(ask(ref, mv.get(c), TIMEOUT));
			++c;
			++m;

			rd.add(new Date(2015, Calendar.FEBRUARY, 19, 17, m, 20));
			mv.add(new MessageWrapper(SOURCE2_BEAN_NAME, rd.get(c), "message-"+c));
			f.add(ask(ref, mv.get(c), TIMEOUT));
			++c;

			rd.add(new Date(2015, Calendar.FEBRUARY, 19, 17, m, 30));
			mv.add(new MessageWrapper(SOURCE3_BEAN_NAME, rd.get(c), "message-"+c));
			f.add(ask(ref, mv.get(c), TIMEOUT));
			++c;

			rd.add(new Date(2015, Calendar.FEBRUARY, 19, 17, m, 10));
			mv.add(new MessageWrapper(SOURCE1_BEAN_NAME, rd.get(c), "message-"+c));
			f.add(ask(ref, mv.get(c), TIMEOUT));
			++c;
			++m;

			rd.add(new Date(2015, Calendar.FEBRUARY, 19, 17, m, 30));
			mv.add(new MessageWrapper(SOURCE3_BEAN_NAME, rd.get(c), "message-"+c));
			f.add(ask(ref, mv.get(c), TIMEOUT));
			++c;

			rd.add(new Date(2015, Calendar.FEBRUARY, 19, 17, m, 10));
			mv.add(new MessageWrapper(SOURCE1_BEAN_NAME, rd.get(c), "message-"+c));
			f.add(ask(ref, mv.get(c), TIMEOUT));
			++c;

			rd.add(new Date(2015, Calendar.FEBRUARY, 19, 17, m, 20));
			mv.add(new MessageWrapper(SOURCE2_BEAN_NAME, rd.get(c), "message-"+c));
			f.add(ask(ref, mv.get(c), TIMEOUT));
			++c;
			++m;

			try {
				Thread.sleep(WAIT + 100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

			for (int i = 0; i < f.size(); ++i) {
				assertTrue("future " + i, f.get(i).isCompleted());
			}

			int r = 3;
			assertTrue("response+"+r, Await.result(f.get(r), Duration.Zero()) == rd.get(r));
			r = 6;
			assertTrue("response+"+r, Await.result(f.get(r), Duration.Zero()) == rd.get(r));

			assertTrue("execution count", actor.getExecCount() == 2);
			int inputsSize = ((TimedListening) actor.getListening()).inputs.size();
			assertTrue("input size: " + inputsSize, inputsSize == 0);
		} finally {
			ref.stop();
		}
	}

	/**
	 * Controllo della generazione degli {@link HeartBeat} giusti.
	 * Estensione di {@link #test_0()} con quanto fatto in {@link Test_DeltaDelayActor#test_HeartBeat()}.
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void test_HeartBeat() throws Exception {
		log.info("---- test_HeartBeat -------------------------------");

		// questo metodo di creazione delle Props e' ripreso da ActorSystemStarter
		final SpringExt springExtProvider = SpringExtensionProvider.springExtProvider.get(system);
		final Props props = springExtProvider.props(LISTENER_BEAN_NAME);
		final TestActorRef<TestTimedListenerActor> ref = TestActorRef.create(system, props, "test_3");
		final TestTimedListenerActor actor = ref.underlyingActor();

		final TestActorRef<HeartBeatTestingActor> proberef = TestActorRef.create(system,
				springExtProvider.props(PROBE_ACTOR_BEAN_NAME), "probe_0");
		final HeartBeatTestingActor probe = proberef.underlyingActor();

		try {
			final Date d1 = new Date(2015, Calendar.FEBRUARY, 27, 12, 00, 00);
			final MessageWrapper mw1 = new MessageWrapper(SOURCE1_BEAN_NAME, d1, "message1");
			final Future<Object> future1 = ask(ref, mw1, TIMEOUT);

			final Date d2 = new Date(2015, Calendar.FEBRUARY, 27, 12, 00, 01);
			final MessageWrapper mw2 = new MessageWrapper(SOURCE2_BEAN_NAME, d2, "message2");
			final Future<Object> future2 = ask(ref, mw2, TIMEOUT);

			final Date d3 = new Date(2015, Calendar.FEBRUARY, 27, 12, 00, 02);
			final MessageWrapper mw3 = new MessageWrapper(SOURCE3_BEAN_NAME, d3, "message3");
			final Future<Object> future3 = ask(ref, mw3, TIMEOUT);

			try {
				Thread.sleep(WAIT + 100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

			assertTrue("future1", future1.isCompleted());
			assertTrue("future2", future2.isCompleted());
			assertTrue("future3", future3.isCompleted());

			assertTrue("response3", Await.result(future3, Duration.Zero()) == d3);

			assertTrue("execution count", actor.getExecCount() == 1);

			Assert.assertTrue("number of beats: " + probe.getTotal(), probe.getTotal() == actor.getExecCount());
		} finally {
			proberef.stop();
			ref.stop();
		}
	}
}
