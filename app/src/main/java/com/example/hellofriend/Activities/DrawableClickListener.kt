package com.example.hellofriend.Activities;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

public abstract class DrawableClickListener implements View.OnTouchListener {
    public enum DrawablePosition { LEFT, RIGHT }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v instanceof EditText) {
            EditText editText = (EditText) v;

            // Check for touch action
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // Get drawables (left, top, right, bottom)
                Rect leftBounds = null, rightBounds = null;
                if (editText.getCompoundDrawables()[0] != null) {
                    leftBounds = editText.getCompoundDrawables()[0].getBounds();
                }
                if (editText.getCompoundDrawables()[2] != null) {
                    rightBounds = editText.getCompoundDrawables()[2].getBounds();
                }

                // Check if touch is within left drawable bounds
                if (leftBounds != null && event.getX() <= (editText.getPaddingLeft() + leftBounds.width())) {
                    onClick(DrawablePosition.LEFT);
                    return true;
                }

                // Check if touch is within right drawable bounds
                if (rightBounds != null && event.getX() >= (editText.getWidth() - editText.getPaddingRight() - rightBounds.width())) {
                    onClick(DrawablePosition.RIGHT);
                    return true;
                }
            }
        }
        return false;
    }

    public abstract void onClick(DrawablePosition target);
}
