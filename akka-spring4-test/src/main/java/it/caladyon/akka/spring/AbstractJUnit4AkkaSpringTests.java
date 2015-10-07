/**
 *
 */
package it.caladyon.akka.spring;

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
