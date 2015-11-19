package it.caladyon.akka.molla.topology;

import it.caladyon.akka.molla.msg.HeartBeat;
import it.caladyon.akka.molla.tools.HeartBeatListeningActor;

import java.util.Map;

import org.springframework.beans.factory.BeanNameAware;

import scala.collection.mutable.StringBuilder;
import akka.actor.UntypedActor;
import akka.event.EventStream;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * Questa classe definisce le funzionalita' comuni a tutti gli attori del sistema:
 * <ul>
 * <li>gestione del nome dell'attore (bean name);
 * <li>gestione dell'event stream per la pubblicazione degli {@link HeartBeat},
 * con gli opportuni metodi per la generazione dei "battiti";
 * <li>logging.
 * </ul>
 *
 * @author Luciano Boschi
 * @since 1.0.4
 *
 */
public abstract class SpringActor extends UntypedActor implements BeanNameAware {

	/** Logger asincrono di AKKA. */
	protected final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	/** Bean name. */
	protected String name;

	/** @see #isBeatable(Object) */
	private boolean beatable = true;

	/**
	 * @see #preStart()
	 * @see #postStop()
	 */
	protected EventStream eventStream;

	@Override
	public void setBeanName(String name) {
		this.name = name;
	}

	/**
	 * Compone il nome proprieta' completo.
	 *
	 * @param beanPropName
	 * @return		Concatenazione di {@link #name} e del <code>beanPropName</code> fornito.
	 *
	 * @since 02/feb/2015
	 */
	protected String getMyPropName(String beanPropName) {
		return new StringBuilder().append(name).append('.').append(beanPropName).toString();
	}

	/**
	 * Ottiene l'"event stream" dall'attuale actor system.
	 *
	 * @see akka.actor.UntypedActor#preStart()
	 */
	@Override
	public void preStart() throws Exception {
		super.preStart();
		eventStream = getContext().system().eventStream();
	}

	/**
	 * Chiama {@link #onReceive1(Object)} e poi {@link #publishHeartBeat(long, long, Object)}.
	 *
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	@Override
	public final void onReceive(Object message) throws Exception {
		long t0 = System.currentTimeMillis();
		onReceive1(message);
		long t1 = System.currentTimeMillis();
		publishHeartBeat(t0, t1, message);
	}

	protected abstract void onReceive1(Object message) throws Exception;

	/**
	 * Questo metodo pubblica un messaggio di tipo {@link HeartBeat} al completamento di {@link #onReceive(Object)},
	 * quando il messaggio {@link #isBeatable(Object) e' battibile}).
	 *
	 * @see HeartBeatListeningActor
	 *
	 * @param t0		Timestamp iniziale dell'elaborazione.
	 * @param t1		Timestamp finale   dell'elaborazione.
	 * @param message	Messaggio da verificare se "is beatable".
	 */
	protected void publishHeartBeat(long t0, long t1, Object message) {
		if (isBeatable(message)) {
			eventStream.publish(composeHeartBeat(t0, t1, message));
		}
	}

	/**
	 * Per disabilitare completamente gli HeatBeat per un bean,
	 * impostare beatable a false (altrimenti il default e' true).
	 *
	 * @param beatable the beatable to set
	 */
	public final void setBeatable(boolean beatable) {
		this.beatable = beatable;
	}

	/**
	 * <p>
	 * Condizione richiesta da {@link #publishHeartBeat(long, long, Object)}.
	 * Per disabilitare completamente gli HeatBeat per un bean,
	 * impostare beatable a false.
	 * <p>
	 * I metodi derivati dovrebbero prima chiamare <code>super.isBeatable(message)</code>
	 * e poi aggiungere le proprie condizioni.
	 *
	 * @param message
	 * @return
	 */
	protected boolean isBeatable(Object message) {
		return beatable;
	}

	/**
	 * Utility per {@link #publishHeartBeat(long, long, Object)}: costruisce il battito da pubblicare.
	 *
	 * @param t0
	 * @param t1
	 * @param message
	 * @return
	 */
	protected HeartBeat composeHeartBeat(long t0, long t1, Object message) {
		return new HeartBeat(name, t0, t1 - t0, composeVitals(message));
	}

	/**
	 * Utility per {@link #composeHeartBeat(long, long, Object)}:
	 * crea una mappa chiave-valore dei "segni vitali" dell'istanza in esecuzione.
	 *
	 * @param message
	 *
	 * @return	La nuova mappa oppure null.
	 */
	protected Map<String, Object> composeVitals(Object message) {
		return null;
	}

}