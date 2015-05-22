/**
 *
 */
package it.caladyon.akka.spring;

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
