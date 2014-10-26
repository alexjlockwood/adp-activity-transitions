package com.adp.activity.transitions.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Path;
import android.graphics.Rect;
import android.transition.TransitionValues;
import android.transition.Visibility;
import android.util.FloatMath;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;

public class CircularReveal extends Visibility {
    private static final String VALUES_LOCATION_ON_SCREEN = "values_location_on_screen";

    private final Rect mStartBounds = new Rect();

    public CircularReveal(View view) {
       setStartView(view);
    }

    public void setStartView(View view) {
        final int[] loc = new int[2];
        view.getLocationInWindow(loc);
        mStartBounds.set(loc[0], loc[1], loc[0] + view.getWidth(), loc[1] + view.getHeight());
    }

    @Override
    public void captureStartValues(TransitionValues transitionValues) {
        super.captureStartValues(transitionValues);
        captureValues(transitionValues);
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        super.captureEndValues(transitionValues);
        captureValues(transitionValues);
    }

    private void captureValues(TransitionValues transitionValues) {
        final int[] location = new int[2];
        transitionValues.view.getLocationOnScreen(location);
        transitionValues.values.put(VALUES_LOCATION_ON_SCREEN, location);
    }

    private float getCenterX(View view) {
        return mStartBounds.left + mStartBounds.width() / 2 - view.getWidth() / 2;
    }

    private float getCenterY(View view) {
        return mStartBounds.top + mStartBounds.height() / 2 - view.getHeight() / 2;
    }

    @Override
    public Animator onAppear(ViewGroup sceneRoot, final View view, TransitionValues startValues, TransitionValues endValues) {
        return createAnimation(view, getCenterX(view), getCenterY(view), view.getTranslationX(), view.getTranslationY(), true);
    }

    @Override
    public Animator onDisappear(ViewGroup sceneRoot, final View view, TransitionValues startValues, TransitionValues endValues) {
        return createAnimation(view, view.getTranslationX(), view.getTranslationY(), getCenterX(view), getCenterY(view), false);
    }

    private Animator createAnimation(final View view, float startX, float startY, float endX, float endY, final boolean isExpanding) {
        view.setTranslationX(startX);
        view.setTranslationY(startY);
        if (startX == endX && startY == endY) {
            return null;
        }

        Path path = getPathMotion().getPath(startX, startY, endX, endY);
        Animator pathAnimation = ObjectAnimator.ofFloat(view, View.TRANSLATION_X, View.TRANSLATION_Y, path);
        Animator circularRevealAnimator = revealAnimator(view, view.getMeasuredWidth(), view.getMeasuredHeight(), isExpanding);
        circularRevealAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setAlpha(isExpanding ? 1f : 0f);
            }
        });

        final AnimatorSet anim = new AnimatorSet();
        anim.playTogether(pathAnimation, circularRevealAnimator);
        anim.setInterpolator(getInterpolator());
        return anim;
    }

    private Animator revealAnimator(View view, int width, int height, boolean isExpanding) {
        float fullRadius = FloatMath.sqrt(width * width / 4f + height * height / 4f);
        float endRadius = isExpanding ? fullRadius : 0f;
        float startRadius = isExpanding ? 0f : fullRadius;
        Animator animator = ViewAnimationUtils.createCircularReveal(view, width / 2, height / 2, startRadius, endRadius);

        // Workaround b/17541275 by wrapping this animator in a NoPauseAnimatorWrapper.
        return new NoPauseAnimatorWrapper(animator);
    }
}