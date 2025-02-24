/*
 * Copyright (c) 2001-2004 Caucho Technology, Inc. All rights reserved. The Apache Software License,
 * Version 1.1 Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met: 1. Redistributions of source code must
 * retain the above copyright notice, this list of conditions and the following disclaimer. 2.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the
 * distribution. 3. The end-user documentation included with the redistribution, if any, must
 * include the following acknowlegement: "This product includes software developed by the Caucho
 * Technology (http://www.caucho.com/)." Alternately, this acknowlegement may appear in the software
 * itself, if and wherever such third-party acknowlegements normally appear. 4. The names "Hessian",
 * "Resin", and "Caucho" must not be used to endorse or promote products derived from this software
 * without prior written permission. For written permission, please contact info@caucho.com. 5.
 * Products derived from this software may not be called "Resin" nor may "Resin" appear in their
 * names without prior written permission of Caucho Technology. THIS SOFTWARE IS PROVIDED ``AS IS''
 * AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL CAUCHO
 * TECHNOLOGY OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 * @author Scott Ferguson
 */

package com.caucho.services.server;

import java.lang.reflect.Method;
import java.util.HashMap;

import com.caucho.hessian.util.MangleTools;

/**
 * Proxy class for Hessian services.
 */
abstract public class AbstractSkeleton {

	private final Class<?> apiClass;

	private Class<?> homeClass;

	private Class<?> objectClass;

	private final HashMap<String, Method> methodMap = new HashMap<>();

	/**
	 * Create a new hessian skeleton.
	 * @param apiClass the API interface
	 */
	protected AbstractSkeleton(final Class<?> apiClass) {
		this.apiClass = apiClass;

		final Method[] methodList = apiClass.getMethods();

		for (int i = 0; i < methodList.length; i++) {
			final Method method = methodList[i];

			if (this.methodMap.get(method.getName()) == null) {
				this.methodMap.put(method.getName(), methodList[i]);
			}

			final Class<?>[] param = method.getParameterTypes();
			final String mangledName = method.getName() + "__" + param.length;
			this.methodMap.put(mangledName, methodList[i]);

			this.methodMap.put(MangleTools.mangleName(method, false), methodList[i]);
		}
	}

	/**
	 * Returns the API class of the current object.
	 */
	public String getAPIClassName() {
		return this.apiClass.getName();
	}

	/**
	 * Returns the API class of the factory/home.
	 */
	public String getHomeClassName() {
		if (this.homeClass != null) {
			return this.homeClass.getName();
		} else {
			return this.getAPIClassName();
		}
	}

	/**
	 * Sets the home API class.
	 */
	public void setHomeClass(final Class<?> homeAPI) {
		this.homeClass = homeAPI;
	}

	/**
	 * Returns the API class of the object URLs
	 */
	public String getObjectClassName() {
		if (this.objectClass != null) {
			return this.objectClass.getName();
		} else {
			return this.getAPIClassName();
		}
	}

	/**
	 * Sets the object API class.
	 */
	public void setObjectClass(final Class<?> objectAPI) {
		this.objectClass = objectAPI;
	}

	/**
	 * Returns the method by the mangled name.
	 * @param mangledName the name passed by the protocol
	 */
	protected Method getMethod(final String mangledName) {
		return this.methodMap.get(mangledName);
	}


	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[" + this.apiClass.getName() + "]";
	}

}
