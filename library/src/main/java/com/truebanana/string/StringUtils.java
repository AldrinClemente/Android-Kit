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

package com.truebanana.string;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Includes {@link String}-related utility methods.
 */
public class StringUtils {

    /**
     * Checks if the URL is valid
     *
     * @param url The URL to check
     * @return <b>true</b> if valid, <b>false</b> otherwise
     */
    public static boolean isValidURL(String url) {
        return isValidURL(url, false);
    }

    /**
     * Checks if the URL is valid
     *
     * @param url      The URL to check
     * @param checkDot Set to <b>true</b> to check for a dot (.), <b>false</b> to skip it
     * @return <b>true</b> if valid, <b>false</b> otherwise
     */
    public static boolean isValidURL(String url, boolean checkDot) {
        if (checkDot) {
            if (!url.contains(".")) {
                return false;
            }
        }
        if (!url.startsWith("http://") && !url.startsWith("https://") && !url.contains("://")) {
            url = "http://" + url;
        }
        try {
            URL u = new URL(url);
            u.getPath();
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    /**
     * Checks if the email address is valid
     *
     * @param email The email address to check
     * @return <b>true</b> if valid, <b>false</b> otherwise
     */
    public static boolean isValidEmail(String email) {
        return email.matches("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}\\b");
    }

    // Substring
    // ************************************************************************

    /**
     * Returns a substring of the specified length from the left of the given string
     *
     * @param str    The original string
     * @param length The length or the number of characters to get from the string
     * @return The substring
     */
    public static String left(String str, int length) {
        return str.substring(0, length);
    }

    /**
     * Returns a substring of the specified length from the right of the given string
     *
     * @param str    The original string
     * @param length The length or the number of characters to get from the string
     * @return The substring
     */
    public static String right(String str, int length) {
        return str.substring(str.length() - length, str.length());
    }
}