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

import android.net.Uri;
import android.os.Build;
import android.os.Handler;

import com.truebanana.async.Async;
import com.truebanana.log.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * A cool class which makes communicating with web servers faster and easier.<br/>
 * <br />
 * It can be as simple as this for a fire and forget mode:<br />
 * <pre>
 * {@code
 * HTTPRequest.create("http://my.website.com/ping").executeAsync();
 * }
 * </pre>
 * Or with a bit of handling and some logs:<br />
 * <pre>
 * {@code
 * HTTPRequest.create("http://my.website.com/posts")
 *     .setHTTPResponseListener(myListener)
 *     .setLogTag("Get Posts")
 *     .executeAsync();
 * }
 * </pre>
 * Or for some advanced calls:<br />
 * <pre>
 * {@code
 * HTTPRequest.create("https://my.website.com/logIn")
 *     .setHTTPResponseListener(myListener)
 *     .setLogTag("Log In")
 *     .setPost()
 *     .addHeader("Content-Type", "application/json")
 *     .setRequestBody(requestBody)
 *     .setConnectTimeout(30000)
 *     .setReadTimeout(30000)
 *     .setKeyStore(myKeyStore, "password")
 *     .executeAsync();
 * }
 * </pre>
 */
public class HTTPRequest {
    private HTTPResponseListener responseListener = defaultResponseListener;
    private HTTPRequestMethod requestMethod = HTTPRequestMethod.GET;
    private String url;
    private String body;
    private MultiPartContent multiPartContent;
    private String logTag;
    private boolean verifySSL = true;
    private int readTimeout = defaultReadTimeout;
    private int connectTimeout = defaultConnectTimeout;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> queryParameters = new HashMap<>();

    private HTTPRequestBodyProvider bodyProvider;

    private InputStream keyStore, trustStore;
    private String keyStorePassword, trustStorePassword;

    private static int defaultReadTimeout = 10000;
    private static int defaultConnectTimeout = 10000;
    private static HTTPResponseListener defaultResponseListener = new BasicResponseListener() {
    };

    private static Deque<HTTPRequest> requestQueue = new LinkedBlockingDeque<>();
    private static HTTPRequest pendingRequest = null;

    Handler handler = new Handler();

    private HTTPRequest() {
    }

    /**
     * Creates an {@link HTTPRequest} to connect to the specified URL. Use the different setters to modify the request then use {@link HTTPRequest#executeAsync()} to execute the request. By default, the request method is GET with 10000ms connect timeout and read timeout.
     *
     * @param url The URL to connect to.
     * @return An {@link HTTPRequest}
     */
    public static HTTPRequest create(String url) {
        HTTPRequest request = new HTTPRequest();
        request.url = url;
        try {
            URL u = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Invalid URL.");
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Invalid URL.");
        }

        return request;
    }

