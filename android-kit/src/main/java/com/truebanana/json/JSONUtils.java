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

package com.truebanana.json;

import com.truebanana.log.Log;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Includes JSON-related utility methods.
 */
public class JSONUtils {

    private static Object getFromJSONObjectOrArray(Object jsonObjectOrArray, String keyPath) {
        Object vValue = null;
        if (jsonObjectOrArray != null) {
            String[] vKeys = keyPath.split("/");
            Object vTempObject = jsonObjectOrArray;
            String vTempValue = "";
            for (int i = 0; i < vKeys.length; i++) {
                try {
                    if (vTempObject instanceof JSONObject) {
                        if (i == vKeys.length - 1) {
                            vValue = ((JSONObject) vTempObject).get(vKeys[i]);
                        } else {
                            vTempValue = ((JSONObject) vTempObject).getString(vKeys[i]);
                        }
                    } else if (vTempObject instanceof JSONArray) {
                        if (i == vKeys.length - 1) {
                            vValue = ((JSONArray) vTempObject).get(Integer.valueOf(vKeys[i]));
                        } else {
                            vTempValue = ((JSONArray) vTempObject).getString(Integer.valueOf(vKeys[i]));
                        }
                    } else {
                        break;
                    }

                    if (vTempValue != null) {
                        if (vTempValue.length() > 0) {
                            try {
                                vTempObject = new JSONObject(vTempValue);
                            } catch (Exception e) {
                                try {
                                    vTempObject = new JSONArray(vTempValue);
                                } catch (Exception e1) {
                                    Log.e("JSONUtils.getFromJSONObjectOrArray()", e.getMessage());
                                    break;
                                }
                            }
                        }
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    Log.e("JSONUtils.getFromJSONObjectOrArray()", e.getMessage());
                }
            }
        }
        return vValue;
    }

    /**
     * Converts a String to a JSONObject
     *
     * @param jsonString The string to convert
     * @return The resulting JSONObject or null if the string could not be converted to a JSONObject
     */
    public static JSONObject toJSONObject(String jsonString) {
        JSONObject vJSON = null;
        try {
            if (jsonString != null) {
                if (jsonString.length() > 0) {
                    vJSON = new JSONObject(jsonString);
                }
            }
        } catch (Exception e) {
            Log.e("JSONUtils.toJSONObject()", e.getMessage());
        }
        return vJSON;
    }

    /**
     * Converts a String to a JSONArray
     *
     * @param jsonString The string to convert
     * @return The resulting JSONArray or null if the string could not be converted to a JSONArray
     */
    public static JSONArray toJSONArray(String jsonString) {
        JSONArray vJSON = null;
        try {
            if (jsonString != null) {
                if (jsonString.length() > 0) {
                    vJSON = new JSONArray(jsonString);
                }
            }
        } catch (Exception e) {
            Log.e("JSONUtils.toJSONArray()", e.getMessage());
        }
        return vJSON;
    }

    /**
     * Returns a JSONObject from a JSONObject with the specified "key path".
     *
     * @param jsonObject The JSONObject to search
     * @param keyPath    The path of keys where the value will be pulled from separated by slashes (e.g. "results/0/value")
     * @return The JSONObject from the specified key path or null if the key does not exist or could not be converted.
     */
    public static JSONObject getJSONObject(JSONObject jsonObject, String keyPath) {
        return JSONUtils.toJSONObject(getString(jsonObject, keyPath));
    }

    /**
     * Returns a JSONArray from a JSONObject with the specified "key path".
     *
     * @param jsonObject The JSONObject to search
     * @param keyPath    The path of keys where the value will be pulled from separated by slashes (e.g. "results/0/value")
     * @return The JSONArray from the specified key path or null if the key does not exist or could not be converted.
     */
    public static JSONArray getJSONArray(JSONObject jsonObject, String keyPath) {
        return JSONUtils.toJSONArray(getString(jsonObject, keyPath));
    }

    /**
     * Returns a String from a JSONObject with the specified "key path".
     *
     * @param jsonObject The JSONObject to search
     * @param keyPath    The path of keys where the value will be pulled from separated by slashes (e.g. "results/0/value")
     * @return The String from the specified key path or null if the key does not exist.
     */
    public static String getString(JSONObject jsonObject, String keyPath) {
        String value = String.valueOf(getFromJSONObjectOrArray(jsonObject, keyPath));
        return value.equals("null") ? null : value;
    }

    /**
     * Returns an integer from a JSONObject with the specified "key path".
     *
     * @param jsonObject The JSONObject to search
     * @param keyPath    The path of keys where the value will be pulled from separated by slashes (e.g. "results/0/value")
     * @return The integer value from the specified key path.
     * @throws NumberFormatException if the data could not be converted
     */
    public static int getInt(JSONObject jsonObject, String keyPath) throws NumberFormatException {
        return Integer.parseInt(getString(jsonObject, keyPath));
    }

    /**
     * Returns a long from a JSONObject with the specified "key path".
     *
     * @param jsonObject The JSONObject to search
     * @param keyPath    The path of keys where the value will be pulled from separated by slashes (e.g. "results/0/value")
     * @return The long value from the specified key path.
     * @throws NumberFormatException if the data could not be converted
     */
    public static long getLong(JSONObject jsonObject, String keyPath) throws NumberFormatException {
        return Long.parseLong(getString(jsonObject, keyPath));
    }

    /**
     * Returns a float from a JSONObject with the specified "key path".
     *
     * @param jsonObject The JSONObject to search
     * @param keyPath    The path of keys where the value will be pulled from separated by slashes (e.g. "results/0/value")
     * @return The float value from the specified key path.
     * @throws NumberFormatException if the data could not be converted
     */
    public static float getFloat(JSONObject jsonObject, String keyPath) throws NumberFormatException {
        return Float.parseFloat(getString(jsonObject, keyPath));
    }

    /**
     * Returns a double from a JSONObject with the specified "key path".
     *
     * @param jsonObject The JSONObject to search
     * @param keyPath    The path of keys where the value will be pulled from separated by slashes (e.g. "results/0/value")
     * @return The float value from the specified key path.
     * @throws NumberFormatException if the data could not be converted
     */
    public static double getDouble(JSONObject jsonObject, String keyPath) throws NumberFormatException {
        return Double.parseDouble(getString(jsonObject, keyPath));
    }

    /******************************************************************************************************************************/

    /**
     * Returns a JSONObject from a JSONArray with the specified "key path".
     *
     * @param jsonArray The JSONArray to search
     * @param keyPath   The path of keys where the value will be pulled from separated by slashes (e.g. "results/0/value")
     * @return The JSONObject from the specified key path or null if the key does not exist or could not be converted.
     */
    public static JSONObject getJSONObject(JSONArray jsonArray, String keyPath) {
        return JSONUtils.toJSONObject(getString(jsonArray, keyPath));
    }

    /**
     * Returns a JSONArray from a JSONArray with the specified "key path".
     *
     * @param jsonArray The JSONArray to search
     * @param keyPath   The path of keys where the value will be pulled from separated by slashes (e.g. "results/0/value")
     * @return The JSONArray from the specified key path or null if the key does not exist or could not be converted.
     */
    public static JSONArray getJSONArray(JSONArray jsonArray, String keyPath) {
        return JSONUtils.toJSONArray(getString(jsonArray, keyPath));
    }

    /**
     * Returns a String from a JSONArray with the specified "key path".
     *
     * @param jsonArray The JSONArray to search
     * @param keyPath   The path of keys where the value will be pulled from separated by slashes (e.g. "results/0/value")
     * @return The String from the specified key path or null if the key does not exist.
     */
    public static String getString(JSONArray jsonArray, String keyPath) {
        String value = String.valueOf(getFromJSONObjectOrArray(jsonArray, keyPath));
        return value.equals("null") ? null : value;
    }

    /**
     * Returns an integer from a JSONArray with the specified "key path".
     *
     * @param jsonArray The JSONArray to search
     * @param keyPath   The path of keys where the value will be pulled from separated by slashes (e.g. "results/0/value")
     * @return The integer value from the specified key path.
     * @throws NumberFormatException if the data could not be converted
     */
    public static int getInt(JSONArray jsonArray, String keyPath) throws NumberFormatException {
        return Integer.parseInt(getString(jsonArray, keyPath));
    }

    /**
     * Returns a long from a JSONArray with the specified "key path".
     *
     * @param jsonArray The JSONArray to search
     * @param keyPath   The path of keys where the value will be pulled from separated by slashes (e.g. "results/0/value")
     * @return The long value from the specified key path.
     * @throws NumberFormatException if the data could not be converted
     */
    public static long getLong(JSONArray jsonArray, String keyPath) throws NumberFormatException {
        return Long.parseLong(getString(jsonArray, keyPath));
    }

    /**
     * Returns a float from a JSONArray with the specified "key path".
     *
     * @param jsonArray The JSONArray to search
     * @param keyPath   The path of keys where the value will be pulled from separated by slashes (e.g. "results/0/value")
     * @return The float value from the specified key path.
     * @throws NumberFormatException if the data could not be converted
     */
    public static float getFloat(JSONArray jsonArray, String keyPath) throws NumberFormatException {
        return Float.parseFloat(getString(jsonArray, keyPath));
    }

    /**
     * Returns a double from a JSONArray with the specified "key path".
     *
     * @param jsonArray The JSONArray to search
     * @param keyPath   The path of keys where the value will be pulled from separated by slashes (e.g. "results/0/value")
     * @return The float value from the specified key path.
     * @throws NumberFormatException if the data could not be converted
     */
    public static double getDouble(JSONArray jsonArray, String keyPath) throws NumberFormatException {
        return Double.parseDouble(getString(jsonArray, keyPath));
    }
}