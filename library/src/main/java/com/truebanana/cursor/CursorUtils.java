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

package com.truebanana.cursor;

import android.database.Cursor;
import android.text.TextUtils;

/**
 * Includes utility methods for easier retrieval of field values from a {@link Cursor}.
 */
public class CursorUtils {

	/**
	 *
	 * Returns the value of the specified column from a {@link Cursor}
	 *
	 * @param cursor
	 *            The {@link Cursor} (record) from where the value will be retrieved
	 * @param columnName
	 *            The name of the column
	 * @return The value from the cursor
	 */
	public static String getString(Cursor cursor, String columnName, String defaultValue) {
		String value = defaultValue;
		if ((cursor != null) && !TextUtils.isEmpty(columnName)) {
			int index = cursor.getColumnIndex(columnName);
			if (index != -1) {
				value = cursor.getString(index);
			}
		}
		return value;
	}

	/**
	 *
	 * Returns the value of the specified column from a {@link Cursor}
	 *
	 * @param cursor
	 *            The {@link Cursor} (record) from where the value will be retrieved
	 * @param columnName
	 *            The name of the column
	 * @return The value from the cursor
	 */
	public static String getString(Cursor cursor, String columnName) {
		return getString(cursor, columnName, null);
	}

	/**
	 *
	 * Returns the value of the specified column from a {@link Cursor}
	 *
	 * @param cursor
	 *            The {@link Cursor} (record) from where the value will be retrieved
	 * @param columnName
	 *            The name of the column
	 * @return The value from the cursor
	 */
	public static byte[] getBlob(Cursor cursor, String columnName, byte[] defaultValue) {
		byte[] value = defaultValue;
		if ((cursor != null) && !TextUtils.isEmpty(columnName)) {
			int index = cursor.getColumnIndex(columnName);
			if (index != -1) {
				value = cursor.getBlob(index);
			}
		}
		return value;
	}

	/**
	 *
	 * Returns the value of the specified column from a {@link Cursor}
	 *
	 * @param cursor
	 *            The {@link Cursor} (record) from where the value will be retrieved
	 * @param columnName
	 *            The name of the column
	 * @return The value from the cursor
	 */
	public static byte[] getBlob(Cursor cursor, String columnName) {
		return getBlob(cursor, columnName, null);
	}

	/**
	 *
	 * Returns the value of the specified column from a {@link Cursor}
	 *
	 * @param cursor
	 *            The {@link Cursor} (record) from where the value will be retrieved
	 * @param columnName
	 *            The name of the column
	 * @return The value from the cursor
	 */
	public static int getInt(Cursor cursor, String columnName, int defaultValue) {
		int value = defaultValue;
		if ((cursor != null) && !TextUtils.isEmpty(columnName)) {
			int index = cursor.getColumnIndex(columnName);
			if (index != -1) {
				value = cursor.getInt(index);
			}
		}
		return value;
	}

	/**
	 *
	 * Returns the value of the specified column from a {@link Cursor}
	 *
	 * @param cursor
	 *            The {@link Cursor} (record) from where the value will be retrieved
	 * @param columnName
	 *            The name of the column
	 * @return The value from the cursor
	 */
	public static long getLong(Cursor cursor, String columnName, long defaultValue) {
		long value = defaultValue;
		if ((cursor != null) && !TextUtils.isEmpty(columnName)) {
			int index = cursor.getColumnIndex(columnName);
			if (index != -1) {
				value = cursor.getLong(index);
			}
		}
		return value;
	}

	/**
	 *
	 * Returns the value of the specified column from a {@link Cursor}
	 *
	 * @param cursor
	 *            The {@link Cursor} (record) from where the value will be retrieved
	 * @param columnName
	 *            The name of the column
	 * @return The value from the cursor
	 */
	public static float getFloat(Cursor cursor, String columnName, float defaultValue) {
		float value = defaultValue;
		if ((cursor != null) && !TextUtils.isEmpty(columnName)) {
			int index = cursor.getColumnIndex(columnName);
			if (index != -1) {
				value = cursor.getFloat(index);
			}
		}
		return value;
	}

	/**
	 *
	 * Returns the value of the specified column from a {@link Cursor}
	 *
	 * @param cursor
	 *            The {@link Cursor} (record) from where the value will be retrieved
	 * @param columnName
	 *            The name of the column
	 * @return The value from the cursor
	 */
	public static double getDouble(Cursor cursor, String columnName, double defaultValue) {
		double value = defaultValue;
		if ((cursor != null) && !TextUtils.isEmpty(columnName)) {
			int index = cursor.getColumnIndex(columnName);
			if (index != -1) {
				value = cursor.getDouble(index);
			}
		}
		return value;
	}
}
