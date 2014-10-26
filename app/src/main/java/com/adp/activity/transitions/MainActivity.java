package com.adp.activity.transitions;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.SharedElementCallback;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private static final boolean DEBUG = true;

    static final String EXTRA_CURRENT_ITEM_POSITION = "extra_current_item_position";
    static final String EXTRA_OLD_ITEM_POSITION = "extra_old_item_position";
    static final int[] COLORS = {Color.RED, Color.GREEN, Color.BLUE, Color.CYAN, Color.YELLOW, Color.GRAY,};
    static final String[] CAPTIONS = {"Red", "Green", "Blue", "Cyan", "Yellow", "Gray",};

    private RecyclerView mRecyclerView;
    private Bundle mTmpState;

    private final SharedElementCallback mCallback = new SharedElementCallback() {
        @Override
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            LOG("onMapSharedElements(List<String>, Map<String, View>)");
            if (mTmpState != null) {
                final int oldPosition = mTmpState.getInt(EXTRA_OLD_ITEM_POSITION);
                final int currentPosition = mTmpState.getInt(EXTRA_CURRENT_ITEM_POSITION);
                if (currentPosition != oldPosition) {
                    // Then the user must have swiped to a different page in the DetailsActivity.
                    // We must update the shared element so that the correct one falls into place.
                    final String newTransitionName = CAPTIONS[currentPosition];
                    final View newSharedView =  mRecyclerView.findViewWithTag(newTransitionName);
                    names.clear();
                    names.add(newTransitionName);
                    sharedElements.clear();
                    sharedElements.put(newTransitionName, newSharedView);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postponeEnterTransition();
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, getResources().getInteger(R.integer.num_columns)));
        mRecyclerView.setAdapter(new MyAdapter());
        getWindow().getDecorView().getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                getWindow().getDecorView().getViewTreeObserver().removeOnPreDrawListener(this);
                startPostponedEnterTransition();
                return true;
            }
        });
        setExitSharedElementCallback(mCallback);
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

    private class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final View mView;
        private final TextView mTextView;
        private int mPosition;

        public MyHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mView = itemView.findViewById(R.id.color_view);
            mTextView = (TextView) itemView.findViewById(R.id.text_view);
        }

        public void bind(int position) {
            mView.setBackgroundColor(COLORS[position]);
            mView.setTransitionName(CAPTIONS[position]);
            mView.setTag(CAPTIONS[position]);
            mTextView.setText(CAPTIONS[position]);
            mPosition = position;
        }

        @Override
        public void onClick(View v) {
            LOG("startActivity(Intent, Bundle)");
            final Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
            intent.putExtra(EXTRA_CURRENT_ITEM_POSITION, mPosition);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(
                    MainActivity.this, mView, mView.getTransitionName()).toBundle());
        }
    }

    @Override
    public void onActivityReenter(int requestCode, Intent data) {
        LOG("onActivityReenter(int, Intent)");
        super.onActivityReenter(requestCode, data);
        if (data != null && data.getExtras() != null) {
            mTmpState = new Bundle(data.getExtras());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTmpState = null;
    }

    private static void LOG(String message) {
        if (DEBUG) {
            Log.i(TAG, message);
        }
    }
}
