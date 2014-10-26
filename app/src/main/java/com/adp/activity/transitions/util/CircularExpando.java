package com.adp.activity.transitions.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.transition.ArcMotion;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.View;

/**
 * Defines a set of helper methods to create transitions that may include a shared hero element and
 * a number of transitioning views, including a {@link NewCircularReveal} which is the defining
 * characteristic of the Circular Expando transition. Transition views are coupled to the use of a
 * PlayHeaderListLayout.
 */
@TargetApi(21)
public class CircularExpando {

    public static final long DEFAULT_DURATION_MS = 450L;

    /**
     * Builds a {@link Transition} with a Material-style arc path motion and interpolation for use
     * on the hero element of a Circular-Reveal Expando during the enter transition phase.
     */
    public static Transition sharedElementEnterTransition(Context context, View hero, boolean maintainsHeroAspectRatio) {
        return sharedElementTransition(context, hero, maintainsHeroAspectRatio, true);
    }

    /**
     * Builds a {@link Transition} with a Material-style arc path motion and interpolation for use
     * on the hero element of a Circular-Reveal Expando during the return transition phase.
     */
    public static Transition sharedElementReturnTransition(Context context, View hero, boolean maintainsHeroAspectRatio) {
        return sharedElementTransition(context, hero, maintainsHeroAspectRatio, false);
    }

    private static Transition sharedElementTransition(Context context, View hero, boolean maintainsHeroAspectRatio, boolean isEntering) {
        Transition heroTransition = new Scale(isEntering)
                .forceMaintainAspectRatio(maintainsHeroAspectRatio)
                .addTarget(hero)
                .setInterpolator(TransitionUtils.fastOutSlowIn(context))
                .setDuration(DEFAULT_DURATION_MS);
        heroTransition.setPathMotion(new ArcMotion());
        return heroTransition;
    }

    public static Transition enterTransition(Activity activity, View hero, View container) {
        return windowTransition(activity, hero, container, true);
    }

    public static Transition returnTransition(Activity activity, View container) {
        return windowTransition(activity, null, container, false);
    }

    private static Transition windowTransition(Activity activity, View hero, View container, boolean isEntering) {
        return new TransitionSet()
                .addTransition(revealTransition(container, hero, isEntering))
                .setInterpolator(TransitionUtils.fastOutSlowIn(activity))
                .setDuration(DEFAULT_DURATION_MS);
    }

    /**
     * Builds a transition for the splash/background owned by a PlayHeaderListLayout
     *
     * @param isEntering may receive a different animation depending on its direction
     */
    private static Transition revealTransition(View splash, View hero, boolean isEntering) {
        if (isEntering) {
            Transition revealSplash = new NewCircularReveal(TransitionUtils.viewBounds(hero)).addTarget(splash);
            revealSplash.setPathMotion(new ArcMotion());
            return revealSplash;
        } else {
            // TODO(klampert): Once b/17112782 is fixed, use a CircularReveal during the return
            return new Slide(Gravity.TOP).addTarget(splash);
        }
    }
}