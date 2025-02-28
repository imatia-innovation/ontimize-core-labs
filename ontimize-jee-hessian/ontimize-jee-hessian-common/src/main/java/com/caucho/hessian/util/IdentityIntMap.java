/*
 * Copyright (c) 2001-2008 Caucho Technology, Inc. All rights reserved. The Apache Software License,
 * Version 1.1 Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met: 1. Redistributions of source code must
 * retain the above copyright notice, this list of conditions and the following disclaimer. 2.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the
 * distribution. 3. The end-user documentation included with the redistribution, if any, must
 * include the following acknowlegement: "This product includes software developed by the Caucho
 * Technology (http://www.caucho.com/)." Alternately, this acknowlegement may appear in the software
 * itself, if and wherever such third-party acknowlegements normally appear. 4. The names "Burlap",
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

package com.caucho.hessian.util;

/**
 * The IntMap provides a simple hashmap from keys to integers. The API is an abbreviation of the
 * HashMap collection API.
 *
 * <p>
 * The convenience of IntMap is avoiding all the silly wrapping of integers.
 */
public class IdentityIntMap {

    /**
     * Encoding of a null entry. Since NULL is equal to Integer.MIN_VALUE, it's impossible to
     * distinguish between the two. Integer.MIN_VALUE + 1;
     */
    public static final int NULL = 0xdeadbeef;

    private Object[] keys;

    private int[] values;

    private int size;

    private int prime;

    /**
     * Create a new IntMap. Default size is 16.
     */
    public IdentityIntMap(int capacity) {
        this.keys = new Object[capacity];
        this.values = new int[capacity];

        this.prime = IdentityIntMap.getBiggestPrime(this.keys.length);
        this.size = 0;
    }

    /**
     * Clear the hashmap.
     */
    public void clear() {
        final Object[] keys = this.keys;
        final int[] values = this.values;

        for (int i = keys.length - 1; i >= 0; i--) {
            keys[i] = null;
            values[i] = 0;
        }

        this.size = 0;
    }

    /**
     * Returns the current number of entries in the map.
     */
    public final int size() {
        return this.size;
    }

    /**
     * Puts a new value in the property table with the appropriate flags
     */
    public final int get(Object key) {
        int prime = this.prime;
        int hash = System.identityHashCode(key) % prime;
        final Object[] keys = this.keys;

        while (true) {
            Object mapKey = keys[hash];

            if (mapKey == null) {
                return IdentityIntMap.NULL;
            } else if (mapKey == key) {
                return this.values[hash];
            }

            hash = (hash + 1) % prime;
        }
    }

    /**
     * Puts a new value in the property table with the appropriate flags
     */
    public final int put(Object key, int value, boolean isReplace) {
        int prime = this.prime;
        int hash = Math.abs(System.identityHashCode(key) % prime);
        Object[] keys = this.keys;

        while (true) {
            Object testKey = keys[hash];

            if (testKey == null) {
                keys[hash] = key;
                this.values[hash] = value;

                this.size++;

                if (keys.length <= (4 * this.size)) {
                    this.resize(4 * keys.length);
                }

                return value;
            } else if (key != testKey) {
                hash = (hash + 1) % prime;

                continue;
            } else if (isReplace) {
                int old = this.values[hash];

                this.values[hash] = value;

                return old;
            } else {
                return this.values[hash];
            }
        }
    }

    /**
     * Removes a value in the property table.
     */
    public final void remove(Object key) {
        if (this.put(key, IdentityIntMap.NULL, true) != IdentityIntMap.NULL) {
            this.size--;
        }
    }

    /**
     * Expands the property table
     */
    private void resize(int newSize) {
        Object[] keys = this.keys;
        int values[] = this.values;

        this.keys = new Object[newSize];
        this.values = new int[newSize];
        this.size = 0;

        this.prime = IdentityIntMap.getBiggestPrime(this.keys.length);

        for (int i = keys.length - 1; i >= 0; i--) {
            Object key = keys[i];
            int value = values[i];

            if ((key != null) && (value != IdentityIntMap.NULL)) {
                this.put(key, value, true);
            }
        }
    }

    protected int hashCode(Object value) {
        return System.identityHashCode(value);
    }

    @Override
    public String toString() {
        StringBuilder sbuf = new StringBuilder();

        sbuf.append("IntMap[");
        boolean isFirst = true;

        for (int i = 0; i <= this.keys.length; i++) {
            if (this.keys[i] != null) {
                if (!isFirst) {
                    sbuf.append(", ");
                }

                isFirst = false;
                sbuf.append(this.keys[i]);
                sbuf.append(":");
                sbuf.append(this.values[i]);
            }
        }
        sbuf.append("]");

        return sbuf.toString();
    }

    private static final int[] PRIMES = { 1, /* 1<< 0 = 1 */
            2, /* 1<< 1 = 2 */
            3, /* 1<< 2 = 4 */
            7, /* 1<< 3 = 8 */
            13, /* 1<< 4 = 16 */
            31, /* 1<< 5 = 32 */
            61, /* 1<< 6 = 64 */
            127, /* 1<< 7 = 128 */
            251, /* 1<< 8 = 256 */
            509, /* 1<< 9 = 512 */
            1021, /* 1<<10 = 1024 */
            2039, /* 1<<11 = 2048 */
            4093, /* 1<<12 = 4096 */
            8191, /* 1<<13 = 8192 */
            16381, /* 1<<14 = 16384 */
            32749, /* 1<<15 = 32768 */
            65521, /* 1<<16 = 65536 */
            131071, /* 1<<17 = 131072 */
            262139, /* 1<<18 = 262144 */
            524287, /* 1<<19 = 524288 */
            1048573, /* 1<<20 = 1048576 */
            2097143, /* 1<<21 = 2097152 */
            4194301, /* 1<<22 = 4194304 */
            8388593, /* 1<<23 = 8388608 */
            16777213, /* 1<<24 = 16777216 */
            33554393, /* 1<<25 = 33554432 */
            67108859, /* 1<<26 = 67108864 */
            134217689, /* 1<<27 = 134217728 */
            268435399, /* 1<<28 = 268435456 */
    };

    public static int getBiggestPrime(int value) {
        for (int i = IdentityIntMap.PRIMES.length - 1; i >= 0; i--) {
            if (IdentityIntMap.PRIMES[i] <= value) {
                return IdentityIntMap.PRIMES[i];
            }
        }

        return 2;
    }

}
