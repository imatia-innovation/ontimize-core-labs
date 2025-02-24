package com.ontimize.util;

import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.tools.ReflectionTools;

public final class ObjectTools {
	private ObjectTools() {
		super();
	}

	public static <T> T clone(final T input) {
		if (input instanceof Cloneable) {
			try {
				return (T) ReflectionTools.invoke(input, "clone");
			} catch (final Exception err) {
				throw new RuntimeException("Error al clonar el objeto", err);
			}
		} else if (input instanceof EntityResult) {
			return (T) ((EntityResult) input).clone();
		}
		throw new IllegalArgumentException("El objeto no implementa Cloneable");
	}

}
