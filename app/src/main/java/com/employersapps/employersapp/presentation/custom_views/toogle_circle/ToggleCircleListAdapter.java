package com.employersapps.employersapp.presentation.custom_views.toogle_circle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;

public class ToggleCircleListAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private final Context context;
    private final boolean[] toggleCircles;
    private final int toggleCircleRes;

    public ToggleCircleListAdapter(Context context, int toggleCircleRes, int count) {
        this.toggleCircles = new boolean[count];
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.toggleCircleRes = toggleCircleRes;
    }

    @Override
    public int getCount() {
        return toggleCircles.length;
    }

    @Override
    public Object getItem(int position) {
        return toggleCircles[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ToggleCircle toggleCircle = (ToggleCircle) inflater.inflate(
                toggleCircleRes,
                parent,
                false
        );

        toggleCircle.setOn(toggleCircles[position]);

        return toggleCircle;
    }

    public void setOnToggle(int position) {
        for(int i = 0; i < toggleCircles.length; i++) {
            toggleCircles[i] = position == i;
        }
        notifyDataSetChanged();
    }
}
