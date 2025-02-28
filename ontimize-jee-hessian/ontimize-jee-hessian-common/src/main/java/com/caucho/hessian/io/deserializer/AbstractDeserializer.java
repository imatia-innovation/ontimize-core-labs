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

package com.caucho.hessian.io.deserializer;

import java.io.IOException;

import com.caucho.hessian.io.AbstractHessianInput;
import com.caucho.hessian.io.HessianProtocolException;

/**
 * Deserializing an object.
 */
public class AbstractDeserializer implements Deserializer {

    public static final NullDeserializer NULL = new NullDeserializer();

    @Override
    public Class<?> getType() {
        return Object.class;
    }

    @Override
    public boolean isReadResolve() {
        return false;
    }

    @Override
    public Object readObject(AbstractHessianInput in) throws IOException {
        Object obj = in.readObject();

        String className = this.getClass().getName();

        if (obj != null) {
            throw this.error(className + ": unexpected object " + obj.getClass().getName() + " (" + obj + ")");
        }
        throw this.error(className + ": unexpected null value");
    }

    @Override
    public Object readList(AbstractHessianInput in, int length) throws IOException {
        throw new UnsupportedOperationException(String.valueOf(this));
    }

    @Override
    public Object readLengthList(AbstractHessianInput in, int length) throws IOException {
        throw new UnsupportedOperationException(String.valueOf(this));
    }

    @Override
    public Object readMap(AbstractHessianInput in) throws IOException {
        Object obj = in.readObject();

        String className = this.getClass().getName();

        if (obj != null) {
            throw this.error(className + ": unexpected object " + obj.getClass().getName() + " (" + obj + ")");
        }
        throw this.error(className + ": unexpected null value");
    }

    /**
     * Creates the field array for a class. The default implementation returns a String[] array.
     * @param len number of items in the array
     * @return the new empty array
     */
    @Override
    public Object[] createFields(int len) {
        return new String[len];
    }

    /**
     * Creates a field value class. The default implementation returns the String.
     * @param len number of items in the array
     * @return the new empty array
     */
    @Override
    public Object createField(String name) {
        return name;
    }

    @Override
    public Object readObject(AbstractHessianInput in, String[] fieldNames) throws IOException {
        return this.readObject(in, (Object[]) fieldNames);
    }

    /**
     * Reads an object instance from the input stream
     */
    @Override
    public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
        throw new UnsupportedOperationException(this.toString());
    }

    protected HessianProtocolException error(String msg) {
        return new HessianProtocolException(msg);
    }

    protected String codeName(int ch) {
        if (ch < 0) {
            return "end of file";
        }
        return "0x" + Integer.toHexString(ch & 0xff);
    }

    /**
     * The NullDeserializer exists as a marker for the factory classes so they save a null result.
     */
    static final class NullDeserializer extends AbstractDeserializer {

    }

}
