/*
 * MIT License
 *
 * Copyright (c) 2016 Aldrin Clemente
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.truebanana.http;

import java.io.InputStream;
import java.io.OutputStream;

public enum HTTPRequestError {
    /**
     * Indicates a connect or read timeout.
     */
    TIMEOUT,

    /**
     * Indicates a security-related problem with the trust store or key store provided using {@link HTTPRequest#setTrustStore(InputStream, String)} or {@link HTTPRequest#setKeyStore(InputStream, String)}.<br />
     * Possible causes:<br/>
     * &middot; The trust store is not a BKS<br/>
     * &middot; The keystore is not a PKCS12<br/>
     * &middot; The password provided is incorrect
     */
    SECURITY_EXCEPTION,

    /**
     * Indicates a problem when reading the trust store or key store file provided using {@link HTTPRequest#setTrustStore(InputStream, String)} or {@link HTTPRequest#setKeyStore(InputStream, String)}.<br />
     * Possible causes:<br/>
     * &middot; Corrupt keystore file<br/>
     * &middot; The file is not really a keystore<br/>
     * &middot; Invalid {@link InputStream}
     */
    KEYSTORE_INVALID,

    /**
     * Indicates other problems which caused the request to fail or terminate pre-maturely.
     * Possible causes:<br/>
     * &middot; A problem occurred when writing to the connection's {@link OutputStream}<br/>
     * &middot; Hostname verification failed<br/>
     * &middot; Server certificate is expired or invalid<br/>
     */
    OTHER;
}