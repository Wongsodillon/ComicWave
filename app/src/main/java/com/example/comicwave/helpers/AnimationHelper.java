package com.example.comicwave.helpers;

import android.view.View;

public class AnimationHelper {

    public static void fadeOut(View view) {
        if (view.getVisibility() == View.VISIBLE) { // Only animate if it's visible
            view.setAlpha(1); // Reset alpha to 1 for immediate visibility change
            view.setVisibility(View.GONE); // Immediately set to GONE
            view.animate()
                    .alpha(0) // Smoothly transition to 0 alpha
                    .setDuration(150)
                    .start();
        }
    }

    public static void fadeIn(View view) {
        view.setVisibility(View.VISIBLE);
        view.animate()
                .alpha(1)
                .setDuration(100);
    }

}
