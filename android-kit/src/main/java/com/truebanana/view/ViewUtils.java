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

package com.truebanana.view;

import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Includes {@link View}-related utility methods.
 */
public class ViewUtils {

    /**
     * Convenience method to get the integer value of an {@link EditText}
     *
     * @param editText The EditText
     * @return The integer value of the EditText or 0 if parsing fails
     */
    public static int getInt(EditText editText) {
        int value = 0;
        try {
            value = Integer.parseInt(editText.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return value;
    }

    /**
     * Convenience method to get the long value of an {@link EditText}
     *
     * @param editText The EditText
     * @return The long value of the EditText or 0 if parsing fails
     */
    public static long getLong(EditText editText) {
        long value = 0L;
        try {
            value = Long.parseLong(editText.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return value;
    }

    /**
     * Convenience method to get the double value of an {@link EditText}
     *
     * @param editText The EditText
     * @return The double value of the EditText or 0 if parsing fails
     */
    public static double getDouble(EditText editText) {
        double value = 0D;
        try {
            value = Double.parseDouble(editText.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return value;
    }

    /**
     * Convenience method to get the float value of an {@link EditText}
     *
     * @param editText The EditText
     * @return The float value of the EditText or 0 if parsing fails
     */
    public static float getFloat(EditText editText) {
        float value = 0F;
        try {
            value = Float.parseFloat(editText.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return value;
    }

    /**
     * Convenience method to get the string value of an {@link EditText}
     *
     * @param editText The EditText
     * @return The String value of the EditText
     */
    public static String getString(EditText editText) {
        return editText.getText().toString();
    }

    /**
     * Convenience method to get the length of the text in an {@link EditText}
     *
     * @param editText The EditText
     * @return The length of the text
     */
    public static int getLength(EditText editText) {
        return editText.getText().toString().length();
    }

    /**
     * Convenience method to return the text of the selected {@link Spinner} item
     *
     * @param spinner The spinner
     * @return The text of the selected item
     */
    public static String getSelectedItem(Spinner spinner) {
        return spinner.getSelectedItem().toString();
    }

    /**
     * Convenience method to set the selected {@link Spinner} item by text
     *
     * @param spinner The spinner
     * @param item    The text of the item to select
     */
    public static void setSelectedItem(Spinner spinner, String item) {
        Adapter vAdapter = spinner.getAdapter();
        for (int i = 0; i < vAdapter.getCount(); i++) {
            if (item.equals(vAdapter.getItem(i).toString())) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    /**
     * Returns a {@link List} of {@link View}s selected by its tag
     *
     * @param parentView The parent view
     * @param tag
     * @return The {@link View}s
     */
    public static List<View> getViewsByTag(ViewGroup parentView, Object tag) {
        List<View> views = new ArrayList<>();
        final int childCount = parentView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parentView.getChildAt(i);
            if (child instanceof ViewGroup) {
                views.addAll(getViewsByTag((ViewGroup) child, tag));
            }

            final Object tagObj = child.getTag();
            if (tagObj != null && tagObj.equals(tag)) {
                views.add(child);
            }
        }
        return views;
    }

    /**
     * Convenience method to change the visibility of {@link View}s
     *
     * @param visibility The visibility to be set, one of {@link View#VISIBLE}, {@link View#INVISIBLE} or {@link View#GONE}
     * @param views      The views
     */
    public static void setVisibility(int visibility, View... views) {
        setVisibility(visibility, Arrays.asList(views));
    }

    /**
     * Convenience method to change the visibility of {@link View}s
     *
     * @param visibility The visibility to be set, one of {@link View#VISIBLE}, {@link View#INVISIBLE} or {@link View#GONE}
     * @param views      The views
     */
    public static void setVisibility(int visibility, List<View> views) {
        for (View view : views) {
            view.setVisibility(visibility);
        }
    }

    /**
     * Convenience method to set the visibility of {@link View}s to {@link View#VISIBLE}
     *
     * @param views The views
     */
    public static void setVisible(View... views) {
        setVisibility(View.VISIBLE, views);
    }

    /**
     * Convenience method to set the visibility of {@link View}s to {@link View#VISIBLE}
     *
     * @param views The views
     */
    public static void setVisible(List<View> views) {
        setVisibility(View.VISIBLE, views);
    }

    /**
     * Convenience method to set the visibility of {@link View}s to {@link View#INVISIBLE}
     *
     * @param views The views
     */
    public static void setInvisible(View... views) {
        setVisibility(View.INVISIBLE, views);
    }

    /**
     * Convenience method to set the visibility of {@link View}s to {@link View#INVISIBLE}
     *
     * @param views The views
     */
    public static void setInvisible(List<View> views) {
        setVisibility(View.INVISIBLE, views);
    }

    /**
     * Convenience method to set the visibility of {@link View}s to {@link View#GONE}
     *
     * @param views The views
     */
    public static void setGone(View... views) {
        setVisibility(View.GONE, views);
    }

    /**
     * Convenience method to set the visibility of {@link View}s to {@link View#GONE}
     *
     * @param views The views
     */
    public static void setGone(List<View> views) {
        setVisibility(View.GONE, views);
    }


    /**
     * Convenience method to set the typeface of {@link View}s
     *
     * @param typeface The typeface
     * @param views    The views
     */
    public static void setTypeface(Typeface typeface, View... views) {
        setTypeface(typeface, Arrays.asList(views));
    }

    /**
     * Convenience method to set the typeface of {@link View}s
     *
     * @param typeface The typeface
     * @param views    The views
     */
    public static void setTypeface(Typeface typeface, List<View> views) {
        for (View view : views) {
            if (view instanceof TextView) {
                ((TextView) view).setTypeface(typeface);
            } else if (view instanceof EditText) {
                ((EditText) view).setTypeface(typeface);
            } else if (view instanceof Button) {
                ((Button) view).setTypeface(typeface);
            } else if (view instanceof CheckBox) {
                ((CheckBox) view).setTypeface(typeface);
            } else if (view instanceof RadioButton) {
                ((RadioButton) view).setTypeface(typeface);
            }
        }
    }
}
