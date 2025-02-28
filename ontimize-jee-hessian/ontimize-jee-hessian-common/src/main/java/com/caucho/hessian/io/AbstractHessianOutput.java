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

package com.caucho.hessian.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

// @formatter:off
/**
 * Abstract output stream for Hessian requests.
 *
 * <pre>
 * OutputStream os = ...; // from http connection
 * AbstractOutput out = new HessianSerializerOutput(os);
 * String value;
 *
 * out.startCall("hello");  // start hello call
 * out.writeString("arg1"); // write a string argument
 * out.completeCall();      // complete the call
 * </pre>
 */
// @formatter:on
abstract public class AbstractHessianOutput {

    // serializer factory
    private SerializerFactory defaultSerializerFactory;

    // serializer factory
    protected SerializerFactory serializerFactory;

    private byte[] byteBuffer;

    /**
     * Sets the serializer factory.
     */
    public void setSerializerFactory(SerializerFactory factory) {
        this.serializerFactory = factory;
    }

    /**
     * Gets the serializer factory.
     */
    public SerializerFactory getSerializerFactory() {
        // the default serializer factory cannot be modified by external
        // callers
        if (this.serializerFactory == this.defaultSerializerFactory) {
            this.serializerFactory = new SerializerFactory();
        }

        return this.serializerFactory;
    }

    /**
     * Gets the serializer factory.
     */
    protected final SerializerFactory findSerializerFactory() {
        SerializerFactory factory = this.serializerFactory;

        if (factory == null) {
            factory = SerializerFactory.createDefault();
            this.defaultSerializerFactory = factory;
            this.serializerFactory = factory;
        }

        return factory;
    }

    /**
     * Initialize the output with a new underlying stream.
     */
    public void init(OutputStream os) {
    }

    public boolean setUnshared(boolean isUnshared) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    /**
     * Writes a complete method call.
     */
    public void call(String method, Object[] args) throws IOException {
        int length = args != null ? args.length : 0;

        this.startCall(method, length);

        for (int i = 0; i < length; i++) {
            this.writeObject(args[i]);
        }

        this.completeCall();
    }

    /**
     * Starts the method call:
     *
     * <pre>
     * <code> C </code>
     * </pre>
     *
     * @param method the method name to call.
     */
    abstract public void startCall() throws IOException;

    /**
     * Starts the method call:
     *
     * <pre>
     * <code> C string int </code>
     * </pre>
     *
     * @param method the method name to call.
     */
    abstract public void startCall(String method, int length) throws IOException;

    /**
     * For Hessian 2.0, use the Header envelope instead
     * @deprecated
     */
    @Deprecated
    public void writeHeader(String name) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    /**
     * Writes the method tag.
     *
     * <pre>
     * <code> string </code>
     * </pre>
     *
     * @param method the method name to call.
     */
    abstract public void writeMethod(String method) throws IOException;

    /**
     * Completes the method call:
     *
     * <pre>
     * <code> </code>
     * </pre>
     */
    abstract public void completeCall() throws IOException;

    /**
     * Writes a boolean value to the stream. The boolean will be written with the following syntax:
     *
     * <pre>
     * <code> T F </code>
     * </pre>
     *
     * @param value the boolean value to write.
     */
    abstract public void writeBoolean(boolean value) throws IOException;

    /**
     * Writes an integer value to the stream. The integer will be written with the following syntax:
     *
     * <pre>
     * <code> I b32 b24 b16 b8 </code>
     * </pre>
     *
     * @param value the integer value to write.
     */
    abstract public void writeInt(int value) throws IOException;

    /**
     * Writes a long value to the stream. The long will be written with the following syntax:
     *
     * <pre>
     * <code> L b64 b56 b48 b40 b32 b24 b16 b8 </code>
     * </pre>
     *
     * @param value the long value to write.
     */
    abstract public void writeLong(long value) throws IOException;

    /**
     * Writes a double value to the stream. The double will be written with the following syntax:
     *
     * <pre>
     * <code> D b64 b56 b48 b40 b32 b24 b16 b8 </code>
     * </pre>
     *
     * @param value the double value to write.
     */
    abstract public void writeDouble(double value) throws IOException;

    /**
     * Writes a date to the stream.
     *
     * <pre>
     * <code> T b64 b56 b48 b40 b32 b24 b16 b8 </code>
     * </pre>
     *
     * @param time the date in milliseconds from the epoch in UTC
     */
    abstract public void writeUTCDate(long time) throws IOException;

    /**
     * Writes a null value to the stream. The null will be written with the following syntax
     *
     * <pre>
     * <code> N </code>
     * </pre>
     *
     * @param value the string value to write.
     */
    abstract public void writeNull() throws IOException;

    /**
     * Writes a string value to the stream using UTF-8 encoding. The string will be written with the
     * following syntax:
     *
     * <pre>
     * <code> S b16 b8 string-value </code>
     * </pre>
     *
     * If the value is null, it will be written as
     *
     * <pre>
     * <code> N </code>
     * </pre>
     *
     * @param value the string value to write.
     */
    abstract public void writeString(String value) throws IOException;

    /**
     * Writes a string value to the stream using UTF-8 encoding. The string will be written with the
     * following syntax:
     *
     * <pre>
     * <code> S b16 b8 string-value </code>
     * </pre>
     *
     * If the value is null, it will be written as
     *
     * <pre>
     * <code> N </code>
     * </pre>
     *
     * @param value the string value to write.
     */
    abstract public void writeString(char[] buffer, int offset, int length) throws IOException;

