/**
 *
 */
package it.caladyon.akka.molla.msg;

import java.io.Serializable;

/**
 * Indica che un attore ha prodotto dei dati e li ha messi in cache, con la chiave indicata.
 *
 *
 * @author Luciano Boschi 16800028
 * @since 15/gen/2015
 *
 */
public class NewCacheData implements Serializable {

	private static final long serialVersionUID = 1L;

	/** Chiave della cache. */
	private String key;

	public NewCacheData(String key) {
		super();
		this.key = key;
	}

	/**
	 * @return the key
	 */
	public final String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public final void setKey(String key) {
		this.key = key;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "NewCacheData[" + key + "]";
	}

}
