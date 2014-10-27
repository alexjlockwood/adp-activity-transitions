package com.adp.activity.transitions;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SharedElementCallback;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

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
                names.add(sharedView.getTransitionName());
                sharedElements.put(sharedView.getTransitionName(), sharedView);
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

    public static class DetailsFragment extends Fragment {
        private static final int[] IMAGE_RESOURCES = {R.drawable.p241, R.drawable.p242, R.drawable.p243};
        private static final String[] CAPTIONS = {"Season 5 #1", "Season 5 #2", "Season 6"};
        private static final String ARG_SELECTED_IMAGE_POSITION = "arg_selected_image_position";

        private View mSharedView;

        public static DetailsFragment newInstance(int position) {
            final Bundle args = new Bundle();
            args.putInt(ARG_SELECTED_IMAGE_POSITION, position);
            final DetailsFragment fragment = new DetailsFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_details, container, false);
            final int selectedPosition = getArguments().getInt(ARG_SELECTED_IMAGE_POSITION);
            mSharedView = rootView.findViewById(R.id.details_view);
            mSharedView.setBackgroundColor(MainActivity.COLORS[selectedPosition]);
            mSharedView.setTransitionName(MainActivity.CAPTIONS[selectedPosition]);
            final RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(new MyAdapter(getActivity(), IMAGE_RESOURCES, CAPTIONS));
            getActivity().getWindow().getDecorView().getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    getActivity().getWindow().getDecorView().getViewTreeObserver().removeOnPreDrawListener(this);
                    getActivity().startPostponedEnterTransition();
                    return true;
                }
            });
            return rootView;
        }

        public View getSharedView() {
            return mSharedView;
        }

        private static class MyAdapter extends RecyclerView.Adapter<MyHolder> {
            private final LayoutInflater mInflater;
            private final int[] mImageResources;
            private final String[] mCaptions;

            public MyAdapter(Context context, int[] imageResources, String[] captions) {
                mInflater = LayoutInflater.from(context);
                mImageResources = imageResources;
                mCaptions = captions;
            }

            @Override
            public MyHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                return new MyHolder(mInflater.inflate(R.layout.fragment_details_card, viewGroup, false),mImageResources, mCaptions);
            }

            @Override
            public void onBindViewHolder(MyHolder holder, int position) {
                holder.bind(position);
            }

            @Override
            public int getItemCount() {
                return mImageResources.length;
            }
        }

        private static class MyHolder extends RecyclerView.ViewHolder {
            private final ImageView mImageView;
            private final TextView mTextView;
            private final int[] mImageResources;
            private final String[] mCaptions;

            public MyHolder(View itemView, int[] colors, String[] captions) {
                super(itemView);
                itemView.setClickable(true);
                mImageView = (ImageView) itemView.findViewById(R.id.image);
                mTextView = (TextView) itemView.findViewById(R.id.text);
                mImageResources = colors;
                mCaptions = captions;
            }

            public void bind(int position) {
                mImageView.setImageResource(mImageResources[position]);
                mTextView.setText(mCaptions[position]);
            }
        }
    }

    private static class DetailsFragmentPagerAdapter extends FragmentStatePagerAdapter {
        private DetailsFragment mCurrentFragment;

        public DetailsFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return DetailsFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return MainActivity.COLORS.length;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            mCurrentFragment = (DetailsFragment) object;
        }

        public DetailsFragment getCurrentDetailsFragment() {
            return mCurrentFragment;
        }
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
