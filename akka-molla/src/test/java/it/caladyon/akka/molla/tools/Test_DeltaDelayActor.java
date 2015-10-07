/**
 *
 */
package it.caladyon.akka.molla.tools;

import it.caladyon.akka.spring.AbstractJUnit4AkkaSpringTests;
import it.caladyon.akka.spring.SpringExtensionProvider;
import it.caladyon.akka.spring.SpringExtensionProvider.SpringExt;
import it.caladyon.akka.spring.config.AkkaConf;
import it.caladyon.akka.molla.msg.HeartBeat;
import it.caladyon.akka.molla.tools.AbstractTicketyActor.TicketyToc;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.test.context.ContextConfiguration;

import akka.testkit.TestActorRef;

/**
 * @author 16800028
 *
 */
@ContextConfiguration(classes = Test_DeltaDelayActor.class)
@Configuration
@Import(AkkaConf.class)
public class Test_DeltaDelayActor extends AbstractJUnit4AkkaSpringTests {

	public static final int SCHEDULING_SECONDS = 5;

	// Spring Configuration - inizio

	public static final String TESTED_ACTOR_BEAN_NAME = "dd-actor";

	public static final String PROBE_ACTOR_BEAN_NAME = "probe-actor";

	@Bean(name = TESTED_ACTOR_BEAN_NAME)
	@Scope("prototype")
	public DeltaDelayActor getTestedActor() {
		DeltaDelayActor rv = new DeltaDelayActor();
		rv.setListenedActors("none");
		rv.setSeconds(SCHEDULING_SECONDS);
		return rv;
	}

	@Bean(name = PROBE_ACTOR_BEAN_NAME)
	@Scope("prototype")
	public HeartBeatTestingActor getHBProbeActor() {
		HeartBeatTestingActor rv = new HeartBeatTestingActor();
		return rv;
	}

	/**
	 * Questo bean serve a BaseTellerActor.
	 * Senza questo (o altri bean necessari, underlyingActor va in timeout!
	 *
	 * @return		Cache "finta": e' solo un normale hashmap.
	 */
	@Bean(name = "applicativeCache")
	@Lazy
	public Map<String, Object> createCache() {
		return new HashMap<String, Object>();
	}

	// Spring Configuration - fine

	/**
	 * Si verifica che i messaggi {@link TicketyToc} non generino {@link HeartBeat}.
	 */
	@Test
	public void test_HeartBeat() {
		// questo metodo di creazione delle Props e' ripreso da ActorSystemStarter
		final SpringExt springExtProvider = SpringExtensionProvider.springExtProvider.get(system);
		final TestActorRef<DeltaDelayActor> actorref = TestActorRef.create(system,
				springExtProvider.props(TESTED_ACTOR_BEAN_NAME), "test_0");
		//final DeltaDelayActor actor = ref.underlyingActor();
		final TestActorRef<HeartBeatTestingActor> proberef = TestActorRef.create(system,
				springExtProvider.props(PROBE_ACTOR_BEAN_NAME), "probe_0");
		final HeartBeatTestingActor probe = proberef.underlyingActor();

		try {
			Thread.sleep(SCHEDULING_SECONDS * 5 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Assert.assertTrue("number of beats: " + probe.getTotal(), probe.getTotal() == 0);

		proberef.stop();
		actorref.stop();
	}
}
