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

package com.caucho.hessian.io.serializer;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caucho.hessian.io.AbstractHessianOutput;

/**
 * Serializing an object containing a byte stream.
 */
abstract public class AbstractStreamSerializer extends AbstractSerializer {

    private static final Logger log = LoggerFactory.getLogger(AbstractStreamSerializer.class);

    /**
     * Writes the object to the output stream.
     */
    @Override
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if (out.addRef(obj)) {
            return;
        }

        int ref = out.writeObjectBegin(this.getClassName(obj));

        if (ref < -1) {
            out.writeString("value");

            InputStream is = null;

            try {
                is = this.getInputStream(obj);
            } catch (Exception e) {
                AbstractStreamSerializer.log.warn(null, e);
            }

            if (is != null) {
                try {
                    out.writeByteStream(is);
                } finally {
                    is.close();
                }
            } else {
                out.writeNull();
            }

            out.writeMapEnd();
        } else {
            if (ref == -1) {
                out.writeClassFieldLength(1);
                out.writeString("value");

                out.writeObjectBegin(this.getClassName(obj));
            }

            InputStream is = null;

            try {
                is = this.getInputStream(obj);
            } catch (Exception e) {
                AbstractStreamSerializer.log.warn(null, e);
            }

            try {
                if (is != null) {
                    out.writeByteStream(is);
                } else {
                    out.writeNull();
                }
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }
    }

    protected String getClassName(Object obj) {
        return obj.getClass().getName();
    }

    abstract protected InputStream getInputStream(Object obj) throws IOException;

}
