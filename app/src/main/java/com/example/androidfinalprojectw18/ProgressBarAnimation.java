package com.example.androidfinalprojectw18;

import android.content.Context;
import android.content.Intent;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProgressBarAnimation extends Animation {

    private Context ctx;
    private ProgressBar progressBar;
    private TextView percent_textView;
    private float from;
    private float to;

    public ProgressBarAnimation (Context ctx, ProgressBar progressBar, TextView percent_textView, float from, float to){

        this.ctx = ctx;
        this.progressBar = progressBar;
        this.percent_textView = percent_textView;
        this.from = from;
        this.to = to;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);

        float value = from + (to - from) * interpolatedTime;
        progressBar.setProgress((int)value);
        percent_textView.setText((int)value + " %");

        if (value == to){
            ctx.startActivity(new Intent(ctx, HomeActivity.class));
        }
    }
}
