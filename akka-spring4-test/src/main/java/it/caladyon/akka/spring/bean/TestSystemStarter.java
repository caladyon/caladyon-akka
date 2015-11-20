/**
 *
 */
package it.caladyon.akka.spring.bean;

import it.caladyon.akka.spring.SpringExtensionProvider;
import it.caladyon.akka.spring.SpringExtensionProvider.SpringExt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PreDestroy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.testkit.TestActorRef;

/**
 * XXX: think about the use of SpringActor (currently defined in akka-molla), instead of UntypedActor
 *
 * @author Luciano Boschi 87001893
 * @since 15/apr/2015
 *
 */
public class TestSystemStarter {

	/** Logger. */
	protected final Log log = LogFactory.getLog(getClass());

	@Autowired(required = true)
	protected ActorSystem system;

	/** Associazione bean name - riferimento a attore di test. */
	private final Map<String, TestActorRef<UntypedActor>> testActorRefs = new HashMap<String, TestActorRef<UntypedActor>>();

	/**
	 * @return the system
	 */
	public final ActorSystem getSystem() {
		return system;
	}

	public void setActors(List<String> actors) {
		// topology
		final SpringExt springExtProvider = SpringExtensionProvider.springExtProvider.get(system);

		for (String actorBeanName : actors) {
			Props props = springExtProvider.props(actorBeanName);
			TestActorRef<UntypedActor> ref = TestActorRef.create(system, props, actorBeanName);
			testActorRefs.put(actorBeanName, ref);

//			ActorRef actor = system.actorOf(springExtProvider.props(actorBeanName), actorBeanName);
			log.info("Starting +++ " + actorBeanName + " + " + ref);
		}
	}

	/**
	 *
	 * @param beanname
	 * @return
	 *
	 * @since 16/apr/2015
	 */
	public final TestActorRef<UntypedActor> getTestActorRefByBeanName(String beanname) {
		return testActorRefs.get(beanname);
	}

	@PreDestroy
	public void terminate() {
		log.info("Shutting down the AKKA actor system");
		system.shutdown();
		system.awaitTermination();
		log.info("AKKA actor system shut down");
	}
}
