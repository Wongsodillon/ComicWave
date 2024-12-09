package com.example.comicwave.helpers;

import android.view.View;

public class AnimationHelper {

    public static void fadeOut(View view) {
        view.animate()
                .alpha(0)
                .setDuration(75)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        view.setVisibility(View.GONE);
                    }
                });
    }

    public static void fadeIn(View view) {
        view.setVisibility(View.VISIBLE);
        view.animate()
                .alpha(1)
                .setDuration(100);
    }

}
