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

package com.caucho.hessian.security;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.HessianEnvelope;

public class X509Encryption extends HessianEnvelope {

    private String algorithm = "AES";

    // certificate for encryption/decryption
    private X509Certificate cert;

    // private key for decryption
    private PrivateKey privateKey;

    private SecureRandom secureRandom;

    public X509Encryption() {
    }

    /**
     * Sets the encryption algorithm for the content.
     */
    public void setAlgorithm(String algorithm) {
        if (algorithm == null) {
            throw new NullPointerException();
        }

        this.algorithm = algorithm;
    }

    /**
     * Gets the encryption algorithm for the content.
     */
    public String getAlgorithm() {
        return this.algorithm;
    }

    /**
     * The X509 certificate to obtain the public key of the recipient.
     */
    public X509Certificate getCertificate() {
        return this.cert;
    }

    /**
     * The X509 certificate to obtain the public key of the recipient.
     */
    public void setCertificate(X509Certificate cert) {
        this.cert = cert;
    }

    /**
     * The private key for decryption.
     */
    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    /**
     * The X509 certificate to obtain the public key of the recipient.
     */
    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    /**
     * The random number generator for the shared secrets.
     */
    public SecureRandom getSecureRandom() {
        return this.secureRandom;
    }

    /**
     * The random number generator for the shared secrets.
     */
    public void setSecureRandom(SecureRandom random) {
        this.secureRandom = random;
    }

    @Override
    public Hessian2Output wrap(Hessian2Output out) throws IOException {
        if (this.cert == null) {
            throw new IOException("X509Encryption.wrap requires a certificate");
        }

        OutputStream os = new EncryptOutputStream(out);

        Hessian2Output filterOut = new Hessian2Output(os);

        filterOut.setCloseStreamOnClose(true);

        return filterOut;
    }

    @Override
    public Hessian2Input unwrap(Hessian2Input in) throws IOException {
        if (this.privateKey == null) {
            throw new IOException("X509Encryption.unwrap requires a private key");
        }

        if (this.cert == null) {
            throw new IOException("X509Encryption.unwrap requires a certificate");
        }

        int version = in.readEnvelope();

        String method = in.readMethod();

        if (!method.equals(this.getClass().getName())) {
            throw new IOException(
                    "expected hessian Envelope method '" + this.getClass().getName() + "' at '" + method + "'");
        }

        return this.unwrapHeaders(in);
    }

    @Override
    public Hessian2Input unwrapHeaders(Hessian2Input in) throws IOException {
        if (this.privateKey == null) {
            throw new IOException("X509Encryption.unwrap requires a private key");
        }

        if (this.cert == null) {
            throw new IOException("X509Encryption.unwrap requires a certificate");
        }

        InputStream is = new EncryptInputStream(in);

        Hessian2Input filter = new Hessian2Input(is);

        filter.setCloseStreamOnClose(true);

        return filter;
    }

    class EncryptOutputStream extends OutputStream {

        private Hessian2Output _out;

        private Cipher _cipher;

        private OutputStream _bodyOut;

        private CipherOutputStream _cipherOut;

