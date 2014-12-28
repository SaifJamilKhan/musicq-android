package com.haystack.saifkhan.haystack.uI;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Created by saifkhan on 14-12-28.
 */
public class ResizedVideoView extends VideoView {
    private double mVideoWidth = 1280; //add in video dimens
    private double mVideoHeight = 720;

    public ResizedVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ResizedVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ResizedVideoView(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(this.getRootView() != null && this.getRootView().getHeight() > 0) {
            double heightRatio = mVideoHeight / this.getRootView().getHeight() * 1.0;
            double widthRatio = mVideoWidth / this.getRootView().getWidth() * 1.0;

            double biggerRatio = ((Math.abs(1 - heightRatio)) > (Math.abs(1 - widthRatio)) ? heightRatio : widthRatio);

            setMeasuredDimension((int) (mVideoWidth / biggerRatio), (int) (mVideoHeight/ biggerRatio));
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
