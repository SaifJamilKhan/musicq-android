package com.haystack.saifkhan.haystack.Utils;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

/**
 * Created by saifkhan on 15-01-02.
 */
public class ViewUtils {

    public static void hideKeyboardFromTextview(TextView textView, Context context){
        InputMethodManager imm = (InputMethodManager)context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
    }
}
