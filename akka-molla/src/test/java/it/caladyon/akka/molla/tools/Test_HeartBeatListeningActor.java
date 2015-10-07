/**
 *
 */
package it.caladyon.akka.molla.tools;

import it.caladyon.akka.spring.AbstractJUnit4AkkaSpringTests;
import it.caladyon.akka.spring.SpringExtensionProvider;
import it.caladyon.akka.spring.SpringExtensionProvider.SpringExt;
import it.caladyon.akka.spring.config.AkkaConf;
import it.caladyon.akka.molla.msg.HeartBeat;
import it.caladyon.akka.molla.msg.StatRequest;

import java.util.Map;
import java.util.concurrent.TimeUnit;

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
import akka.actor.ActorPath;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.event.EventStream;
import akka.pattern.Patterns;
import akka.testkit.TestActorRef;

/**
 * Si veda il manuale di Akka, capitolo 3.9.1 / Testing the Actor's Behavior.
 * @author 16800028
 *
 */
@ContextConfiguration(classes = Test_HeartBeatListeningActor.class)
@Configuration
@Import(AkkaConf.class)
public class Test_HeartBeatListeningActor extends AbstractJUnit4AkkaSpringTests {

	private static final long TIMEOUT = 500L;

	/**
	 * Metodi utili solo per test.
	 *
	 * @author 16800028
	 *
	 */
	public static class TestedActor extends HeartBeatListeningActor {

		public final Map<String, HeartBeat> getBeats() {
			return beats;
		}

	}

	// Spring Configuration - inizio

	public static final String SOURCE1_BEAN_NAME = "source1-actor";
	public static final String SOURCE2_BEAN_NAME = "source2-actor";
	public static final String SOURCE3_BEAN_NAME = "source3-actor";

	public static final String TESTED_ACTOR_BEAN_NAME = "heartbeat-listen-actor";

	@Bean(name = TESTED_ACTOR_BEAN_NAME)
	@Scope("prototype")
	public TestedActor getTestedActor() {
		TestedActor rv = new TestedActor();
		return rv;
	}

	// Spring Configuration - fine

	/**
	 * Test di comunicazione: pattern ask con ActorRef.
	 *
	 * @throws Exception
	 */
	@Test
	public void test_0() throws Exception {
		log.info("---- test 0 -------------------------------");

		// questo metodo di creazione delle Props e' ripreso da ActorSystemStarter
		final SpringExt springExtProvider = SpringExtensionProvider.springExtProvider.get(system);
		final Props props = springExtProvider.props(TESTED_ACTOR_BEAN_NAME);
		final TestActorRef<TestedActor> ref = TestActorRef.create(system, props, "test_0");
//		final TestedActor actor = ref.underlyingActor();

		// esecuzione simile a quella del MonitorController
		Future<Object> f = Patterns.ask(ref, new StatRequest(), TIMEOUT);
		Object result = Await.result(f, Duration.Zero());

		Assert.assertTrue("type", result instanceof Map<?, ?>);
		Assert.assertTrue("size", ((Map<?, ?>) result).size() == 0);

		ref.stop();
	}

	/**
	 * Test di comunicazione: pattern ask con ActorSelection.
	 *
	 * @throws Exception
	 */
	@Test
	public void test_1() throws Exception {
		log.info("---- test 1 -------------------------------");

		// questo metodo di creazione delle Props e' ripreso da ActorSystemStarter
		final SpringExt springExtProvider = SpringExtensionProvider.springExtProvider.get(system);
		final Props props = springExtProvider.props(TESTED_ACTOR_BEAN_NAME);
		final TestActorRef<TestedActor> ref = TestActorRef.create(system, props, "test_0");
		final ActorPath path = ref.path();
//		final TestedActor actor = ref.underlyingActor();

		// esecuzione simile a quella del MonitorController
		ActorSelection asel = system.actorSelection(path);
		Future<Object> f = Patterns.ask(asel, new StatRequest(), TIMEOUT);
		Object result = Await.result(f, Duration.Zero());

		Assert.assertTrue("type", result instanceof Map<?, ?>);
		Assert.assertTrue("size", ((Map<?, ?>) result).size() == 0);

		ref.stop();
	}

	/**
	 * Test di comunicazione: pattern ask con ActorSelection; alcuni {@link HeartBeat}.
	 *
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void test_2() throws Exception {
		log.info("---- test 2 -------------------------------");

		// questo metodo di creazione delle Props e' ripreso da ActorSystemStarter
		final SpringExt springExtProvider = SpringExtensionProvider.springExtProvider.get(system);
		final Props props = springExtProvider.props(TESTED_ACTOR_BEAN_NAME);
		final TestActorRef<TestedActor> ref = TestActorRef.create(system, props, "test_0");
		final ActorPath path = ref.path();
//		final TestedActor actor = ref.underlyingActor();

		EventStream es = system.eventStream();

		// heartbeats
		es.publish(new HeartBeat(SOURCE1_BEAN_NAME, System.currentTimeMillis(), 1000));
		es.publish(new HeartBeat(SOURCE2_BEAN_NAME, System.currentTimeMillis(), 2000));
		es.publish(new HeartBeat(SOURCE3_BEAN_NAME, System.currentTimeMillis(), 3000));

		es.publish(new HeartBeat(SOURCE2_BEAN_NAME, System.currentTimeMillis(), 2001));
		es.publish(new HeartBeat(SOURCE3_BEAN_NAME, System.currentTimeMillis(), 3001));
		es.publish(new HeartBeat(SOURCE1_BEAN_NAME, System.currentTimeMillis(), 1001));

		// esecuzione simile a quella del MonitorController
		ActorSelection asel = system.actorSelection(path);
		Future<Object> f = Patterns.ask(asel, new StatRequest(), TIMEOUT);
		Object result = Await.result(f, Duration.create(TIMEOUT, TimeUnit.MILLISECONDS));

		Assert.assertTrue("type", result instanceof Map<?, ?>);
		Assert.assertTrue("size", ((Map<?, ?>) result).size() == 3);

		for (HeartBeat hb : ((Map<String, HeartBeat>) result).values()) {
			log.debug(hb);
		}

		ref.stop();
	}

}
