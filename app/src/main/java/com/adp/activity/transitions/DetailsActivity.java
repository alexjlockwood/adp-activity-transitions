package com.adp.activity.transitions;

import android.app.Activity;
import android.app.SharedElementCallback;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.adp.activity.transitions.util.CircularReveal;

import java.util.List;
import java.util.Map;

import static com.adp.activity.transitions.MainActivity.EXTRA_CURRENT_ITEM_POSITION;
import static com.adp.activity.transitions.MainActivity.EXTRA_OLD_ITEM_POSITION;

public class DetailsActivity extends Activity implements ViewPager.OnPageChangeListener {
    private static final String TAG = "DetailsActivity";
    private static final boolean DEBUG = true;

    private static final String STATE_CURRENT_POSITION = "state_current_position";
    private static final String STATE_OLD_POSITION = "state_old_position";

    private DetailsFragmentPagerAdapter mAdapter;
    private int mCurrentPosition;
    private int mOriginalPosition;
    private boolean mIsReturning;

    private final SharedElementCallback mCallback = new SharedElementCallback() {
        @Override
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            LOG("onMapSharedElements(List<String>, Map<String, View>)", mIsReturning);
            if (mIsReturning && mCurrentPosition != mOriginalPosition) {
                names.clear();
                sharedElements.clear();
                final View sharedView = mAdapter.getCurrentDetailsFragment().getSharedView();
                if (sharedView == null) {
                    // If shared view is null, then it has likely been scrolled off screen and
                    // recycled. In this case we cancel the shared element transition and use
                    // a fallback window animation instead.
                    // TODO: write a "split slide" return transition, similar to Newsstand.
                } else {
                    names.add(sharedView.getTransitionName());
                    sharedElements.put(sharedView.getTransitionName(), sharedView);
                }
                LOG(names.toString());
                LOG(sharedElements.toString());
            }
        }

        @Override
        public void onSharedElementStart(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
            LOG("onSharedElementStart(List<String>, List<View>, List<View>)", mIsReturning);
            if (!mIsReturning) {
                getWindow().setEnterTransition(makeEnterTransition(sharedElements.get(0)));
            }
        }
    };

    private static Transition makeEnterTransition(View sharedElement) {
        TransitionSet enterTransition = new TransitionSet();

        // Play a circular reveal animation starting beneath the shared element.
        Transition circularReveal = new CircularReveal(sharedElement);
        circularReveal.addTarget(R.id.reveal_container); // TODO: is it OK to add target by ID or should we add a specific view instead?
        enterTransition.addTransition(circularReveal);

        // Slide the cards in through the bottom of the screen.
        Transition cardSlide = new Slide(Gravity.BOTTOM);
        cardSlide.addTarget(R.id.card_view); // TODO: is it OK to add target by ID or should we add a specific set of views instead?
        enterTransition.addTransition(cardSlide);

        return enterTransition;
    }

    private static Transition makeReturnTransition() {
        TransitionSet returnTransition = new TransitionSet();

        // Slide and fade the circular reveal container off the top of the screen.
        TransitionSet slideFade = new TransitionSet();
        slideFade.addTarget(R.id.reveal_container);  // TODO: is it OK to add target by ID or should we add a specific set of views instead?
        slideFade.addTransition(new Slide(Gravity.TOP));
        slideFade.addTransition(new Fade());
        returnTransition.addTransition(slideFade);

        // Slide the cards off the bottom of the screen.
        Transition cardSlide = new Slide(Gravity.BOTTOM);
        cardSlide.addTarget(R.id.card_view); // TODO: is it OK to add target by ID or should we add a specific set of views instead?
        returnTransition.addTransition(cardSlide);

        return returnTransition;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        if (savedInstanceState == null) {
            mCurrentPosition = getIntent().getExtras().getInt(EXTRA_CURRENT_ITEM_POSITION);
            mOriginalPosition = mCurrentPosition;
        } else {
            mCurrentPosition = savedInstanceState.getInt(STATE_CURRENT_POSITION);
            mOriginalPosition = savedInstanceState.getInt(STATE_OLD_POSITION);
        }

        mAdapter = new DetailsFragmentPagerAdapter(getFragmentManager());
        final ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(mAdapter);
        pager.setOnPageChangeListener(this);
        pager.setCurrentItem(mCurrentPosition);

        setEnterSharedElementCallback(mCallback);
        getWindow().setReturnTransition(makeReturnTransition());
        postponeEnterTransition();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_CURRENT_POSITION, mCurrentPosition);
        outState.putInt(STATE_OLD_POSITION, mOriginalPosition);
    }

    @Override
    public void finishAfterTransition() {
        LOG("finishAfterTransition()");
        final Intent data = new Intent();
        data.putExtra(EXTRA_OLD_ITEM_POSITION, getIntent().getIntExtra(EXTRA_CURRENT_ITEM_POSITION, 0));
        data.putExtra(EXTRA_CURRENT_ITEM_POSITION, mCurrentPosition);
        setResult(RESULT_OK, data);
        mIsReturning = true;
        super.finishAfterTransition();
    }

    @Override
    public void onPageSelected(int position) {
        mCurrentPosition = position;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // Do nothing.
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // Do nothing.
    }

    private static void LOG(String message) {
        if (DEBUG) {
            Log.i(TAG, message);
        }
    }

    private static void LOG(String message, boolean isReturning) {
        if (DEBUG) {
            Log.i(TAG, String.format("%s: %s", isReturning ? "RETURNING" : "ENTERING", message));
        }
    }
}
