package com.ontimize.util.rtf.element;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RTFElement {

	private static final Logger logger = LoggerFactory.getLogger(RTFElement.class);

	public static final String TYPE = "type";

	public static final String NAME = "name";

	public static final String VAL = "content";

	public static final String LENGTH = "length";

	private final Map attrs = new Hashtable();

	private final List childs = new Vector(100);

	private RTFElement parent = null;

	public RTFElement() {
	}

	public RTFElement(final RTFElement parent) {
		parent.addChild(this);
	}

	public Enumeration getAttributeNames() {
		return Collections.enumeration(this.attrs.keySet());
	}

	public Object getAttribute(final String key) {
		return this.attrs.get(key);
	}

	public String getType() {
		return (String) this.getAttribute("type");
	}

	public String getName() {
		if (this.getAttribute("name") == null) {
			return "";
		}
		return (String) this.getAttribute("name");
	}

	public int getLength() {
		if (this.getAttribute("length") != null) {
			return ((Integer) this.getAttribute("length")).intValue();
		}
		return -1;
	}

	public Object getContent() {
		return this.getAttribute("content");
	}

	public void setAttribute(final String key, final Object obj) {
		this.attrs.put(key, obj);
	}

	public void setAttribute(final String key, final int obj) {
		this.setAttribute(key, new Integer(obj));
	}

	public void removeAttribute(final String key) {
		this.attrs.remove(key);
	}

	public int getAttributeCount() {
		return this.attrs.size();
	}

	public Enumeration getChildList() {
		return Collections.enumeration(this.childs);
	}

	public int getChildCount() {
		return this.childs.size();
	}

	public RTFElement getChild(final int idx) {
		return (RTFElement) this.childs.get(idx);
	}

	public void addChild(final RTFElement node) {
		this.childs.add(node);
		node.parent = this;
	}

	public void removeChild(final int idx) {
		this.childs.remove(idx);
	}

	public int indexOf(final RTFElement node) {
		return this.childs.indexOf(node);
	}

	@Override
	public String toString() {
		String s = null;
		final Enumeration keys = this.getAttributeNames();
		while (keys.hasMoreElements()) {
			final String key = (String) keys.nextElement();
			final Object val = this.getAttribute(key);
			if (s == null) {
				s = " " + key + "='" + val + "'; ";
			} else {
				s = s + key + "='" + val + "'";
			}
		}
		return "[" + s + "]";
	}

	public void list() {
		this.list(0);
	}

	public void list(final int tab) {
		for (int j = 0; j < tab; j++) {
			RTFElement.logger.debug("\t");
		}
		RTFElement.logger.debug(this.toString());
		for (int i = 0; i < this.getChildCount(); i++) {
			this.getChild(i).list(tab + 1);
		}
	}

	public RTFElement getParent() {
		return this.parent;
	}

	RTFElement cloneNode() {
		final RTFElement clone = new RTFElement(this.parent);
		final Enumeration keys = this.getAttributeNames();
		while (keys.hasMoreElements()) {
			final String key = (String) keys.nextElement();
			final Object val = this.getAttribute(key);
			clone.setAttribute(key, val);
		}
		return clone;
	}

}
