package com.adp.activity.transitions;

import android.animation.Animator;
import android.animation.TimeInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * A wrapper that does not forward pause/resume calls to the wrapped animator. This class is
 * used to avoid b/17541275 when an animator that does not support pause is used in a window
 * transition (i.e. ViewAnimationUtils.createCircularReveal()).
 * <p/>
 * We must always return a reference to ourself and not the wrapped animator. So we must
 * keep track of listeners, register ourself as a listener to the wrapped animator and dispatch
 * events.
 * <p/>
 * Note that since we don't support pause, there's no point in keeping track of pause listeners.
 * Pause listeners registered with this animator will never be called.
 */
public class IgnorePauseAnimator extends Animator implements Animator.AnimatorListener {

    private Animator mAnimator;
    private ArrayList<AnimatorListener> mListeners;

    public IgnorePauseAnimator(Animator animator) {
        mAnimator = animator;
        mAnimator.addListener(this);
    }

    @Override
    public long getDuration() {
        return mAnimator.getDuration();
    }

    @Override
    public Animator setDuration(long millis) {
        mAnimator.setDuration(millis);
        return this;
    }

    @Override
    public TimeInterpolator getInterpolator() {
        return mAnimator.getInterpolator();
    }

    @Override
    public void setInterpolator(TimeInterpolator interpolator) {
        mAnimator.setInterpolator(interpolator);
    }

    @Override
    public long getStartDelay() {
        return mAnimator.getStartDelay();
    }

    @Override
    public void setStartDelay(long millis) {
        mAnimator.setStartDelay(millis);
    }

    @Override
    public boolean isRunning() {
        return mAnimator.isRunning();
    }

    @Override
    public ArrayList<Animator.AnimatorListener> getListeners() {
        return mListeners;
    }

    @Override
    public void addListener(Animator.AnimatorListener listener) {
        if (mListeners == null) {
            mListeners = new ArrayList<>();
        }
        mListeners.add(listener);
    }

    @Override
    public void removeListener(Animator.AnimatorListener listener) {
        if (mListeners != null) {
            mListeners.remove(listener);
            if (mListeners.isEmpty()) {
                mListeners = null;
            }
        }
    }

    @Override
    public void removeAllListeners() {
        if (mListeners != null) {
            mListeners.clear();
            mListeners = null;
        }
    }

    @Override
    public void addPauseListener(Animator.AnimatorPauseListener listener) {
        // Do nothing.
    }

    @Override
    public void removePauseListener(Animator.AnimatorPauseListener listener) {
        // Do nothing.
    }

    @Override
    public void pause() {
        // Do nothing.
    }

    @Override
    public void resume() {
        // Do nothing.
    }

    @Override
    public void start() {
        mAnimator.start();
    }

    @Override
    public void end() {
        mAnimator.end();
    }

    @Override
    public void cancel() {
        mAnimator.cancel();
    }

    @Override
    public boolean isPaused() {
        return mAnimator.isPaused();
    }

    @Override
    public boolean isStarted() {
        return mAnimator.isStarted();
    }

    @Override
    public void setTarget(Object target) {
        mAnimator.setTarget(target);
    }

    @Override
    public void setupStartValues() {
        mAnimator.setupStartValues();
    }

    @Override
    public void setupEndValues() {
        mAnimator.setupEndValues();
    }

    @Override
    public IgnorePauseAnimator clone() {
        IgnorePauseAnimator anim = (IgnorePauseAnimator) super.clone();
        if (mListeners != null) {
            List<AnimatorListener> oldListeners = mListeners;
            anim.mListeners = new ArrayList<>();
            for (AnimatorListener oldListener : oldListeners) {
                anim.mListeners.add(oldListener);
            }
        }
        return anim;
    }

    @Override
    public void onAnimationCancel(Animator animator) {
        @SuppressWarnings("unchecked")
        List<AnimatorListener> listeners = (ArrayList<AnimatorListener>) mListeners.clone();
        for (AnimatorListener tmpListener : listeners) {
            tmpListener.onAnimationCancel(this);
        }
    }

    @Override
    public void onAnimationEnd(Animator animator) {
        @SuppressWarnings("unchecked")
        List<AnimatorListener> listeners = (ArrayList<AnimatorListener>) mListeners.clone();
        for (AnimatorListener tmpListener : listeners) {
            tmpListener.onAnimationEnd(this);
        }
    }

    @Override
    public void onAnimationRepeat(Animator animator) {
        @SuppressWarnings("unchecked")
        List<AnimatorListener> listeners = (ArrayList<AnimatorListener>) mListeners.clone();
        for (AnimatorListener tmpListener : listeners) {
            tmpListener.onAnimationRepeat(this);
        }
    }

    @Override
    public void onAnimationStart(Animator animator) {
        @SuppressWarnings("unchecked")
        List<AnimatorListener> listeners = (ArrayList<AnimatorListener>) mListeners.clone();
        for (AnimatorListener tmpListener : listeners) {
            tmpListener.onAnimationStart(this);
        }
    }
}
