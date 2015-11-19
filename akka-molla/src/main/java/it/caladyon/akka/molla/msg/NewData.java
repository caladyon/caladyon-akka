/**
 *
 */
package it.caladyon.akka.molla.msg;

import java.io.Serializable;

/**
 * Contiene i dati prodotti da un attore, che diventano input degli attori in ascolto.
 *
 * @author Luciano Boschi
 * @since 15/gen/2015
 *
 */
public class NewData implements Serializable {

	private static final long serialVersionUID = 1L;

	/** Output dell'attore. */
	private Object value;

	public NewData(Object value) {
		super();
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public final Object getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public final void setValue(Object value) {
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "NewData [" + (value == null ? null : value.getClass()) + "]";
	}

}
