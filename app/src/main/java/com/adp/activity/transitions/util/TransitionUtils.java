package com.adp.activity.transitions.util;

import android.content.Context;
import android.graphics.Rect;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

import java.util.ArrayList;
import java.util.List;

public final class TransitionUtils {

    private static Interpolator sFastOutSlowIn;

    public static Interpolator fastOutSlowIn(Context context) {
        if (sFastOutSlowIn == null) {
            sFastOutSlowIn = AnimationUtils.loadInterpolator(context, android.R.interpolator.fast_out_slow_in);
        }
        return sFastOutSlowIn;
    }

    public static Interpolator slowOutFastIn(final Context context) {
        return new Interpolator() {
            private Interpolator inverse = fastOutSlowIn(context);

            @Override
            public float getInterpolation(float input) {
                return 1.f - inverse.getInterpolation(1.f - input);
            }
        };
    }

    /**
     * Builds a {@link TransitionSet} from a number of discrete transitions, applying targets and
     * exclusions to prevent overlap between animators of the children transitions.
     */
    public static TransitionSet aggregate(Transition... transitions) {
        List<View> allTargets = new ArrayList<View>();
        for (Transition transition : transitions) {
            allTargets.addAll(transition.getTargets());
        }
        // Apply exclusions
        for (Transition transition : transitions) {
            for (View target : allTargets) {
                if (!transition.getTargets().contains(target)) {
                    transition.excludeTarget(target, true);
                }
            }
        }
        TransitionSet set = new TransitionSet();
        for (Transition transition : transitions) {
            set.addTransition(transition);
        }
        // The TransitionSet must target all of its childrens' targets to ensure that
        // TransitionSet.captureValues() will be run as needed
        for (View target : allTargets) {
            set.addTarget(target);
        }
        return set;
    }

    /**
     * Returns the Rect of a view as given by left, top, right, and bottom. This rect describes the
     * view's position in its parent.
     */
    public static Rect viewBounds(View view) {
        return new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
    }

    private TransitionUtils() {
    }
}
