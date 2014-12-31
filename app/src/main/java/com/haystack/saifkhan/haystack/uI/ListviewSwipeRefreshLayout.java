package com.haystack.saifkhan.haystack.uI;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by saifkhan on 14-12-26.
 */
public class ListviewSwipeRefreshLayout extends SwipeRefreshLayout {
    private AbsListView mListView;

    public ListviewSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setListView(AbsListView listView)
    {
        mListView = listView;
    }

    @Override
    public boolean canChildScrollUp() {
        if(mListView == null || mListView.getChildCount() == 0) return false;
        return mListView.getChildAt(0).getTop() != 0;
    }
}