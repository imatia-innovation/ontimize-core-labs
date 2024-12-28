package com.ontimize.util;

import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.stream.IntStream;

public final class CollectionTools {
	private CollectionTools() {
		super();
	}

	public static int indexOf(final List<?> list, final Object elem, final int startIndex) {
		return IntStream.range(startIndex, list.size())
				.filter(i -> list.get(i).equals(elem))
				.findFirst()
				.orElse(-1);
	}

	public static <T> T firstElement(final Collection<T> list) {
		if (list instanceof List) {
			return ((List<T>) list).get(0);
		} else {
			return list.iterator().next();
		}
	}

	public static <T> T lastElement(final Collection<T> list) {
		if (list instanceof List) {
			return ((List<T>) list).get(list.size() - 1);
		} else {
			return list.iterator().next();
		}
	}

	public static void setSize(final List<?> list, final int newSize) {
		if (list instanceof Vector) {
			((Vector) list).setSize(newSize);
			return;
		}

		final int currentSize = list.size();
		if (newSize > currentSize) {
			for (int i = currentSize; i < newSize; i++) {
				list.add(null);
			}
		} else if (newSize < currentSize) {
			for (int i = currentSize - 1; i >= newSize; i--) {
				list.remove(i);
			}
		}
	}

}
