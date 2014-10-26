package com.adp.activity.transitions.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.TimeInterpolator;
import android.annotation.TargetApi;
import android.graphics.Rect;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.util.FloatMath;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A {@link ForcedVisibilityTransition} that performs a CircularReveal Transition, optionally with
 * key frames for arbitrary pivoting. This may be useful when performing several conceal/reveal
 * animations in succession.
 */
@TargetApi(21)
public class NewCircularReveal extends ForcedVisibilityTransition {

    private static final String LOG_TAG = "CircularReveal";

    /** The key frames that specify start, end, and pivots in the transition's animation **/
    private List<KeyFrame> mKeyFrames;

    /**
     * @deprecated Use {@link #NewCircularReveal(int)} or
     * {@link #NewCircularReveal(int, float, float)} instead. The old implementation of this class
     * relied on specification of bounds, which was incorrectly mixed with the circular reveal
     * implementation. This class is only now responsible for running a circular reveal animation.
     */
    @Deprecated
    public NewCircularReveal(Rect startCenterOn) {
        super(ForcedVisibilityTransition.SHOW);
        Log.w(LOG_TAG, "This constructor - CircularReveal(Rect) - is deprecated.");
    }

    /**
     * Create a new Transition in the specified direction.
     * @param mode see {@link ForcedVisibilityTransition}. Should be {@link #SHOW} or {@link #HIDE}
     */
    public NewCircularReveal(int mode) {
        this(mode, 0f, 1f);
    }

    /**
     * Create a new instance in the specified direction with specific start and end values.
     * @param mode see {@link ForcedVisibilityTransition}. Should be {@link #SHOW} or {@link #HIDE}.
     * @param startRadiusPct the starting radius size (in percent of the full radius). defaults to 0
     * @param endRadiusPct the ending radius size (in percent of the full radius). defaults to 1
     */
    public NewCircularReveal(int mode, float startRadiusPct, float endRadiusPct) {
        super(mode);
        mKeyFrames = new LinkedList<KeyFrame>();
        mKeyFrames.add(new KeyFrame(0f, startRadiusPct, null));
        mKeyFrames.add(new KeyFrame(1f, endRadiusPct, null));
    }

    /**
     * Note: This API is still experimental and is subject to breakage or removal at any point.
     *
     * See {@link #addKeyFrame(float, float, TimeInterpolator)}
     */
    public NewCircularReveal addKeyFrame(float time, float amount) {
        addKeyFrame(time, amount, null);
        return this;
    }

    /**
     * Note: This API is still experimental and is subject to breakage or removal at any point.
     *
     * Add a key frame to this animation
     *
     * @param time the time at which to add this pivot; must be in the range [0,1]
     * @param amount the percentage of the full radius to animate to
     */
    public NewCircularReveal addKeyFrame(float time, float amount,
                                      TimeInterpolator interpolator) {
        if (time == 0f) {
            mKeyFrames.remove(0);
            mKeyFrames.add(new KeyFrame(time, amount, interpolator));
        } else if (time == 1f) {
            mKeyFrames.remove(mKeyFrames.size() - 1);
            mKeyFrames.add(new KeyFrame(time, amount, interpolator));
        } else {
            mKeyFrames.add(new KeyFrame(time, amount, interpolator));
        }
        Collections.sort(mKeyFrames);
        return this;
    }

    @Override
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues,
                                   TransitionValues endValues) {
        final View view = endValues.view;
        float maxRadius = calculateMaxRadius(view);
        int centerX = view.getMeasuredWidth() / 2;
        int centerY = view.getMeasuredHeight() / 2;

        // Keyframes are pre-sorted in chronological order
        if (!isRevealing()) {
            Collections.reverse(mKeyFrames);
        }

        // Build an animator set with all the specified keyframes
        List<Animator> animators = new ArrayList<Animator>(mKeyFrames.size() - 1);

        Iterator<KeyFrame> keyFramesIter = mKeyFrames.iterator();
        KeyFrame prevFrame = keyFramesIter.next();
        KeyFrame currFrame;
        while (keyFramesIter.hasNext()) {
            currFrame = keyFramesIter.next();

            Animator animator = ViewAnimationUtils.createCircularReveal(view, centerX, centerY,
                    prevFrame.mAmountPct * maxRadius, currFrame.mAmountPct * maxRadius);
            animator.setDuration(calculateDuration(prevFrame, currFrame, getDuration()));
            animator.setInterpolator(
                    isRevealing() ? currFrame.mInterpolator : prevFrame.mInterpolator);
            animators.add(animator);

            prevFrame = currFrame;
        }

        AnimatorSet set = new AnimatorSet();
        set.playSequentially(animators);
        set.setDuration(getDuration());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isRevealing()) {
                    // Hide the view after the animation to prevent a flicker
                    view.setVisibility(View.INVISIBLE);
                    view.setAlpha(0f);
                }
            }
        });
        return new NoPauseAnimatorWrapper(set);
    }

    private long calculateDuration(KeyFrame prev, KeyFrame current, long totalDuration) {
        if (isRevealing()) {
            return (long)
                    Math.abs(current.mTimePct * totalDuration - prev.mTimePct * totalDuration);
        } else {
            return (long) Math.abs(
                    (1 - current.mTimePct) * totalDuration - (1 - prev.mTimePct) * totalDuration);
        }
    }

    /**
     * Sets the interpolator on this transition's N-1th animation. Because this class may contain
     * multiple keyframes, it's only possible to modify the N-1th interpolator.
     */
    @Override
    public Transition setInterpolator(TimeInterpolator interpolator) {
        mKeyFrames.get(mKeyFrames.size() - 1).mInterpolator = interpolator;
        return this;
    }

    /**
     * This Transition may use multiple interpolations; return the N-1th animation.
     */
    @Override
    public TimeInterpolator getInterpolator() {
        return mKeyFrames.get(mKeyFrames.size() - 1).mInterpolator;
    }

    private static float calculateMaxRadius(View view) {
        float widthSquared = view.getWidth() * view.getWidth();
        float heightSquared = view.getHeight() * view.getHeight();
        return FloatMath.sqrt(widthSquared + heightSquared) / 2;
    }

    /**
     * Encapsulates a Key Frame -- holds information about the values at a given point in an
     * animation.
     */
    private static class KeyFrame implements Comparable<KeyFrame> {

        public Float mTimePct;
        public Float mAmountPct;
        public TimeInterpolator mInterpolator;

        public KeyFrame(float timePct, float amountPct, TimeInterpolator interpolator) {
            if (timePct < 0f || timePct > 1f) {
                throw new IllegalArgumentException("Time value must be between [0,1]");
            } else if (amountPct < 0f || amountPct > 1f) {
                throw new IllegalArgumentException("Value percentage must be between [0,1]");
            }
            mTimePct = timePct;
            mAmountPct = amountPct;
            mInterpolator = interpolator;
        }

        @Override
        public int hashCode() {
            return mTimePct.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof KeyFrame && mTimePct.equals(((KeyFrame) o).mTimePct);
        }

        @Override
        public int compareTo(KeyFrame another) {
            return mTimePct.compareTo(another.mTimePct);
        }
    }
}