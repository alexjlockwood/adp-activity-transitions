package com.adp.activity.transitions;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SharedElementCallback;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
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
import android.widget.TextView;

import com.adp.activity.transitions.util.CircularReveal;

import java.util.List;
import java.util.Map;

import static com.adp.activity.transitions.MainActivity.CAPTIONS;
import static com.adp.activity.transitions.MainActivity.COLORS;
import static com.adp.activity.transitions.MainActivity.EXTRA_CURRENT_ITEM_POSITION;
import static com.adp.activity.transitions.MainActivity.EXTRA_OLD_ITEM_POSITION;

public class DetailsActivity extends Activity implements ViewPager.OnPageChangeListener {
    private static final String TAG = "DetailsActivity";
    private static final boolean DEBUG = true;

    private static final String STATE_CURRENT_ITEM_POSITION = "state_current_item_position";
    private static final String STATE_OLD_ITEM_POSITION = "state_old_item_position";

    private CurrentPageFragmentAdapter mAdapter;
    private int mOldItemPosition;
    private int mCurrentItemPosition;
    private boolean mIsFinishing;

    private final SharedElementCallback mCallback = new SharedElementCallback() {
        @Override
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            LOG("onMapSharedElements(List<String>, Map<String, View>)", mIsFinishing);
            if (mIsFinishing && mCurrentItemPosition != mOldItemPosition) {
                names.clear();
                sharedElements.clear();
                final View sharedView = mAdapter.getCurrentPageFragment().getSharedView();
                names.add(sharedView.getTransitionName());
                sharedElements.put(sharedView.getTransitionName(), sharedView);
            }
        }

        @Override
        public void onSharedElementStart(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
            LOG("onSharedElementStart(List<String>, List<View>, List<View>)", mIsFinishing);
            if (!mIsFinishing) {
                // Create the enter transition.
                final TransitionSet enterTransition = new TransitionSet();
                enterTransition.addTransition(makeReveal(sharedElements.get(0)));
                enterTransition.addTransition(makeCardSlide());
                enterTransition.setOrdering(TransitionSet.ORDERING_TOGETHER);
                getWindow().setEnterTransition(enterTransition);
            }
        }

        @Override
        public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
            LOG("onSharedElementEnd(List<String>, List<View>, List<View>)", mIsFinishing);
        }
    };

    private static Transition makeReveal(View sharedElement) {
        final Transition reveal = new CircularReveal(sharedElement);
        reveal.addTarget(R.id.reveal_container); // TODO: is it OK to add target by ID or should we add a specific view instead?
        return reveal;
    }

    private static Transition makeRevealContainerSlide() {
        final TransitionSet transitionSet = new TransitionSet();
        final Transition slide = new Slide(Gravity.TOP);
        slide.addTarget(R.id.reveal_container);  // TODO: is it OK to add target by ID or should we add a specific set of views instead?
        transitionSet.addTransition(slide);
        final Transition fade = new Fade();
        fade.addTarget(R.id.reveal_container);
        transitionSet.addTransition(fade);
        transitionSet.setOrdering(TransitionSet.ORDERING_TOGETHER);
        return transitionSet;
    }

    private static Transition makeCardSlide() {
        final Transition slide = new Slide(Gravity.BOTTOM);
        slide.addTarget(R.id.card_view); // TODO: is it OK to add target by ID or should we add a specific set of views instead?
        return slide;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postponeEnterTransition();
        setContentView(R.layout.activity_details);

        // Create the return transition.
        final TransitionSet returnTransition = new TransitionSet();
        returnTransition.addTransition(makeRevealContainerSlide());
        returnTransition.addTransition(makeCardSlide());
        returnTransition.setOrdering(TransitionSet.ORDERING_TOGETHER);
        getWindow().setReturnTransition(returnTransition);

        if (savedInstanceState == null) {
            mCurrentItemPosition = getIntent().getExtras().getInt(EXTRA_CURRENT_ITEM_POSITION);
            mOldItemPosition = mCurrentItemPosition;
        } else {
            mCurrentItemPosition = savedInstanceState.getInt(STATE_CURRENT_ITEM_POSITION);
            mOldItemPosition = savedInstanceState.getInt(STATE_OLD_ITEM_POSITION);
        }

        mAdapter = new CurrentPageFragmentAdapter(getFragmentManager());
        final ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(mAdapter);
        pager.setOnPageChangeListener(this);
        pager.setCurrentItem(mCurrentItemPosition);

        setEnterSharedElementCallback(mCallback);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_CURRENT_ITEM_POSITION, mCurrentItemPosition);
        outState.putInt(STATE_OLD_ITEM_POSITION, mOldItemPosition);
    }

    public static class PageFragment extends Fragment {
        private static final String ARG_SELECTED_IMAGE_POSITION = "arg_selected_image_position";

        private View mSharedView;

        public static PageFragment newInstance(int position) {
            final Bundle args = new Bundle();
            args.putInt(ARG_SELECTED_IMAGE_POSITION, position);
            final PageFragment fragment = new PageFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_details, container, false);
            final int selectedPosition = getArguments().getInt(ARG_SELECTED_IMAGE_POSITION);
            mSharedView = rootView.findViewById(R.id.details_view);
            mSharedView.setBackgroundColor(COLORS[selectedPosition]);
            mSharedView.setTransitionName(CAPTIONS[selectedPosition]);
            final RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.num_columns)));
            recyclerView.setAdapter(new MyAdapter());
            final ViewTreeObserver observer = getActivity().getWindow().getDecorView().getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
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

        private class MyAdapter extends RecyclerView.Adapter<MyHolder> {
            @Override
            public MyHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                return new MyHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grid_item, viewGroup, false));
            }

            @Override
            public void onBindViewHolder(MyHolder holder, int position) {
                holder.bind(position);
            }

            @Override
            public int getItemCount() {
                return COLORS.length;
            }
        }

        private class MyHolder extends RecyclerView.ViewHolder {
            private final View mView;
            private final TextView mTextView;

            public MyHolder(View itemView) {
                super(itemView);
                itemView.setClickable(true);
                mView = itemView.findViewById(R.id.color_view);
                mTextView = (TextView) itemView.findViewById(R.id.text_view);
            }

            public void bind(int position) {
                mView.setBackgroundColor(COLORS[position]);
                mTextView.setText(CAPTIONS[position]);
            }
        }
    }

    private static class CurrentPageFragmentAdapter extends FragmentStatePagerAdapter {
        private PageFragment mCurrentPageFragment;

        public CurrentPageFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PageFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return COLORS.length;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            mCurrentPageFragment = (PageFragment) object;
        }

        public PageFragment getCurrentPageFragment() {
            return mCurrentPageFragment;
        }
    }

    @Override
    public void finishAfterTransition() {
        LOG("finishAfterTransition()");
        final Intent data = new Intent();
        final int oldPosition = getIntent().getIntExtra(EXTRA_CURRENT_ITEM_POSITION, 0);
        final int currentPosition = mCurrentItemPosition;
        data.putExtra(EXTRA_OLD_ITEM_POSITION, oldPosition);
        data.putExtra(EXTRA_CURRENT_ITEM_POSITION, currentPosition);
        setResult(RESULT_OK, data);
        mIsFinishing = true;
        super.finishAfterTransition();
    }

    @Override
    public void onPageSelected(int position) {
        if (mCurrentItemPosition != position) {
            mCurrentItemPosition = position;
        }
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

    private static void LOG(String message, boolean isFinishing) {
        if (DEBUG) {
            Log.i(TAG, String.format("%s: %s", isFinishing ? "FINISHING" : "ENTERING", message));
        }
    }
}
