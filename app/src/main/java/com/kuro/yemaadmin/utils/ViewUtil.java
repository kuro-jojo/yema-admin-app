package com.kuro.yemaadmin.utils;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class ViewUtil {

    public static void hideSoftKeyboard(MotionEvent event, View focusedView, Context context) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // Check if the touch event was outside the EditText
            if (isTouchOutsideView(event, focusedView)) {
                // Hide soft keyboard
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);

                // Clear focus from the EditText
                focusedView.clearFocus();
            }
        }
    }

    public static View.OnTouchListener hideSoftKeyboardOnTouchEvent(View focusedView, Context context) {
        return (view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                // Check if the touch event was outside the EditText
                if (isTouchOutsideView(motionEvent, focusedView)) {
                    // Hide soft keyboard
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);

                    // Clear focus from the EditText
                    focusedView.clearFocus();
                }
            }
            view.performClick();
            return false;
        };
    }

    public static View.OnTouchListener hideSoftKeyboardOnTouchEvent(View[] focusedViews, Context context) {
        return (view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                for (View focusedView : focusedViews) {
                    // Check if the touch event was outside the EditText
                    if (isTouchOutsideView(motionEvent, focusedView)) {
                        // Hide soft keyboard
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);

                        // Clear focus from the EditText
                        focusedView.clearFocus();
                    }
                }
            }
            view.performClick();
            return false;
        };
    }

    public static boolean isTouchOutsideView(MotionEvent event, View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        float x = event.getRawX();
        float y = event.getRawY();
        return !(x > location[0]) || !(x < (location[0] + view.getWidth())) || !(y > location[1]) || !(y < (location[1] + view.getHeight()));
    }
}
