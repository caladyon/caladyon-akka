/**
 *
 */
package it.caladyon.akka.spring.config;

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
