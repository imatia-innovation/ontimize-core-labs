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

package com.caucho.hessian.io.deserializer;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caucho.hessian.io.AbstractHessianInput;

/**
 * Deserializing a Java array
 */
public class ArrayDeserializer extends AbstractListDeserializer {

    private static final Logger logger = LoggerFactory.getLogger(ArrayDeserializer.class);

    private final Class<?> componentType;

    private Class<?> type;

    public ArrayDeserializer(Class<?> componentType) {
        this.componentType = componentType;

        if (this.componentType != null) {
            try {
                this.type = Array.newInstance(this.componentType, 0).getClass();
            } catch (Exception e) {
                logger.trace(null, e);
            }
        }

        if (this.type == null) {
            this.type = Object[].class;
        }
    }

    @Override
    public Class<?> getType() {
        return this.type;
    }

    /**
     * Reads the array.
     */
    @Override
    public Object readList(AbstractHessianInput in, int length) throws IOException {
        if (length >= 0) {
            Object[] data = this.createArray(length);

            in.addRef(data);

            if (this.componentType != null) {
                for (int i = 0; i < data.length; i++) {
                    data[i] = in.readObject(this.componentType);
                }
            } else {
                for (int i = 0; i < data.length; i++) {
                    data[i] = in.readObject();
                }
            }

            in.readListEnd();

            return data;
        } else {
            ArrayList<Object> list = new ArrayList<>();

            in.addRef(list);

            if (this.componentType != null) {
                while (!in.isEnd()) {
                    list.add(in.readObject(this.componentType));
                }
            } else {
                while (!in.isEnd()) {
                    list.add(in.readObject());
                }
            }

            in.readListEnd();

            Object[] data = this.createArray(list.size());
            for (int i = 0; i < data.length; i++) {
                data[i] = list.get(i);
            }

            return data;
        }
    }

    /**
     * Reads the array.
     */
    @Override
    public Object readLengthList(AbstractHessianInput in, int length) throws IOException {
        Object[] data = this.createArray(length);

        in.addRef(data);

        if (this.componentType != null) {
            for (int i = 0; i < data.length; i++) {
                data[i] = in.readObject(this.componentType);
            }
        } else {
            for (int i = 0; i < data.length; i++) {
                data[i] = in.readObject();
            }
        }

        return data;
    }

    protected Object[] createArray(int length) {
        if (this.componentType != null) {
            return (Object[]) Array.newInstance(this.componentType, length);
        } else {
            return new Object[length];
        }
    }

    @Override
    public String toString() {
        return "ArrayDeserializer[" + this.componentType + "]";
    }

}