        EncryptOutputStream(Hessian2Output out) throws IOException {
            try {
                this._out = out;

                KeyGenerator keyGen = KeyGenerator.getInstance(X509Encryption.this.algorithm);

                if (X509Encryption.this.secureRandom != null) {
                    keyGen.init(X509Encryption.this.secureRandom);
                }

                SecretKey sharedKey = keyGen.generateKey();

                this._out = out;

                this._out.startEnvelope(X509Encryption.class.getName());

                PublicKey publicKey = X509Encryption.this.cert.getPublicKey();

                byte[] encoded = publicKey.getEncoded();
                MessageDigest md = MessageDigest.getInstance("SHA1");
                md.update(encoded);
                byte[] fingerprint = md.digest();

                String keyAlgorithm = publicKey.getAlgorithm();
                Cipher keyCipher = Cipher.getInstance(keyAlgorithm);
                if (X509Encryption.this.secureRandom != null) {
                    keyCipher.init(Cipher.WRAP_MODE, X509Encryption.this.cert, X509Encryption.this.secureRandom);
                } else {
                    keyCipher.init(Cipher.WRAP_MODE, X509Encryption.this.cert);
                }

                byte[] encKey = keyCipher.wrap(sharedKey);

                this._out.writeInt(4);

                this._out.writeString("algorithm");
                this._out.writeString(X509Encryption.this.algorithm);
                this._out.writeString("fingerprint");
                this._out.writeBytes(fingerprint);
                this._out.writeString("key-algorithm");
                this._out.writeString(keyAlgorithm);
                this._out.writeString("key");
                this._out.writeBytes(encKey);

                this._bodyOut = this._out.getBytesOutputStream();

                this._cipher = Cipher.getInstance(X509Encryption.this.algorithm);
                if (X509Encryption.this.secureRandom != null) {
                    this._cipher.init(Cipher.ENCRYPT_MODE, sharedKey, X509Encryption.this.secureRandom);
                } else {
                    this._cipher.init(Cipher.ENCRYPT_MODE, sharedKey);
                }

                this._cipherOut = new CipherOutputStream(this._bodyOut, this._cipher);
            } catch (RuntimeException e) {
                throw e;
            } catch (IOException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void write(int ch) throws IOException {
            this._cipherOut.write(ch);
        }

        @Override
        public void write(byte[] buffer, int offset, int length) throws IOException {
            this._cipherOut.write(buffer, offset, length);
        }

        @Override
        public void close() throws IOException {
            Hessian2Output out = this._out;
            this._out = null;

            if (out != null) {
                this._cipherOut.close();
                this._bodyOut.close();

                out.writeInt(0);
                out.completeEnvelope();
                out.close();
            }
        }

    }

    class EncryptInputStream extends InputStream {

        private Hessian2Input _in;

        private Cipher _cipher;

        private InputStream _bodyIn;

        private CipherInputStream _cipherIn;

        EncryptInputStream(Hessian2Input in) throws IOException {
            try {
                this._in = in;

                byte[] fingerprint = null;
                String keyAlgorithm = null;
                String algorithm = null;
                byte[] encKey = null;

                int len = in.readInt();

                for (int i = 0; i < len; i++) {
                    String header = in.readString();

                    if ("fingerprint".equals(header)) {
                        fingerprint = in.readBytes();
                    } else if ("key-algorithm".equals(header)) {
                        keyAlgorithm = in.readString();
                    } else if ("algorithm".equals(header)) {
                        algorithm = in.readString();
                    } else if ("key".equals(header)) {
                        encKey = in.readBytes();
                    } else {
                        throw new IOException("'" + header + "' is an unexpected header");
                    }
                }

                Cipher keyCipher = Cipher.getInstance(keyAlgorithm);
                keyCipher.init(Cipher.UNWRAP_MODE, X509Encryption.this.privateKey);

                Key key = keyCipher.unwrap(encKey, algorithm, Cipher.SECRET_KEY);
                this._bodyIn = this._in.readInputStream();

                this._cipher = Cipher.getInstance(algorithm);
                this._cipher.init(Cipher.DECRYPT_MODE, key);

                this._cipherIn = new CipherInputStream(this._bodyIn, this._cipher);
            } catch (RuntimeException e) {
                throw e;
            } catch (IOException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public int read() throws IOException {
            return this._cipherIn.read();
        }

        @Override
        public int read(byte[] buffer, int offset, int length) throws IOException {
            return this._cipherIn.read(buffer, offset, length);
        }

        @Override
        public void close() throws IOException {
            Hessian2Input in = this._in;
            this._in = null;

            if (in != null) {
                this._cipherIn.close();
                this._bodyIn.close();

                int len = in.readInt();

                if (len != 0) {
                    throw new IOException("Unexpected footer");
                }

                in.completeEnvelope();

                in.close();
            }
        }

    }

}
