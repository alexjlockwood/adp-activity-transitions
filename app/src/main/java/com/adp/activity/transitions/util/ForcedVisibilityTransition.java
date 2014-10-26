package com.adp.activity.transitions.util;

import android.annotation.TargetApi;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.view.View;

/**
 * A transition that emulates {@link android.transition.Visibility} while exposing a mechanism to force the direction
 * of the animation via modes {@link #SHOW} and {@link #HIDE}. This class may be useful when
 * building transitions that do not rely on views' properties changing.
 */
@TargetApi(19)
public abstract class ForcedVisibilityTransition extends Transition {

    private static final String PROPNAME_VISIBILITY = "play:forcedVisibility:visibility";
    private static final String[] sTransitionProperties = {
            PROPNAME_VISIBILITY
    };

    /**
     * Mode for creating an expanding/revealing animation
     */
    public static final int SHOW = 0x1;

    /**
     * Mode for creating a contracting/hiding animation
     */
    public static final int HIDE = 0x2;

    /** The mode that this transition will run in; either {@link #SHOW} or {@link #HIDE} **/
    private int mMode;

    public ForcedVisibilityTransition(int mode) {
        mMode = mode;
    }

    @Override
    public String[] getTransitionProperties() {
        return sTransitionProperties;
    }

    @Override
    public void captureStartValues(TransitionValues transitionValues) {
        transitionValues.values.put(PROPNAME_VISIBILITY,
                mMode == SHOW ? View.INVISIBLE : transitionValues.view.getVisibility());
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        transitionValues.values.put(PROPNAME_VISIBILITY,
                mMode == HIDE ? View.INVISIBLE : transitionValues.view.getVisibility());
    }

    /**
     * Returns true if this Transition will show; false if it will hide
     */
    protected boolean isRevealing() {
        return (mMode & SHOW) != 0;
    }
}