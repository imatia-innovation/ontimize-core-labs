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

package com.caucho.hessian.io;

import java.io.InputStream;
import java.io.OutputStream;

import com.caucho.hessian.util.HessianFreeList;

/**
 * Factory for creating HessianInput and HessianOutput streams.
 */
public class HessianFactory {

    private SerializerFactory serializerFactory;

    private final SerializerFactory defaultSerializerFactory;

    private final HessianFreeList<Hessian2Output> freeHessian2Output = new HessianFreeList<>(32);

    private final HessianFreeList<Hessian2Input> freeHessian2Input = new HessianFreeList<>(32);

    public HessianFactory() {
        this.defaultSerializerFactory = SerializerFactory.createDefault();
        this.serializerFactory = this.defaultSerializerFactory;
    }

    public void setSerializerFactory(SerializerFactory factory) {
        this.serializerFactory = factory;
    }

    public SerializerFactory getSerializerFactory() {
        // the default serializer factory cannot be modified by external
        // callers
        if (this.serializerFactory == this.defaultSerializerFactory) {
            this.serializerFactory = new SerializerFactory();
        }

        return this.serializerFactory;
    }

    /**
     * Creates a new Hessian 2.0 deserializer.
     */
    public Hessian2Input createHessian2Input(InputStream is) {
        Hessian2Input in = this.freeHessian2Input.allocate();

        if (in == null) {
            in = new Hessian2Input(is);
            in.setSerializerFactory(this.getSerializerFactory());
        } else {
            in.init(is);
        }

        return in;
    }

    /**
     * Frees a Hessian 2.0 deserializer
     */
    public void freeHessian2Input(Hessian2Input in) {
        if (in == null) {
            return;
        }

        in.free();

        this.freeHessian2Input.free(in);
    }

    /**
     * Creates a new Hessian 2.0 deserializer.
     */
    public Hessian2StreamingInput createHessian2StreamingInput(InputStream is) {
        Hessian2StreamingInput in = new Hessian2StreamingInput(is);
        in.setSerializerFactory(this.getSerializerFactory());

        return in;
    }

    /**
     * Frees a Hessian 2.0 deserializer
     */
    public void freeHessian2StreamingInput(Hessian2StreamingInput in) {
    }

    /**
     * Creates a new Hessian 2.0 serializer.
     */
    public Hessian2Output createHessian2Output(OutputStream os) {
        Hessian2Output out = this.createHessian2Output();

        out.init(os);

        return out;
    }

    /**
     * Creates a new Hessian 2.0 serializer.
     */
    public Hessian2Output createHessian2Output() {
        Hessian2Output out = this.freeHessian2Output.allocate();

        if (out == null) {
            out = new Hessian2Output();

            out.setSerializerFactory(this.getSerializerFactory());
        }

        return out;
    }

    /**
     * Frees a Hessian 2.0 serializer
     */
    public void freeHessian2Output(Hessian2Output out) {
        if (out == null) {
            return;
        }

        out.free();

        this.freeHessian2Output.free(out);
    }

    /**
     * Creates a new Hessian 2.0 serializer.
     */
    public Hessian2StreamingOutput createHessian2StreamingOutput(OutputStream os) {
        Hessian2Output out = this.createHessian2Output(os);

        return new Hessian2StreamingOutput(out);
    }

    /**
     * Frees a Hessian 2.0 serializer
     */
    public void freeHessian2StreamingOutput(Hessian2StreamingOutput out) {
        if (out == null) {
            return;
        }

        this.freeHessian2Output(out.getHessian2Output());
    }

}
