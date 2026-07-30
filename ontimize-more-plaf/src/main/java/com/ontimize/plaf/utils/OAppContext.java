/*
 * Copyright (c) 2009 Kathryn Huxtable and Kenneth Orr.
 *
 * This file is part of the Ontimize Pluggable Look and Feel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 * $Id: OAppContext.java,v 1.1 2026/04/19 12:00:00 daniel.grana Exp $
 */
package com.ontimize.plaf.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Thread-local application context that provides a way to store thread-specific data.
 * This replaces the use of sun.awt.AppContext with a public API alternative.
 *
 * Each thread has its own context storage, making it safe for use in multi-threaded
 * environments like Swing's Event Dispatch Thread (EDT).
 *
 * @author Imatia Innovation
 */
public class OAppContext {

    /**
     * ThreadLocal storage for each thread's context map.
     */
    private static final ThreadLocal<Map<Object, Object>> CONTEXT_HOLDER = ThreadLocal.withInitial(HashMap::new);

    /**
     * Gets the current thread's application context map.
     *
     * @return the context map for the current thread
     */
    public static Map<Object, Object> getAppContext() {
        return CONTEXT_HOLDER.get();
    }

    /**
     * Gets a value from the current thread's context by key.
     *
     * @param key the key to retrieve
     * @return the value associated with the key, or null if not found
     */
    public static Object get(Object key) {
        return getAppContext().get(key);
    }

    /**
     * Stores a value in the current thread's context.
     *
     * @param key the key to store
     * @param value the value to store
     */
    public static void put(Object key, Object value) {
        getAppContext().put(key, value);
    }

    /**
     * Removes a value from the current thread's context.
     *
     * @param key the key to remove
     */
    public static void remove(Object key) {
        getAppContext().remove(key);
    }

    /**
     * Clears all values from the current thread's context.
     */
    public static void clear() {
        getAppContext().clear();
    }

    /**
     * Clears the ThreadLocal to avoid memory leaks.
     * Call this when the thread is no longer needed.
     */
    public static void clearContext() {
        CONTEXT_HOLDER.remove();
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private OAppContext() {
    }

}
