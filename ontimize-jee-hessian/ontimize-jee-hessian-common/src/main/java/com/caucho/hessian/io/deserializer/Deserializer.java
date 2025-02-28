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

/**
 * Deserializing an object. Custom deserializers should extend from AbstractDeserializer to avoid
 * issues with signature changes.
 */
public interface Deserializer {

    public Class<?> getType();

    public boolean isReadResolve();

    public Object readObject(AbstractHessianInput in) throws IOException;

    public Object readList(AbstractHessianInput in, int length) throws IOException;

    public Object readLengthList(AbstractHessianInput in, int length) throws IOException;

    public Object readMap(AbstractHessianInput in) throws IOException;

    /**
     * Creates an empty array for the deserializers field entries.
     * @param len number of fields to be read
     * @return empty array of the proper field type.
     */
    public Object[] createFields(int len);

    /**
     * Returns the deserializer's field reader for the given name.
     * @param name the field name
     * @return the deserializer's internal field reader
     */
    public Object createField(String name);

    /**
     * Reads the object from the input stream, given the field definition.
     * @param in the input stream
     * @param fields the deserializer's own field marshal
     * @return the new object
     * @throws IOException
     */
    public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException;

    public Object readObject(AbstractHessianInput in, String[] fieldNames) throws IOException;

}
