/**
 *
 */
package it.caladyon.akka.spring.bean;

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
