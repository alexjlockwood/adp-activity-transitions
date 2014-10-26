package com.adp.activity.transitions.util;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.graphics.Path;
import android.graphics.Rect;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.view.View;
import android.view.ViewGroup;

/**
 * A {@link Transition} used for Activity Transitions that acts much like {@link android.transition.ChangeBounds}, but
 * uses x + y and scale instead of left/top/right/bottom.
 */
@TargetApi(21)
public class Scale extends Transition {

    public static final long ANIMATION_DURATION_MS = 400;

    private static final String PROPNAME_BOUNDS = "android:silhouetteExpando:bounds";
    private static final String PROPNAME_WINDOW_X = "android:silhouetteExpando:windowX";
    private static final String PROPNAME_WINDOW_Y = "android:silhouetteExpando:windowY";

    private static final String[] sTransitionProperties = {
            PROPNAME_BOUNDS,
            PROPNAME_WINDOW_X,
            PROPNAME_WINDOW_Y
    };

    private final boolean mIsEntering;
    private int[] mTempLocation = new int[2];
    private int mOriginatingViewInset;
    private boolean mMaintainAspectRatio;

    /**
     * @param isEntering if the transition
     */
    public Scale(boolean isEntering) {
        this.mIsEntering = isEntering;
    }

    /**
     * Allows the caller to specify that the originating view should be offset by some amount. This
     * may be useful when transitioning from a view with an inset background.
     */
    public Scale setOriginatingViewInset(int inset) {
        this.mOriginatingViewInset = inset;
        return this;
    }

    /**
     * Set to force this transition to maintain its end values' aspect ratio. May be useful when
     * transitioning from a shared element that has a desirable position, but does not share an
     * aspect ratio with the end value.
     */
    public Scale forceMaintainAspectRatio(boolean maintainAspectRatio) {
        this.mMaintainAspectRatio = maintainAspectRatio;
        return this;
    }

    @Override
    public String[] getTransitionProperties() {
        return sTransitionProperties;
    }

    private void captureValues(TransitionValues values) {
        View view = values.view;
        Rect outerBounds = new Rect(view.getLeft(), view.getTop(), view.getRight(),
                view.getBottom());
        values.values.put(PROPNAME_BOUNDS, outerBounds);
        values.view.getLocationInWindow(mTempLocation);
        values.values.put(PROPNAME_WINDOW_X, mTempLocation[0]);
        values.values.put(PROPNAME_WINDOW_Y, mTempLocation[1]);
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override
    public void captureStartValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override
    public Animator createAnimator(final ViewGroup sceneRoot, TransitionValues startValues,
                                   TransitionValues endValues) {
        if (startValues != null && endValues != null) {
            return getHeroAnimator(sceneRoot, startValues, endValues);
        }
        return null;
    }

    protected Animator getHeroAnimator(final ViewGroup sceneRoot, TransitionValues startValues,
                                       final TransitionValues endValues) {

        // Get starting & ending bounds
        Rect startBounds = (Rect) startValues.values.get(PROPNAME_BOUNDS);
        Rect endBounds = (Rect) endValues.values.get(PROPNAME_BOUNDS);

        // Apply the inset to the view from which the transition originated from
        if (mIsEntering) {
            startBounds.inset(mOriginatingViewInset, mOriginatingViewInset);
            if (mMaintainAspectRatio) {
                startBounds = matchAspectRatio(endBounds.width() / (float) endBounds.height(),
                        startBounds);
            }
        } else {
            endBounds.inset(mOriginatingViewInset, mOriginatingViewInset);
            if (mMaintainAspectRatio) {
                endBounds = matchAspectRatio(startBounds.width() / (float) startBounds.height(),
                        endBounds);
            }
        }

        final View view = endValues.view;

        // Path animation along top + left
        Path path = getPathMotion()
                .getPath(startBounds.left, startBounds.top, endBounds.left, endBounds.top);
        ObjectAnimator topLeftAnimator = ObjectAnimator.ofFloat(view, View.X, View.Y, path);

        // Use pivot x & y = 0 as we're animating from the top / left of the view
        view.setPivotX(0);
        view.setPivotY(0);

        // Set up scale start and end values
        float scaleXStart = startBounds.width() / (float) endBounds.width();
        float scaleYStart = startBounds.height() / (float) endBounds.height();
        float scaleXEnd = 1f;
        float scaleYEnd = 1f;

        // When animating in the reverse direction, scale must compensate for the view inset
        if (!mIsEntering && mOriginatingViewInset != 0) {
            scaleXEnd =
                    (endBounds.width() - mOriginatingViewInset * 2) / (float) endBounds.width();
            scaleYEnd =
                    (endBounds.height() - mOriginatingViewInset * 2) / (float) endBounds.height();
        }

        // Setup initial positioning for the enter animation
        if (mIsEntering) {
            view.setX(startBounds.left);
            view.setY(startBounds.top);
            view.setScaleX(scaleXStart);
            view.setScaleY(scaleYStart);
        }

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", scaleXStart, scaleXEnd);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", scaleYStart, scaleYEnd);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, topLeftAnimator);
        animatorSet.setDuration(ANIMATION_DURATION_MS);
        animatorSet.setInterpolator(getInterpolator());
        return animatorSet;
    }

    /**
     * @param aspectRatio the desired aspect ratio
     * @param bounds rect to operate on. will not be mutated.
     * @return a new Rect with an updated aspect ratio
     */
    protected static Rect matchAspectRatio(float aspectRatio, Rect bounds) {
        Rect adjustedBounds = new Rect(bounds);
        float currentAspectRatio = bounds.width() / (float) bounds.height();
        if (currentAspectRatio != aspectRatio) {
            // Update the smaller dimension to match the aspect ratio
            if (bounds.width() > bounds.height()) {
                int widthDiff = (int) ((bounds.width() - (bounds.height() * aspectRatio)) / 2f);
                adjustedBounds
                        .set(bounds.left + widthDiff, bounds.top, bounds.right - widthDiff,
                                bounds.bottom);
            } else {
                int heightDiff = (int) ((bounds.height() - (int) (bounds.width() * aspectRatio))
                        / 2f);
                adjustedBounds
                        .set(bounds.left, bounds.top + heightDiff, bounds.right,
                                bounds.bottom - heightDiff);
            }
        }
        return adjustedBounds;
    }
}
