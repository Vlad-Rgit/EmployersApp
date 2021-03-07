package com.employersapps.employersapp.presentation.custom_views.toogle_circle;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.employersapps.employersapp.R;

public class ToggleCircle extends View {

    private float borderWidth;
    private int borderBrush;
    private int backgroundColorOn;
    private int backgroundColorOff;
    private boolean on;

    private Paint borderPaint;
    private Paint backgroundPaint;


    public ToggleCircle(Context context) {
        super(context);
    }

    public ToggleCircle(Context context, @Nullable AttributeSet attrs) {

        super(context, attrs);

        TypedArray typedAttrs = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ToggleCircle,
                0,
                0
        );

        initAttrs(typedAttrs);
    }

    public ToggleCircle(Context context,
                        @Nullable AttributeSet attrs,
                        int defStyleAttr) {

        super(context, attrs, defStyleAttr);

        TypedArray typedAttrs = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ToggleCircle,
                defStyleAttr,
                0
        );

        initAttrs(typedAttrs);
    }

    public ToggleCircle(Context context,
                        @Nullable AttributeSet attrs,
                        int defStyleAttr,
                        int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray typedAttrs = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ToggleCircle,
                defStyleAttr,
                defStyleRes
        );

        initAttrs(typedAttrs);
    }

    private void initAttrs(TypedArray typedAttrs) {
        try {
            borderWidth = typedAttrs.getDimension(
                    R.styleable.ToggleCircle_borderWidth, 0);
            borderBrush = typedAttrs.getColor(
                    R.styleable.ToggleCircle_borderColor, 0);
            backgroundColorOn = typedAttrs.getColor(
                    R.styleable.ToggleCircle_backgroundColorOn, 0);
            backgroundColorOff = typedAttrs.getColor(
                    R.styleable.ToggleCircle_backgroundColorOff, 0);
            on = typedAttrs.getBoolean(
                    R.styleable.ToggleCircle_on, false);

            borderPaint = new Paint();
            borderPaint.setStyle(Paint.Style.STROKE);
            borderPaint.setAntiAlias(true);
            updateBorderPaintColor();

            backgroundPaint = new Paint();
            backgroundPaint.setStyle(Paint.Style.FILL);
            borderPaint.setAntiAlias(true);
            updateBackgroundPaintColor();

        }
        finally {
            typedAttrs.recycle();
        }
    }

    private void updateBorderPaintColor() {
        borderPaint.setColor(borderBrush);
        borderPaint.setStrokeWidth(borderWidth);
    }

    private void updateBackgroundPaintColor() {
        if(on) {
            backgroundPaint.setColor(backgroundColorOn);
        }
        else {
            backgroundPaint.setColor(backgroundColorOff);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        if(borderWidth > 0) {
            canvas.drawCircle(
                    centerX,
                    centerY,
                    centerX - 2,
                    borderPaint
            );
        }

        canvas.drawCircle(
                centerX,
                centerY,
                centerX - borderWidth - 1,
                backgroundPaint
        );

    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
        updateBackgroundPaintColor();
        invalidate();
    }

    public float getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(float borderWidth) {
        this.borderWidth = borderWidth;
        invalidate();
    }

    public int getBorderBrush() {
        return borderBrush;
    }

    public void setBorderBrush(int borderBrush) {
        this.borderBrush = borderBrush;
        updateBorderPaintColor();
        invalidate();
    }

    public int getBackgroundColorOn() {
        return backgroundColorOn;
    }

    public void setBackgroundColorOn(int backgroundColorOn) {
        this.backgroundColorOn = backgroundColorOn;
        updateBackgroundPaintColor();
        invalidate();
    }

    public int getBackgroundColorOff() {
        return backgroundColorOff;
    }

    public void setBackgroundColorOff(int backgroundColorOff) {
        this.backgroundColorOff = backgroundColorOff;
        updateBackgroundPaintColor();
        invalidate();
    }
}
