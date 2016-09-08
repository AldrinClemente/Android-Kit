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

import com.truebanana.json.JSONUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

/**
 * Represents a response from the remote host.
 */
public class HTTPResponse {
    protected byte[] content = new byte[0];
    protected String stringContent;
    protected int statusCode = -1;
    protected String responseMessage;
    protected Map<String, List<String>> headers;
    protected String requestURL;
    protected HTTPRequest originalRequest;

    protected HTTPResponse() {
    }

    protected static HTTPResponse from(HTTPRequest request, HttpURLConnection connection, InputStream content) {
        HTTPResponse response = new HTTPResponse();

        response.originalRequest = request;
        if (content != null) {
            try {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                byte[] data = new byte[16384];
                int nRead;

                while ((nRead = content.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                buffer.flush();
                response.content = buffer.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            response.statusCode = connection.getResponseCode();
        } catch (IOException e) {
        }

        String message = null;
        try {
            message = connection.getResponseMessage();
        } catch (IOException e) {
            message = e.getLocalizedMessage();
        }
        response.responseMessage = response.statusCode + (message != null ? " " + message : "");

        response.headers = connection.getHeaderFields();

        response.requestURL = connection.getURL().toString();

        return response;
    }

    /**
     * Returns the original {@link HTTPRequest}.
     * @return The original {@link HTTPRequest} object.
     */
    public HTTPRequest getOriginalRequest() {
        return originalRequest;
    }

    /**
     * Returns URL of the original {@link HTTPRequest} {@link String}.
     *
     * @return The {@link String} URL of the original request.
     */
    public String getRequestURL() {
        return requestURL;
    }

    /**
     * Returns the HTTP response status code based on the HTTP/1.1 standard (RFC 7231).
     *
     * @return The HTTP response status code.
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Returns the raw HTTP response body.
     *
     * @return The raw HTTP response body.
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * Returns the HTTP response body converted into a {@link String}.
     *
     * @return The {@link String} HTTP response body.
     */
    public String getStringContent() {
        if (stringContent == null) {
            stringContent = new String(content);
        }
        return stringContent;
    }

    /**
     * Convenience method to parse the HTTP response body into a {@link JSONObject}
     *
     * @return The {@link JSONObject} HTTP response body or <strong>null</strong> parsing fails.
     */
    public JSONObject toJSONObject() {
        return JSONUtils.toJSONObject(getStringContent());
    }


    /**
     * Convenience method to parse the HTTP response body into a {@link JSONArray}
     *
     * @return The {@link JSONArray} HTTP response body or <strong>null</strong> parsing fails.
     */
    public JSONArray toJSONArray() {
        return JSONUtils.toJSONArray(getStringContent());
    }

    /**
     * Returns the response status code and message such as "200 OK" or "404 Not Found".
     *
     * @return The response message.
     */
    public String getResponseMessage() {
        return responseMessage;
    }

    /**
     * Returns the headers of the HTTP response.
     *
     * @return The {@link Map} of headers.
     */
    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    /**
     * Returns the value of a specific header key.
     *
     * @param key The header key to check.
     * @return The value of the header or <strong>null</strong> if it does not exist.
     */
    public String getHeaderField(String key) {
        return headers.containsKey(key) ? headers.get(key).get(0) : null;
    }

    /**
     * Convenience method to check if the HTTP status code is 1xx Informatinoal.
     *
     * @return <strong>true</strong> if the status code is 1xx or <strong>false</strong> otherwise.
     */
    public boolean isInformational() {
        return statusCode >= 100 && statusCode < 200;
    }


    /**
     * Convenience method to check if the HTTP status code is 2xx Success.
     *
     * @return <strong>true</strong> if the status code is 2xx or <strong>false</strong> otherwise.
     */
    public boolean isSuccess() {
        return statusCode >= 200 && statusCode < 300;
    }


    /**
     * Convenience method to check if the HTTP status code is 3xx Redirection.
     *
     * @return <strong>true</strong> if the status code is 3xx or <strong>false</strong> otherwise.
     */
    public boolean isRedirection() {
        return statusCode >= 300 && statusCode < 400;
    }


    /**
     * Convenience method to check if the HTTP status code is 4xx Client Error.
     *
     * @return <strong>true</strong> if the status code is 4xx or <strong>false</strong> otherwise.
     */
    public boolean isClientError() {
        return statusCode >= 400 && statusCode < 500;
    }


    /**
     * Convenience method to check if the HTTP status code is 5xx Server Error.
     *
     * @return <strong>true</strong> if the status code is 5xx or <strong>false</strong> otherwise.
     */
    public boolean isServerError() {
        return statusCode >= 500 && statusCode < 600;
    }


    /**
     * Convenience method to check if there is no valid response due to the failure of the request.
     *
     * @return <strong>true</strong> if the request failed i.e. there is no valid response or <strong>false</strong> otherwise.
     */
    public boolean isConnectionError() {
        return statusCode == -1;
    }
}