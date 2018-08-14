package com.oneflaretech.kiakia.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.oneflaretech.kiakia.R;

/**
 * Created by finco on 9/26/17.
 */

public class CurvedLayout extends View {
    private Paint circlePaint;
    private int circleColor = Color.parseColor("#46B0ED");

    public CurvedLayout(Context context) {
        this(context, null, 0);
    }

    public CurvedLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CurvedLayout, 0, 0);
        try {
            circleColor = ta.getColor(R.styleable.CurvedLayout_circleBackgroundColor, 0);
        } finally {
            ta.recycle();
        }
    }

    public CurvedLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        initPaints();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);

        int size = Math.min(w, h);
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    }

    @Override
    protected void onDraw(Canvas canvas) {
        circlePaint = new Paint();
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setColor(circleColor);
        circlePaint.setAntiAlias(true);
        boolean isGottenDensity = false;
        switch (getResources().getDisplayMetrics().densityDpi) {
            case DisplayMetrics.DENSITY_LOW:
//                canvas.drawCircle(getWidth()/2, -(canvas.getHeight()/15), canvas.getWidth()-200, circlePaint);
                isGottenDensity = true;
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
//                canvas.drawCircle(getWidth()/2, -(canvas.getHeight()/15), canvas.getWidth()-200, circlePaint);
                isGottenDensity = true;
                break;
            case DisplayMetrics.DENSITY_HIGH:
//                canvas.drawCircle(getWidth()/2, -(canvas.getHeight()/35), canvas.getWidth()-200, circlePaint);
                isGottenDensity = true;
                break;
            case DisplayMetrics.DENSITY_XHIGH:
//                canvas.drawCircle(getWidth()/2, -(canvas.getHeight()/30), canvas.getWidth()-300, circlePaint);
                isGottenDensity = true;
                break;

            case DisplayMetrics.DENSITY_XXHIGH:
//                canvas.drawCircle(getWidth()/2, -(canvas.getHeight()/50), canvas.getWidth()-450, circlePaint);
                isGottenDensity = true;
                break;
            case DisplayMetrics.DENSITY_XXXHIGH:
//                canvas.drawCircle(getWidth()/2, -(canvas.getHeight()/50), canvas.getWidth()+450, circlePaint);
                isGottenDensity = true;
                break;
        }
            canvas.drawCircle(getWidth()/2, -(canvas.getHeight()/8), getWidth()+200, circlePaint);

    }
}