    /**
     * Writes a byte array to the stream. The array will be written with the following syntax:
     *
     * <pre>
     * <code> B b16 b18 bytes </code>
     * </pre>
     *
     * If the value is null, it will be written as
     *
     * <pre>
     * <code> N </code>
     * </pre>
     *
     * @param value the string value to write.
     */
    abstract public void writeBytes(byte[] buffer) throws IOException;

    /**
     * Writes a byte array to the stream. The array will be written with the following syntax:
     *
     * <pre>
     * <code> B b16 b18 bytes </code>
     * </pre>
     *
     * If the value is null, it will be written as
     *
     * <pre>
     * <code> N </code>
     * </pre>
     *
     * @param value the string value to write.
     */
    abstract public void writeBytes(byte[] buffer, int offset, int length) throws IOException;

    /**
     * Writes a byte buffer to the stream.
     */
    abstract public void writeByteBufferStart() throws IOException;

    /**
     * Writes a byte buffer to the stream.
     *
     * <pre>
     * <code> b b16 b18 bytes </code>
     * </pre>
     *
     * @param value the string value to write.
     */
    abstract public void writeByteBufferPart(byte[] buffer, int offset, int length) throws IOException;

    /**
     * Writes the last chunk of a byte buffer to the stream.
     *
     * <pre>
     * <code> b b16 b18 bytes </code>
     * </pre>
     *
     * @param value the string value to write.
     */
    abstract public void writeByteBufferEnd(byte[] buffer, int offset, int length) throws IOException;

    /**
     * Writes a full output stream.
     */
    public void writeByteStream(InputStream is) throws IOException {
        try {
            this.writeByteBufferStart();

            if (this.byteBuffer == null) {
                this.byteBuffer = new byte[1024];
            }

            byte[] buffer = this.byteBuffer;

            int len;
            while ((len = is.read(buffer, 0, buffer.length)) > 0) {
                if (len < buffer.length) {
                    int len2 = is.read(buffer, len, buffer.length - len);

                    if (len2 < 0) {
                        this.writeByteBufferEnd(buffer, 0, len);
                        return;
                    }

                    len += len2;
                }

                this.writeByteBufferPart(buffer, 0, len);
            }

            this.writeByteBufferEnd(buffer, 0, 0);
        } finally {
            is.close();
        }
    }

    /**
     * Writes a reference.
     *
     * <pre>
     * <code> Q int </code>
     * </pre>
     *
     * @param value the integer value to write.
     */
    abstract public void writeRef(int value) throws IOException;

    /**
     * Removes a reference.
     */
    public boolean removeRef(Object obj) throws IOException {
        return false;
    }

    /**
     * Replaces a reference from one object to another.
     */
    abstract public boolean replaceRef(Object oldRef, Object newRef) throws IOException;

    /**
     * Adds an object to the reference list. If the object already exists, writes the reference,
     * otherwise, the caller is responsible for the serialization.
     *
     * <pre>
     * <code> R b32 b24 b16 b8 </code>
     * </pre>
     *
     * @param object the object to add as a reference.
     * @return true if the object has already been written.
     */
    abstract public boolean addRef(Object object) throws IOException;

    /**
     * @param obj
     * @return
     */
    abstract public int getRef(Object obj);

    /**
     * Resets the references for streaming.
     */
    public void resetReferences() {
    }

    /**
     * Writes a generic object to the output stream.
     */
    abstract public void writeObject(Object object) throws IOException;

    /**
     * Writes the list header to the stream. List writers will call <code>writeListBegin</code> followed
     * by the list contents and then call <code>writeListEnd</code>.
     *
     * <pre>
     * <code> V x13 java.util.ArrayList # type x93 # length=3 x91 # 1 x92 # 2 x93 # 3 &lt;/list> </code>
     * </pre>
     */
    abstract public boolean writeListBegin(int length, String type) throws IOException;

    /**
     * Writes the tail of the list to the stream.
     */
    abstract public void writeListEnd() throws IOException;

    /**
     * Writes the map header to the stream. Map writers will call <code>writeMapBegin</code> followed by
     * the map contents and then call <code>writeMapEnd</code>.
     *
     * <pre>
     * <code> M type (<key> <value>)* Z </code>
     * </pre>
     */
    abstract public void writeMapBegin(String type) throws IOException;

    /**
     * Writes the tail of the map to the stream.
     */
    abstract public void writeMapEnd() throws IOException;

    /**
     * Writes the object header to the stream (for Hessian 2.0), or a Map for Hessian 1.0. Object
     * writers will call <code>writeObjectBegin</code> followed by the map contents and then call
     * <code>writeObjectEnd</code>.
     *
     * <pre>
     * <code> C type int <key>* C int <value>* </code>
     * </pre>
     *
     * @return true if the object has already been defined.
     */
    public int writeObjectBegin(String type) throws IOException {
        this.writeMapBegin(type);

        return -2;
    }

    /**
     * Writes the end of the class.
     */
    public void writeClassFieldLength(int len) throws IOException {
    }

    /**
     * Writes the tail of the object to the stream.
     */
    public void writeObjectEnd() throws IOException {
    }

    public void writeReply(Object o) throws IOException {
        this.startReply();
        this.writeObject(o);
        this.completeReply();
    }

    public void startReply() throws IOException {
    }

    public void completeReply() throws IOException {
    }

    public void writeFault(String code, String message, Object detail) throws IOException {
    }

    public void flush() throws IOException {
    }

    public void close() throws IOException {
    }

}
