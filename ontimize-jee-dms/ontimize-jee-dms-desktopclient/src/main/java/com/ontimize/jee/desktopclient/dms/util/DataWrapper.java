package com.ontimize.jee.desktopclient.dms.util;

/**
 * The Class DataWrapper.
 *
 * @param <T>
 *            the generic type
 */
public class DataWrapper<T> {

	/** The data. */
	private final T			data;

	/** The source. */
	private final Object	source;

	/**
	 * Instantiates a new data wrapper.
	 *
	 * @param data
	 *            the data
	 * @param source
	 *            the source
	 */
	public DataWrapper(T data, Object source) {
		super();
		this.source = source;
		this.data = data;
	}

	/**
	 * Gets the data.
	 *
	 * @return the data
	 */
	public T getData() {
		return this.data;
	}

	/**
	 * Gets the source.
	 *
	 * @return the source
	 */
	public Object getSource() {
		return this.source;
	}
}