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

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Represents a response from the remote host.
 */
public class MockResponse extends HTTPResponse {
    private MockResponse() {
    }

    public static class Builder {
        private byte[] content = new byte[0];
        private String stringContent;
        private int statusCode = 200;
        private Map<String, String> headers = Collections.EMPTY_MAP;

        public Builder() {
        }

        public Builder setContent(byte[] content) {
            this.content = content;
            return this;
        }

        public Builder setContent(String content) {
            this.stringContent = content;
            return this;
        }

        public Builder setContent(JSONObject content) {
            this.stringContent = content.toString();
            return this;
        }

        public Builder setContent(InputStream content) {
            if (content != null) {
                try {
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    byte[] data = new byte[16384];
                    int nRead;

                    while ((nRead = content.read(data, 0, data.length)) != -1) {
                        buffer.write(data, 0, nRead);
                    }
                    buffer.flush();
                    this.content = buffer.toByteArray();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return this;
        }

        public Builder setStatusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder addHeader(String key, String value) {
            headers.put(key, value);
            return this;
        }

        public Builder addHeaders(Map<String, String> headers) {
            headers.putAll(headers);
            return this;
        }

        public Builder setHeaders(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public MockResponse build() {
            MockResponse response = new MockResponse();

            if (stringContent != null) {
                response.content = stringContent.getBytes();
                response.stringContent = stringContent;
            } else {
                response.content = content;
                response.stringContent = new String(content);
            }

            response.statusCode = statusCode;
            response.responseMessage = statusCode + " MOCK RESPONSE";

            response.headers = new HashMap<>();
            Iterator<Map.Entry<String, String>> it = headers.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> pair = (Map.Entry) it.next();
                response.headers.put(pair.getKey(), Arrays.asList(new String[]{pair.getValue()}));
            }

            return response;
        }
    }
}