    private HttpURLConnection buildURLConnection() {
        try {
            Uri.Builder builder = Uri.parse(url).buildUpon();
            Iterator<Map.Entry<String, String>> iterator = queryParameters.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> pair = (Map.Entry) iterator.next();
                builder.appendQueryParameter(pair.getKey(), pair.getValue());
            }

            URL u = new URL(builder.build().toString());

            Proxy proxy = Proxy.NO_PROXY;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB_MR1) {
                String host = System.getProperty("http.proxyHost");
                if (host != null && !host.isEmpty()) {
                    String port = System.getProperty("http.proxyPort");
                    if (port != null && !port.isEmpty()) {
                        proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, Integer.parseInt(port)));
                    }
                }
            }

            HttpURLConnection urlConnection = (HttpURLConnection) u.openConnection(proxy);
            urlConnection.setRequestMethod(requestMethod.name());
            urlConnection.setDoInput(true);
            urlConnection.setConnectTimeout(connectTimeout);
            urlConnection.setReadTimeout(readTimeout);

            switch (requestMethod) {
                case POST:
                case PUT:
                    urlConnection.setDoOutput(true);
                    break;
                default:
                case GET:
                case DELETE:
                    urlConnection.setDoOutput(false);
                    break;
            }
            return urlConnection;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Invalid URL.");
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Invalid URL.");
        }
    }

    /**
     * Creates a clone of this {@link HTTPRequest} instance.
     *
     * @return A clone of this {@link HTTPRequest}.
     */
    public HTTPRequest clone() {
        HTTPRequest r = HTTPRequest.create(url);
        r.setRequestMethod(requestMethod);
        r.setReadTimeout(readTimeout);
        r.setConnectTimeout(connectTimeout);
        r.addHeaders(headers);
        r.addQueryParameters(queryParameters);
        r.setRequestBody(body);
        r.setRequestBodyProvider(bodyProvider);
        r.setTrustStore(trustStore, trustStorePassword);
        r.setKeyStore(keyStore, keyStorePassword);
        r.setHTTPResponseListener(responseListener);
        r.setSSLVerificationEnabled(verifySSL);
        r.setLogTag(logTag);

        return r;
    }

    /**
     * Sets the request method of this {@link HTTPRequest}.
     *
     * @param requestMethod The {@link HTTPRequestMethod} to use.
     * @return This {@link HTTPRequest} for chaining and convenience.
     */
    public HTTPRequest setRequestMethod(HTTPRequestMethod requestMethod) {
        this.requestMethod = requestMethod;
        return this;
    }

    /**
     * Convenience method to set the {@link HTTPRequestMethod} to {@link HTTPRequestMethod#GET}.
     *
     * @return This {@link HTTPRequest} for chaining and convenience.
     */
    public HTTPRequest setGet() {
        setRequestMethod(HTTPRequestMethod.GET);
        return this;
    }

    /**
     * Convenience method to set the {@link HTTPRequestMethod} to {@link HTTPRequestMethod#POST}.
     *
     * @return This {@link HTTPRequest} for chaining and convenience.
     */
    public HTTPRequest setPost() {
        setRequestMethod(HTTPRequestMethod.POST);
        return this;
    }

    /**
     * Convenience method to set the {@link HTTPRequestMethod} to {@link HTTPRequestMethod#PUT}.
     *
     * @return This {@link HTTPRequest} for chaining and convenience.
     */
    public HTTPRequest setPut() {
        setRequestMethod(HTTPRequestMethod.PUT);
        return this;
    }

    /**
     * Convenience method to set the {@link HTTPRequestMethod} to {@link HTTPRequestMethod#DELETE}.
     *
     * @return This {@link HTTPRequest} for chaining and convenience.
     */
    public HTTPRequest setDelete() {
        setRequestMethod(HTTPRequestMethod.DELETE);
        return this;
    }

    /**
     * Sets the read timeout of this {@link HTTPRequest}. The default is 10000.
     *
     * @param millis The read timeout in milliseconds.
     * @return This {@link HTTPRequest} for chaining and convenience.
     */
    public HTTPRequest setReadTimeout(int millis) {
        this.readTimeout = millis;
        return this;
    }

    /**
     * Sets the connect timeout of this {@link HTTPRequest}. The default is 10000.
     *
     * @param millis The connect timeout in milliseconds.
     * @return This {@link HTTPRequest} for chaining and convenience.
     */
    public HTTPRequest setConnectTimeout(int millis) {
        this.connectTimeout = millis;
        return this;
    }

    /**
     * Adds a header entry to this {@link HTTPRequest}.
     *
     * @param key   The header key.
     * @param value The header value.
     * @return This {@link HTTPRequest} for chaining and convenience.
     */
    public HTTPRequest addHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    /**
     * Adds header entries to this {@link HTTPRequest}.
     *
     * @param headers A {@link Map} of header key-value pairs.
     * @return This {@link HTTPRequest} for chaining and convenience.
     */
    public HTTPRequest addHeaders(Map<String, String> headers) {
        Set<Map.Entry<String, String>> entries = headers.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            addHeader(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * Adds a query parameter to this {@link HTTPRequest}.
     *
     * @param key   The parameter key.
     * @param value The parameter value.
     * @return This {@link HTTPRequest} for chaining and convenience.
     */
    public HTTPRequest addQueryParameter(String key, String value) {
        queryParameters.put(key, value);
        return this;
    }

    /**
     * Adds query parameters to this {@link HTTPRequest}.
     *
     * @param headers A {@link Map} of query parameter key-value pairs.
     * @return This {@link HTTPRequest} for chaining and convenience.
     */
    public HTTPRequest addQueryParameters(Map<String, String> headers) {
        Set<Map.Entry<String, String>> entries = headers.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            addQueryParameter(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * Sets the value of the specified header key to the provided value if it already exists in this {@link HTTPRequest}.
     *
     * @param key   The header key.
     * @param value The header value.
     * @return This {@link HTTPRequest} for chaining and convenience.
     */
    public HTTPRequest setHeaderValue(String key, String value) {
        headers.put(key, value);
        return this;
    }

    /**
     * Sets the request body of this {@link HTTPRequest}.
     *
     * @param body The string content.
     * @return This {@link HTTPRequest} for chaining and convenience.
     */
    public HTTPRequest setRequestBody(String body) {
        this.body = body;
        return this;
    }

    /**
     * Sets the request body of this {@link HTTPRequest}.
     *
     * @param body The {@link JSONObject} content.
     * @return This {@link HTTPRequest} for chaining and convenience.
     */
    public HTTPRequest setRequestBody(JSONObject body) {
        return setRequestBody(body.toString());
    }

    /**
     * Sets the request body of this {@link HTTPRequest}. This will automatically add the necessary headers to flag this request as multipart/form-data.
     *
     * @param body The {@link MultiPartContent} body.
     * @return This {@link HTTPRequest} for chaining and convenience.
     */
    public HTTPRequest setRequestBody(MultiPartContent body) {
        this.multiPartContent = body;
        this.setHeaderValue("Content-Type", "multipart/form-data;boundary=" + body.getBoundary());
        return this;
    }

    /**
     * Sets a {@link HTTPRequestBodyProvider} for just-in-time building of the request body.
     *
     * @param provider The {@link HTTPRequestBodyProvider}
     * @return This {@link HTTPRequest} for chaining and convenience.
     */
    public HTTPRequest setRequestBodyProvider(HTTPRequestBodyProvider provider) {
        this.bodyProvider = provider;
        return this;
    }

    /**
     * Sets the BKS trust store to use for this {@link HTTPRequest} for server authentication.
     *
     * @param trustStore The BKS trust store {@link InputStream}.
     * @param password   The password for the trust store.
     * @return This {@link HTTPRequest} for chaining and convenience.
     */
    public HTTPRequest setTrustStore(InputStream trustStore, String password) {
        this.trustStore = trustStore;
        this.trustStorePassword = password;
        return this;
    }

    /**
     * Sets the PKCS12 key store to use for this {@link HTTPRequest} for client authentication.
     *
     * @param keyStore The PKCS12 key store {@link InputStream}.
     * @param password The password for the key store.
     * @return This {@link HTTPRequest} for chaining and convenience.
     */
    public HTTPRequest setKeyStore(InputStream keyStore, String password) {
        this.keyStore = keyStore;
        this.keyStorePassword = password;
        return this;
    }

    /**
     * Sets the {@link HTTPResponseListener} to use for this {@link HTTPRequest}.
     *
     * @param listener The {@link HTTPResponseListener}.
     * @return This {@link HTTPRequest} for chaining and convenience.
     */
    public HTTPRequest setHTTPResponseListener(HTTPResponseListener listener) {
        this.responseListener = listener;
        return this;
    }


    /**
     * Enables or disables server SSL verification for this {@link HTTPRequest}. Disabling SSL verification will authorize any server for communication and ignores any trust store provided in {@link HTTPRequest#setTrustStore(InputStream, String)}.
     *
     * @param enabled <strong>true</strong> to enable SSL verification or <strong>false</strong> to disable it.
     * @return This {@link HTTPRequest} for chaining and convenience.
     */
    public HTTPRequest setSSLVerificationEnabled(boolean enabled) {
        this.verifySSL = enabled;
        return this;
    }

    /**
     * Enables logging of debug information for this {@link HTTPRequest}. This will automatically log the request URL, request headers, request body, response message, response content and other useful information in the LOGCAT.
     *
     * @param tag The tag to use for logging.
     * @return This {@link HTTPRequest} for chaining and convenience.
     */
    public HTTPRequest setLogTag(String tag) {
        this.logTag = tag;
        return this;
    }

    /**
     * Returns the request body set in this {@link HTTPRequest}.
     *
     * @return The request body.
     */
    public String getRequestBody() {
        return body;
    }


    /**
     * Returns the {@link MultiPartContent} request body set in this {@link HTTPRequest}.
     *
     * @return The {@link MultiPartContent} request body.
     */
    public MultiPartContent getMultiPartContentRequestBody() {
        return multiPartContent;
    }

    private void onPreExecute() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                responseListener.onPreExecute();
            }
        });
    }

    private void onPostExecute() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                responseListener.onPostExecute();
            }
        });
    }

    private void onRequestCompleted(final HTTPResponse response) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                responseListener.onRequestCompleted(response);
            }
        });
    }

    private void onRequestError(final HTTPRequestError error) {
        if (logTag != null) {
            Log.d(logTag + " Request Error", error.name());
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                responseListener.onRequestError(error);
            }
        });
    }

    private void onRequestTerminated() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (HTTPRequest.pendingRequest == HTTPRequest.this) {
                    HTTPRequest.pendingRequest = null;
                    executeFromQueueIfFree();
                }
            }
        });
    }

    /**
     * Executes this {@link HTTPRequest} asynchronously. To hook to events or listen to the server response, you must provide an {@link HTTPResponseListener} using {@link HTTPRequest#setHTTPResponseListener(HTTPResponseListener)}.
     *
     * @return This {@link HTTPRequest}
     */
    public HTTPRequest executeAsync() {
        Async.executeAsync(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = buildURLConnection();

                // Get request body now if there's a provider
                if (bodyProvider != null) {
                    body = bodyProvider.getRequestBody();
                }

                // Update socket factory as needed
                if (urlConnection instanceof HttpsURLConnection) {
                    HttpsURLConnection httpsURLConnection = (HttpsURLConnection) urlConnection;

                    try {
                        httpsURLConnection.setSSLSocketFactory(new FlexibleSSLSocketFactory(trustStore, trustStorePassword, keyStore, keyStorePassword, !verifySSL));
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                        onRequestError(HTTPRequestError.SECURITY_EXCEPTION);
                        onRequestTerminated();
                        return; // Terminate now
                    } catch (IOException e) {
                        e.printStackTrace();
                        onRequestError(HTTPRequestError.KEYSTORE_INVALID);
                        onRequestTerminated();
                        return; // Terminate now
                    }

                    if (!verifySSL) {
                        httpsURLConnection.setHostnameVerifier(new NoVerifyHostnameVerifier());
                        if (logTag != null) {
                            Log.d(logTag + " SSL Verification Disabled", "**********");
                        }
                    }
                }

                // Log important info as needed
                if (logTag != null) {
                    Log.d(logTag + " Endpoint", urlConnection.getURL().toString());
                    Iterator<Map.Entry<String, String>> iterator = headers.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, String> pair = (Map.Entry) iterator.next();
                        urlConnection.addRequestProperty(pair.getKey(), pair.getValue());
                        Log.d(logTag + " Request Header", pair.getKey() + ": " + pair.getValue());
                    }
                    if (multiPartContent != null) {
                        Log.d(logTag + " Multipart Request Boundary", multiPartContent.getBoundary());
                        int counter = 1;
                        for (MultiPartContent.Part part : multiPartContent.getParts()) {
                            Log.d(logTag + " Request Body Part " + counter, "Name: " + part.getName() + "; File Name: " + part.getFileName());

                            Iterator<Map.Entry<String, String>> it = part.getHeaders().entrySet().iterator();
                            while (it.hasNext()) {
                                Map.Entry<String, String> pair = (Map.Entry) it.next();
                                Log.d(logTag + " Request Body Part " + counter + " Header", pair.getKey() + ": " + pair.getValue());
                            }
                        }
                    } else {
                        Log.d(logTag + " Request Body", body);
                    }
                }

                // Trigger pre-execute since preparations are complete
                onPreExecute();

                // Write our request body
                try {
                    if (multiPartContent != null) {
                        multiPartContent.write(urlConnection.getOutputStream());
                    } else if (body != null) {
                        OutputStream os = urlConnection.getOutputStream();
                        OutputStreamWriter writer = new OutputStreamWriter(os);
                        writer.write(body);
                        writer.flush();
                        writer.close();
                        os.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    onRequestError(HTTPRequestError.OTHER);
                    onRequestTerminated();
                    return; // Terminate now
                }

                // Get the response
                InputStream content;
                try {
                    content = urlConnection.getInputStream();
                    onPostExecute();
                } catch (SocketTimeoutException e) { // Timeout
                    e.printStackTrace();
                    onPostExecute();
                    onRequestError(HTTPRequestError.TIMEOUT);
                    onRequestTerminated();
                    return; // Terminate now
                } catch (IOException e) { // All other exceptions
                    e.printStackTrace();
                    content = urlConnection.getErrorStream();
                    onPostExecute();
                }

                // Pre-process the response
                final HTTPResponse response = HTTPResponse.from(HTTPRequest.this, urlConnection, content);

                if (response.isConnectionError()) {
                    onRequestError(HTTPRequestError.OTHER);
                    onRequestTerminated();
                    return; // Terminate now
                }

                // Log response
                if (logTag != null) {
                    Log.d(logTag + " Response Message", response.getResponseMessage());
                    Log.d(logTag + " Response Content", response.getStringContent());
                }

                // Trigger request completed and return the response
                onRequestCompleted(response);

                // Terminate the connection
                urlConnection.disconnect();

                onRequestTerminated();
            }
        });
        return this;
    }

    /**
     * Queues this {@link HTTPRequest} for serial asynchronous execution. Note that the queue will not wait for {@link HTTPRequest}s executed using {@link HTTPRequest#executeAsync()} to finish, and such requests will never be considered as part of the queue at any time.
     * <p>
     * To hook to events or listen to the server response, you must provide an {@link HTTPResponseListener} using {@link HTTPRequest#setHTTPResponseListener(HTTPResponseListener)}.
     *
     * @return This {@link HTTPRequest}
     */
    public HTTPRequest queue() {
        HTTPRequest.requestQueue.add(this);
        executeFromQueueIfFree();
        return this;
    }

    /**
     * Inserts this {@link HTTPRequest} to the front of the queue for serial asynchronous execution. Note that the queue will not wait for {@link HTTPRequest}s executed using {@link HTTPRequest#executeAsync()} to finish, and such requests will never be considered as part of the queue at any time.
     * <p>
     * To hook to events or listen to the server response, you must provide an {@link HTTPResponseListener} using {@link HTTPRequest#setHTTPResponseListener(HTTPResponseListener)}.
     *
     * @return This {@link HTTPRequest}
     */
    public HTTPRequest queueFirst() {
        HTTPRequest.requestQueue.addFirst(this);
        executeFromQueueIfFree();
        return this;
    }

    /**
     * Removes this {@link HTTPRequest} from the queue.
     *
     * @return This {@link HTTPRequest}
     */
    public HTTPRequest dequeue() {
        HTTPRequest.requestQueue.remove(this);
        return this;
    }

    private void executeFromQueueIfFree() {
        if (HTTPRequest.pendingRequest == null && !HTTPRequest.requestQueue.isEmpty()) {
            HTTPRequest.pendingRequest = requestQueue.remove();
            HTTPRequest.pendingRequest.executeAsync();
        }
    }

    /**
     * Returns the request queue
     *
     * @return The {@link HTTPRequest} {@link Queue}
     */
    public static Queue<HTTPRequest> getRequestQueue() {
        return requestQueue;
    }

    /**
     * Clears the request queue
     */
    public static void clearRequestQueue() {
        requestQueue.clear();
    }

    private static class NoVerifyTrustManager implements X509TrustManager {
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }
    }

    private static class NoVerifyHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    private class FlexibleSSLSocketFactory extends SSLSocketFactory {
        SSLContext sslContext;

        public FlexibleSSLSocketFactory(boolean trustAll) throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyManagementException, KeyStoreException, IOException {
            this(null, null, null, null, trustAll);
        }

        public FlexibleSSLSocketFactory(InputStream keyStoreStream, String keyStorePassword, boolean trustAll) throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyManagementException, KeyStoreException, IOException {
            this(null, null, keyStoreStream, keyStorePassword, trustAll);
        }

        public FlexibleSSLSocketFactory(InputStream trustStoreStream, String trustStorePassword) throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyManagementException, KeyStoreException, IOException {
            this(trustStoreStream, trustStorePassword, null, null, false);
        }

        public FlexibleSSLSocketFactory(InputStream trustStoreStream, String trustStorePassword, InputStream keyStoreStream, String keyStorePassword, boolean trustAll) throws KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException {
            super();
            sslContext = SSLContext.getInstance("TLS");

            TrustManager[] trustManagers = null;
            KeyManager[] keyManagers = null;

            if (trustAll) {
                trustManagers = new TrustManager[]{new NoVerifyTrustManager()};
            } else if (trustStoreStream != null) {
                // Load trust store certificate
                KeyStore trustStore = KeyStore.getInstance("BKS");
                trustStore.load(trustStoreStream, trustStorePassword.toCharArray());

                // Initialize trust manager factory with the trust store
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                tmf.init(trustStore);

                trustManagers = tmf.getTrustManagers();
            }

            if (keyStoreStream != null) {
                // Load client certificate
                KeyStore ks = KeyStore.getInstance("PKCS12");
                ks.load(keyStoreStream, keyStorePassword.toCharArray());

                // Initialize key manager factory with the client certificate
                KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                kmf.init(ks, keyStorePassword.toCharArray());

                keyManagers = kmf.getKeyManagers();
            }

            sslContext.init(keyManagers, trustManagers, null);
        }

        @Override
        public String[] getDefaultCipherSuites() {
            return sslContext.getSocketFactory().getDefaultCipherSuites();
        }

        @Override
        public String[] getSupportedCipherSuites() {
            return sslContext.getSocketFactory().getSupportedCipherSuites();
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port,
                                   boolean autoClose) throws IOException {
            return processSocket((SSLSocket) sslContext.getSocketFactory().createSocket(socket, host, port, autoClose));
        }

        @Override
        public Socket createSocket() throws IOException {
            return processSocket((SSLSocket) sslContext.getSocketFactory().createSocket());
        }

        @Override
        public Socket createSocket(String host, int port) throws IOException {
            return processSocket((SSLSocket) sslContext.getSocketFactory().createSocket(host, port));
        }

        @Override
        public Socket createSocket(String host, int port, InetAddress localHost,
                                   int localPort) throws IOException {
            return processSocket((SSLSocket) sslContext.getSocketFactory().createSocket(host, port, localHost, localPort));
        }

        @Override
        public Socket createSocket(InetAddress host, int port) throws IOException {
            return processSocket((SSLSocket) sslContext.getSocketFactory().createSocket(host, port));
        }

        @Override
        public Socket createSocket(InetAddress address, int port, InetAddress localAddress,
                                   int localPort) throws IOException {
            return processSocket((SSLSocket) sslContext.getSocketFactory().createSocket(address, port, localAddress, localPort));
        }

        private SSLSocket processSocket(SSLSocket socket) {
            List<String> cipherSuites = new ArrayList<>(Arrays.asList(socket.getEnabledCipherSuites()));
            cipherSuites.add("SSL_RSA_WITH_3DES_EDE_CBC_SHA");

            String[] modifiedSuites = new String[cipherSuites.size()];
            socket.setEnabledCipherSuites(cipherSuites.toArray(modifiedSuites));
            return socket;
        }
    }
}