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

package com.truebanana.data;

import com.truebanana.crypto.Crypto;
import com.truebanana.json.JSONUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;

public class SecureData {
    protected JSONObject data;
    protected String password;

    public SecureData(byte[] data, String password) {
        this.password = password;
        load(data);
    }

	/*
     * Data Loaders
	 * ************************************************************************
	 */

    public void load(byte[] data) {
        if (data != null && data.length > 0) {
            byte[] decryptedData = Crypto.decrypt(data, password, SecureData.Spec.getInstance());
            if (decryptedData != null) {
                load(new String(decryptedData));
            } else {
                load(new JSONObject());
            }
        } else {
            load(new JSONObject());
        }
    }

    public void load(String jsonString) {
        load(JSONUtils.toJSONObject(jsonString));
    }

    public void load(JSONObject json) {
        if (json != null) {
            data = json;
        } else {
            data = new JSONObject();
        }
    }

	/*
     * Data Comparison
	 * ************************************************************************
	 */

    public boolean equals(SecureData secureData) {
        try {
            return JSONCompare.compareJSON(this.toString(false), secureData.toString(false), JSONCompareMode.LENIENT).passed();
        } catch (JSONException e) {
            return false;
        }
    }

	/*
     * Data Exporters
	 * ************************************************************************
	 */

    @Override
    public String toString() {
        return toString(true);
    }

    public String toString(boolean encrypted) {
        String stringData = data.toString();
        if (encrypted) {
            stringData = new String(Crypto.encrypt(stringData.getBytes(), password, SecureData.Spec.getInstance()));
        }
        return stringData;
    }

    public byte[] toByteArray() {
        return toByteArray(true);
    }

    public byte[] toByteArray(boolean encrypted) {
        byte[] bytes = data.toString().getBytes();
        if (encrypted) {
            return Crypto.encrypt(bytes, password, SecureData.Spec.getInstance());
        }
        return bytes;
    }

	/*
     * Data Getters and Setters/Putters
	 * ************************************************************************
	 */

    public String getString(String key, String defaultValue) {
        try {
            return data.getString(key);
        } catch (JSONException e) {
            put(key, defaultValue);
        }
        return defaultValue;
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        try {
            return Boolean.parseBoolean(data.getString(key));
        } catch (JSONException e) {
            put(key, defaultValue);
        }
        return defaultValue;
    }

    public int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(data.getString(key));
        } catch (JSONException e) {
            put(key, defaultValue);
        } catch (NumberFormatException e) {
            put(key, defaultValue);
        }
        return defaultValue;
    }

    public long getLong(String key, long defaultValue) {
        try {
            return Long.parseLong(data.getString(key));
        } catch (JSONException e) {
            put(key, defaultValue);
        } catch (NumberFormatException e) {
            put(key, defaultValue);
        }
        return defaultValue;
    }

    public float getFloat(String key, float defaultValue) {
        try {
            return Float.parseFloat(data.getString(key));
        } catch (JSONException e) {
            put(key, defaultValue);
        } catch (NumberFormatException e) {
            put(key, defaultValue);
        }
        return defaultValue;
    }

    public double getDouble(String key, double defaultValue) {
        try {
            return Double.parseDouble(data.getString(key));
        } catch (JSONException e) {
            put(key, defaultValue);
        } catch (NumberFormatException e) {
            put(key, defaultValue);
        }
        return defaultValue;
    }

    public JSONObject getJSONObject(String key) {
        try {
            return data.getJSONObject(key);
        } catch (JSONException e) {
            return null;
        }
    }

    public JSONArray getJSONArray(String key) {
        try {
            return data.getJSONArray(key);
        } catch (JSONException e) {
            return null;
        }
    }

    public SecureData put(String key, String value) {
        try {
            data.put(key, value);
        } catch (JSONException e) {
            throw new IllegalArgumentException("Invalid value");
        }
        return this;
    }

    public SecureData put(String key, boolean value) {
        try {
            data.put(key, String.valueOf(value));
        } catch (JSONException e) {
            throw new IllegalArgumentException("Invalid value");
        }
        return this;
    }

    public SecureData put(String key, int value) {
        try {
            data.put(key, String.valueOf(value));
        } catch (JSONException e) {
            throw new IllegalArgumentException("Invalid value");
        }
        return this;
    }

    public SecureData put(String key, long value) {
        try {
            data.put(key, String.valueOf(value));
        } catch (JSONException e) {
            throw new IllegalArgumentException("Invalid value");
        }
        return this;
    }

    public SecureData put(String key, float value) {
        try {
            data.put(key, String.valueOf(value));
        } catch (JSONException e) {
            throw new IllegalArgumentException("Invalid value");
        }
        return this;
    }

    public SecureData put(String key, double value) {
        try {
            data.put(key, String.valueOf(value));
        } catch (JSONException e) {
            throw new IllegalArgumentException("Invalid value");
        }
        return this;
    }

    public SecureData put(String key, JSONObject json) {
        try {
            data.put(key, json);
        } catch (JSONException e) {
            throw new IllegalArgumentException("Invalid value");
        }
        return this;
    }

    public SecureData put(String key, JSONArray json) {
        try {
            data.put(key, json);
        } catch (JSONException e) {
            throw new IllegalArgumentException("Invalid value");
        }
        return this;
    }

    public SecureData remove(String key) {
        data.remove(key);
        return this;
    }

	/*
     * Utility Methods
	 * ************************************************************************
	 */

    public boolean isEmpty() {
        return data.length() == 0;
    }


	/*
     * Crypto Spec
	 * ************************************************************************
	 */

    public static class Spec extends Crypto.Spec {
        private static final Spec INSTANCE = new Spec();

        private Spec() {
            super();
            this.setKeyDerivationIterations(128);
        }

        public static Spec getInstance() {
            return INSTANCE;
        }
    }
}