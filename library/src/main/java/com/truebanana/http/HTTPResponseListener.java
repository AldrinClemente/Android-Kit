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

/**
 * Classes implementing this interface are responsible for handling the {@link HTTPResponse}or the {@link HTTPRequestError} in case of failure.
 */
public interface HTTPResponseListener {
    /**
     * Called right before the connection initializes.
     */
    void onPreExecute();

    /**
     * Called right after the request flow finishes regardless if it succeeds or not.
     */
    void onPostExecute();

    /**
     * Called when the request completes successfully.
     *
     * @param response The remote host response.
     */
    void onRequestCompleted(HTTPResponse response);

    /**
     * Called when a problem has occurred during the request flow causing it to fail.
     *
     * @param error The error which describes the cause of failure.
     */
    void onRequestError(HTTPRequestError error);
}