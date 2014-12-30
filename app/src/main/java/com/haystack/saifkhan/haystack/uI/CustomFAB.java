package com.haystack.saifkhan.haystack.uI;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;

import com.haystack.saifkhan.haystack.R;

/**
 * Created by saifkhan on 14-12-28.
 */
public class CustomFAB extends ImageButton {
    private static final int RAD = 56;
    private Context ctx;
    private int bgColor;
    private int bgColorPressed;


    public CustomFAB(Context context) {
        super(context);
        this.ctx = context;
        init(null);
    }

    public CustomFAB(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.ctx = context;
        init(attrs);
    }

    public CustomFAB(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.ctx = context;
        init(attrs);
    }

    private Drawable createButton(int color) {
        OvalShape oShape = new OvalShape();
        ShapeDrawable sd = new ShapeDrawable(oShape);
        setWillNotDraw(false);
        sd.getPaint().setColor(color);
        return sd;
    }


    private void init(AttributeSet attrSet) {
        Resources.Theme theme = ctx.getTheme();
        TypedArray arr = theme.obtainStyledAttributes(attrSet, R.styleable.FAB, 0, 0);
        try {
            setBgColor(getResources().getColor(R.color.musicq_red));
            setBgColorPressed(getResources().getColor(R.color.musicq_deep_red));
            StateListDrawable sld = new StateListDrawable();

            sld.addState(new int[]{android.R.attr.state_pressed}, createButton(bgColorPressed));
            sld.addState(new int[]{}, createButton(bgColor));
            setBackground(sld);
        } catch (Throwable t) {
        } finally {
            arr.recycle();
        }

    }

    public void setBgColor(int color) {
        this.bgColor = color;
    }

    public void setBgColorPressed(int color) {
        this.bgColorPressed = color;
    }

    public void rotateForward(){
        this.setEnabled(false);
        Animation animation =new RotateAnimation(0, 45, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setFillAfter(true);
        animation.setFillEnabled(true);
        animation.setDuration(200);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                clearAnimation();
                CustomFAB.this.setRotation(45);
                CustomFAB.this.setEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        this.startAnimation(animation);
    }


    public void rotateBackward(){
        Animation animation =new RotateAnimation(0, -45, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setFillAfter(true);
        animation.setFillEnabled(true);
        animation.setDuration(200);
        CustomFAB.this.setEnabled(false);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                clearAnimation();
                CustomFAB.this.setRotation(0);
                CustomFAB.this.setEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        this.startAnimation(animation);
    }
}