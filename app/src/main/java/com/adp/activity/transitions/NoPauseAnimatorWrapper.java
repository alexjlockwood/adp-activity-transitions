package com.adp.activity.transitions;

import android.animation.Animator;
import android.animation.TimeInterpolator;

import java.util.ArrayList;

/**
 * A wrapper that does not forward pause/resume calls to the wrapped animator. This class is
 * used to avoid b/17541275 when an animator that does not support pause is used in a window
 * transition (i.e. ViewAnimationUtils.createCircularReveal()).
 *
 * We must always return a reference to ourself and not the wrapped animator. So we must
 * keep track of listeners, register ourself as a listener to the wrapped animator and dispatch
 * events.
 *
 * Note that since we don't support pause, there's no point in keeping track of pause listeners.
 * Pause listeners registered with this animator will never be called.
 */
public class NoPauseAnimatorWrapper extends Animator implements Animator.AnimatorListener {

    private Animator mAnimator;

    ArrayList<AnimatorListener> mListeners = null;

    public NoPauseAnimatorWrapper(Animator animator) {
        mAnimator = animator;
        mAnimator.addListener(this);
    }

    @Override
    public long getDuration() {
        return mAnimator.getDuration();
    }

    @Override
    public long getStartDelay() {
        return mAnimator.getStartDelay();
    }

    @Override
    public boolean isRunning() {
        return mAnimator.isRunning();
    }

    @Override
    public Animator setDuration(long arg0) {
        mAnimator.setDuration(arg0);
        return this;
    }

    @Override
    public void setInterpolator(TimeInterpolator arg0) {
        mAnimator.setInterpolator(arg0);
    }

    @Override
    public void setStartDelay(long arg0) {
        mAnimator.setStartDelay(arg0);
    }

    @Override
    public void addListener(Animator.AnimatorListener listener) {
        if (mListeners == null) {
            mListeners = new ArrayList<AnimatorListener>();
        }
        mListeners.add(listener);
    }

    @Override
    public void addPauseListener(Animator.AnimatorPauseListener listener) {
        // Do nothing.
    }

    @Override
    public NoPauseAnimatorWrapper clone() {
        final NoPauseAnimatorWrapper anim = (NoPauseAnimatorWrapper) super.clone();
        if (mListeners != null) {
            ArrayList<AnimatorListener> oldListeners = mListeners;
            anim.mListeners = new ArrayList<AnimatorListener>();
            int numListeners = oldListeners.size();
            for (int i = 0; i < numListeners; ++i) {
                anim.mListeners.add(oldListeners.get(i));
            }
        }
        return anim;
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
    public void pause() {
        // Do nothing.
    }

    @Override
    public void resume() {
        // Do nothing.
    }

    @Override
    public TimeInterpolator getInterpolator() {
        return mAnimator.getInterpolator();
    }

    @Override
    public ArrayList<Animator.AnimatorListener> getListeners() {
        return mListeners;
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
    public void removeAllListeners() {
        if (mListeners != null) {
            mListeners.clear();
            mListeners = null;
        }
    }

    @Override
    public void removeListener(Animator.AnimatorListener listener) {
        if (mListeners == null) {
            return;
        }
        mListeners.remove(listener);
        if (mListeners.size() == 0) {
            mListeners = null;
        }
    }

    @Override
    public void removePauseListener(Animator.AnimatorPauseListener listener) {
        // Do nothing.
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



    // AnimatorListener callbacks.

    @Override
    public void onAnimationCancel(Animator arg0) {
        @SuppressWarnings("unchecked")
        ArrayList<AnimatorListener> tmpListeners =
                (ArrayList<AnimatorListener>) mListeners.clone();
        int numListeners = tmpListeners.size();
        for (int i = 0; i < numListeners; ++i) {
            tmpListeners.get(i).onAnimationCancel(this);
        }
    }

    @Override
    public void onAnimationEnd(Animator arg0) {
        @SuppressWarnings("unchecked")
        ArrayList<AnimatorListener> tmpListeners =
                (ArrayList<AnimatorListener>) mListeners.clone();
        int numListeners = tmpListeners.size();
        for (int i = 0; i < numListeners; ++i) {
            tmpListeners.get(i).onAnimationEnd(this);
        }
    }

    @Override
    public void onAnimationRepeat(Animator arg0) {
        @SuppressWarnings("unchecked")
        ArrayList<AnimatorListener> tmpListeners =
                (ArrayList<AnimatorListener>) mListeners.clone();
        int numListeners = tmpListeners.size();
        for (int i = 0; i < numListeners; ++i) {
            tmpListeners.get(i).onAnimationRepeat(this);
        }
    }

    @Override
    public void onAnimationStart(Animator arg0) {
        @SuppressWarnings("unchecked")
        ArrayList<AnimatorListener> tmpListeners =
                (ArrayList<AnimatorListener>) mListeners.clone();
        int numListeners = tmpListeners.size();
        for (int i = 0; i < numListeners; ++i) {
            tmpListeners.get(i).onAnimationStart(this);
        }
    }
};
