package com.adp.activity.transitions;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.SharedElementCallback;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private static final boolean DEBUG = true;

    static final String EXTRA_CURRENT_ITEM_POSITION = "extra_current_item_position";
    static final String EXTRA_OLD_ITEM_POSITION = "extra_old_item_position";
    static final int[] IMAGES = {
            R.drawable.radiohead_backwards_600, R.drawable.radiohead_backwards_600,
            R.drawable.radiohead_backwards_600, R.drawable.radiohead_backwards_600,
            R.drawable.radiohead_backwards_600, R.drawable.radiohead_backwards_600,
            R.drawable.radiohead_backwards_600, R.drawable.radiohead_backwards_600,
    };
    static final String[] CAPTIONS = {
            "Radiohead #1", "Radiohead #2", "Radiohead #3", "Radiohead #4",
            "Radiohead #5", "Radiohead #6", "Radiohead #7", "Radiohead #8",
    };

    private RecyclerView mRecyclerView;
    private Bundle mTmpState;
    private boolean mIsReentering;

    private final SharedElementCallback mCallback = new SharedElementCallback() {
        @Override
        public void onRejectSharedElements(List<View> rejectedSharedElements) {
            LOG("onMapSharedElements(List<View>)", mIsReentering);
        }

        @Override
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            LOG("onMapSharedElements(List<String>, Map<String, View>)", mIsReentering);
            if (mTmpState != null) {
                final int oldPosition = mTmpState.getInt(EXTRA_OLD_ITEM_POSITION);
                final int currentPosition = mTmpState.getInt(EXTRA_CURRENT_ITEM_POSITION);
                if (currentPosition != oldPosition) {
                    // If currentPosition != oldPosition the user must have swiped to a different
                    // page in the DetailsActivity. We must update the shared element so that the
                    // correct one falls into place.
                    final String newTransitionName = CAPTIONS[currentPosition];
                    final View newSharedView = mRecyclerView.findViewWithTag(newTransitionName);
                    if (newSharedView != null) {
                        names.clear();
                        names.add(newTransitionName);
                        sharedElements.clear();
                        sharedElements.put(newTransitionName, newSharedView);
                    }
                }
            }

            View decor = getWindow().getDecorView();
            View navigationBar = decor.findViewById(android.R.id.navigationBarBackground);
            View statusBar = decor.findViewById(android.R.id.statusBarBackground);
            int actionBarId = getResources().getIdentifier("action_bar_container", "id", "android");
            View actionBar = decor.findViewById(actionBarId);

            if (!mIsReentering) {
                names.add("navigationBar");
                sharedElements.put("navigationBar", navigationBar);
                names.add("statusBar");
                sharedElements.put("statusBar", statusBar);
                names.add("actionBar");
                sharedElements.put("actionBar", actionBar);
            } else {
                names.remove("actionBar");
                sharedElements.remove("actionBar");
                names.remove("statusBar");
                sharedElements.remove("statusBar");
                names.remove("navigationBar");
                sharedElements.remove("navigationBar");
            }

            LOG("=== names: " + names.toString(), mIsReentering);
            LOG("=== sharedElements: " + makeString(sharedElements.keySet()), mIsReentering);
        }

        @Override
        public void onSharedElementStart(List<String> sharedElementNames, List<View> sharedElements,
                                         List<View> sharedElementSnapshots) {
            LOG("onSharedElementStart(List<String>, List<View>, List<View>)", mIsReentering);
        }

        @Override
        public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements,
                                       List<View> sharedElementSnapshots) {
            LOG("onSharedElementEnd(List<String>, List<View>, List<View>)", mIsReentering);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Resources res = getResources();
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, res.getInteger(R.integer.num_columns)));
        mRecyclerView.setAdapter(new CardAdapter());

        setExitSharedElementCallback(mCallback);
    }

    private class CardAdapter extends RecyclerView.Adapter<CardHolder> {
        @Override
        public CardHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            return new CardHolder(inflater.inflate(R.layout.image_card, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(CardHolder holder, int position) {
            holder.bind(position);
        }

        @Override
        public int getItemCount() {
            return IMAGES.length;
        }
    }

    private class CardHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView mImage;
        private final TextView mTextView;
        private int mPosition;

        public CardHolder(View itemView) {
            super(itemView);
            mImage = (ImageView) itemView.findViewById(R.id.image);
            mTextView = (TextView) itemView.findViewById(R.id.text);
            mTextView.setOnClickListener(this);
        }

        public void bind(int position) {
            mImage.setImageResource(IMAGES[position]);
            mImage.setTransitionName(CAPTIONS[position]);
            mImage.setTag(CAPTIONS[position]);
            mTextView.setText(CAPTIONS[position]);
            mPosition = position;
        }

        @Override
        public void onClick(View v) {
            LOG("startActivity(Intent, Bundle)", mIsReentering);
            final Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
            intent.putExtra(EXTRA_CURRENT_ITEM_POSITION, mPosition);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(
                    MainActivity.this, mImage, mImage.getTransitionName()).toBundle());
        }
    }

    @Override
    public void onActivityReenter(int requestCode, Intent data) {
        mIsReentering = true;
        LOG("onActivityReenter(int, Intent)", true);
        super.onActivityReenter(requestCode, data);
        if (data != null && data.getExtras() != null) {
            mTmpState = new Bundle(data.getExtras());
            mRecyclerView.scrollToPosition(mTmpState.getInt(EXTRA_CURRENT_ITEM_POSITION));
        }
        postponeEnterTransition();
        mRecyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                startPostponedEnterTransition();
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTmpState = null;
        mIsReentering = false;
    }

    private static void LOG(String message) {
        if (DEBUG) {
            Log.i(TAG, message);
        }
    }

    private static void LOG(String message, boolean isReentering) {
        if (DEBUG) {
            Log.i(TAG, String.format("%s: %s", isReentering ? "REENTERING" : "EXITING", message));
        }
    }

    private static String makeString(Set<String> set) {
        Iterator<String> i = set.iterator();
        if (!i.hasNext()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        while (true) {
            String e = i.next();
            sb.append(e);
            if (!i.hasNext()) {
                return sb.append(']').toString();
            }
            sb.append(", ");
        }
    }
}
