package com.example.hellofriend.Activities

import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.EditText
import com.example.hellofriend.Activities.DrawableClickListener.DrawablePosition

abstract class DrawableClickListener : OnTouchListener {
    enum class DrawablePosition {
        LEFT, RIGHT
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (v is EditText) {
            val editText = v

            // Check for touch action
            if (event.action == MotionEvent.ACTION_UP) {
                // Get drawables (left, top, right, bottom)
                var leftBounds: Rect? = null
                var rightBounds: Rect? = null
                if (editText.getCompoundDrawables()[0] != null) {
                    leftBounds = editText.getCompoundDrawables()[0].getBounds()
                }
                if (editText.getCompoundDrawables()[2] != null) {
                    rightBounds = editText.getCompoundDrawables()[2].getBounds()
                }

                // Check if touch is within left drawable bounds
                if (leftBounds != null && event.x <= (editText.getPaddingLeft() + leftBounds.width())) {
                    onClick(DrawablePosition.LEFT)
                    return true
                }

                // Check if touch is within right drawable bounds
                if (rightBounds != null && event.x >= (editText.width - editText.getPaddingRight() - rightBounds.width())) {
                    onClick(DrawablePosition.RIGHT)
                    return true
                }
            }
        }
        return false
    }

    abstract fun onClick(target: DrawablePosition?)
}
