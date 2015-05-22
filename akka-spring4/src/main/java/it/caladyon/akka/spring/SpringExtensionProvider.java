/**
 *
 */
package it.caladyon.akka.spring;

import org.springframework.context.ApplicationContext;

import akka.actor.AbstractExtensionId;
import akka.actor.ExtendedActorSystem;
import akka.actor.Extension;
import akka.actor.Props;

/**
 * @author Luciano Boschi 16800028
 * @since 0.1.0
 *
 */
public class SpringExtensionProvider extends AbstractExtensionId<SpringExtensionProvider.SpringExt>{

	/**
	 * The identifier used to access the SpringExtension.
	 */
	public static SpringExtensionProvider springExtProvider = new SpringExtensionProvider();

	/**
	 * Is used by Akka to instantiate the Extension identified by this
	 * ExtensionId, internal use only.
	 */
	@Override
	public SpringExt createExtension(ExtendedActorSystem system) {
		return new SpringExt();
	}

	/**
	 * The Extension implementation.
	 */
	public static class SpringExt implements Extension {

		private volatile ApplicationContext applicationContext;

		/**
		 * Used to initialize the Spring application context for the extension.
		 * @param applicationContext
		 */
		public void initialize(ApplicationContext applicationContext) {
			this.applicationContext = applicationContext;
		}

		/**
		 * Create a Props for the specified actorBeanName using the
		 * SpringActorProducer class.
		 *
		 * @param actorBeanName  The name of the actor bean to create Props for
		 * @return a Props that will create the named actor bean using Spring
		 */
		public Props props(String actorBeanName) {
			return Props.create(SpringActorProducer.class, applicationContext, actorBeanName);
		}
	}
}
