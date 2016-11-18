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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Represents a multi-part content body with chainable setters for convenient building.
 */
public class MultiPartContent {
    private String boundary = "**********";
    private List<Part> parts = new ArrayList<>();

    private MultiPartContent() {
    }

    /**
     * Creates an empty {@link MultiPartContent}.
     *
     * @return An empty {@link MultiPartContent}
     */
    public static MultiPartContent create() {
        MultiPartContent mpc = new MultiPartContent();
        return mpc;
    }

    /**
     * Writes this {@link MultiPartContent} into an {@link OutputStream}.
     *
     * @param outputStream
     * @throws IOException
     */
    protected void write(OutputStream outputStream) throws IOException {
        String crlf = "\r\n";
        String hypens = "--";

        DataOutputStream os = new DataOutputStream(outputStream);

        for (MultiPartContent.Part part : parts) {
            os.writeBytes(hypens + boundary + crlf);
            os.writeBytes("Content-Disposition: form-data; name=\"" + part.getName() + "\";filename=\"" + part.getFileName() + "\"" + crlf);
            Iterator<Map.Entry<String, String>> iterator = part.getHeaders().entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> pair = (Map.Entry) iterator.next();
                os.writeBytes(pair.getKey() + ": " + pair.getValue());
                os.writeBytes(crlf);
            }
            os.writeBytes(crlf);

            os.write(part.getContent());
        }

        os.writeBytes(crlf);
        os.writeBytes(hypens + boundary + hypens + crlf);

        os.flush();
        os.close();
    }

    /**
     * Adds a {@link Part} to this {@link MultiPartContent}.
     *
     * @param part The {@link Part} to add.
     * @return This {@link MultiPartContent} for chaining and convenience.
     */
    public MultiPartContent addPart(Part part) {
        parts.add(part);
        return this;
    }


    /**
     * Sets the part boundary for this {@link MultiPartContent}.
     *
     * @param boundary The {@link Part} to add.
     * @return This {@link MultiPartContent} for chaining and convenience.
     */
    public MultiPartContent setBoundary(String boundary) {
        this.boundary = boundary;
        return this;
    }

    /**
     * Returns the {@link Part}s of this {@link MultiPartContent}.
     *
     * @return A {@link List} of {@link Part}s of this {@link MultiPartContent}.
     */
    public List<Part> getParts() {
        return parts;
    }

    /**
     * Returns the part boundary of this {@link MultiPartContent}.
     *
     * @return The boundary.
     */
    public String getBoundary() {
        return boundary;
    }

    /**
     * Represents and defines a part of a {@link MultiPartContent}.
     */
    public static class Part {
        private String name, fileName;
        private byte[] content;
        private Map<String, String> headers = new HashMap<>();

        private Part() {
        }

        /**
         * Creates {@link MultiPartContent.Part} with the set data.
         *
         * @param name     The name of this {@link Part}.
         * @param fileName The filename of this {@link Part}.
         * @param content  The content of this {@link Part}.
         * @return A {@link MultiPartContent.Part}
         */
        public static Part create(String name, String fileName, byte[] content) {
            Part part = new Part();

            part.name = name;
            part.fileName = fileName;
            part.content = content;

            return part;
        }

        /**
         * Adds a header to this {@link Part}
         *
         * @param key   The header key.
         * @param value The header value.
         * @return This {@link Part} for chaining and convenience.
         */
        public Part addHeader(String key, String value) {
            headers.put(key, value);
            return this;
        }

        /**
         * Returns the name of this {@link Part}.
         *
         * @return The name of this {@link Part}.
         */
        public String getName() {
            return name;
        }


        /**
         * Returns the filename of this {@link Part}.
         *
         * @return The filename of this {@link Part}.
         */
        public String getFileName() {
            return fileName;
        }


        /**
         * Returns the content of this {@link Part}.
         *
         * @return The content of this {@link Part}.
         */
        public byte[] getContent() {
            return content;
        }


        /**
         * Returns the headers of this {@link Part}.
         *
         * @return The {@link Map} of headers.
         */
        public Map<String, String> getHeaders() {
            return headers;
        }
    }
}