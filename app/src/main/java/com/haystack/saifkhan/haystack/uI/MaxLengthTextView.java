package com.haystack.saifkhan.haystack.uI;

import android.content.Context;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by saifkhan on 15-01-01.
 */
public class MaxLengthTextView extends EditText {


    public MaxLengthTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MaxLengthTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setMaxLength(int maxLength) {
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);
        this.setFilters(fArray);
    }
}
