package com.employersapps.employersapp.presentation.custom_views.toogle_circle;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.employersapps.employersapp.R;

public class ToggleCircleGroup extends LinearLayout {

    private int circlesCount;
    private int toggleCircleRes;
    private int circleOnPosition;

    public ToggleCircleGroup(Context context) {
        super(context);
    }

    public ToggleCircleGroup(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedAttrs = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ToggleCircleGroup,
                0,
                0
        );

        initAttrs(typedAttrs);
    }

    public ToggleCircleGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedAttrs = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ToggleCircleGroup,
                defStyleAttr,
                0
        );

        initAttrs(typedAttrs);
    }

    public ToggleCircleGroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray typedAttrs = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ToggleCircleGroup,
                defStyleAttr,
                defStyleRes
        );

        initAttrs(typedAttrs);
    }


    private void initAttrs(TypedArray typedAttrs) {
        try {


            toggleCircleRes = typedAttrs.getResourceId(
                    R.styleable.ToggleCircleGroup_toggleCircleRes, 0);

            circleOnPosition = typedAttrs.getInteger(
                    R.styleable.ToggleCircleGroup_circleOnPosition, -1);

            setCirclesCount(typedAttrs.getInteger(
                    R.styleable.ToggleCircleGroup_circlesCount, 0));
        }
        finally {
            typedAttrs.recycle();
        }
    }


    public int getCirclesCount() {
        return circlesCount;
    }

    public void setCirclesCount(int circlesCount) {

        if(circlesCount < 0) {
            return;
        }

        if(circlesCount == 0) {
            removeAllViews();
        }
        else if(this.circlesCount > circlesCount) {
            removeViews(0, this.circlesCount - circlesCount);
            if(circleOnPosition > 0) {
                setCircleOnPosition(0);
            }
        }
        else if(this.circlesCount < circlesCount) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            int toAdd = circlesCount - this.circlesCount;
            for(int i = 0; i < toAdd; i++) {
                ToggleCircle toggleCircle = (ToggleCircle) inflater.inflate(
                        toggleCircleRes,
                        this,
                        false);

                if(circleOnPosition == i) {
                    toggleCircle.setOn(true);
                }
                addView(toggleCircle);
            }
        }

        this.circlesCount = circlesCount;
    }

    public int getCircleOnPosition() {
        return circleOnPosition;
    }

    public void setCircleOnPosition(int circleOnPosition) {
        this.circleOnPosition = circleOnPosition;
        for(int i = 0; i < getChildCount(); i++) {
            ToggleCircle toggleCircle = (ToggleCircle) getChildAt(i);
            toggleCircle.setOn(i == circleOnPosition);
        }
    }
}
