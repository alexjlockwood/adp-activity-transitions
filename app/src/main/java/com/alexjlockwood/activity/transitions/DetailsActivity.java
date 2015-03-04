package com.alexjlockwood.activity.transitions;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import static com.alexjlockwood.activity.transitions.MainActivity.EXTRA_STARTING_ALBUM_POSITION;
import static com.alexjlockwood.activity.transitions.Constants.ALBUM_IMAGE_URLS;

public class DetailsActivity extends Activity {
    private static final String TAG = DetailsActivity.class.getSimpleName();
    private static final boolean DEBUG = false;

    private static final String STATE_CURRENT_PAGE_POSITION = "state_current_page_position";

    private int mCurrentPosition;
    private int mStartingPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mStartingPosition = getIntent().getIntExtra(EXTRA_STARTING_ALBUM_POSITION, 0);
        if (savedInstanceState == null) {
            postponeEnterTransition();
            mCurrentPosition = mStartingPosition;
        } else {
            mCurrentPosition = savedInstanceState.getInt(STATE_CURRENT_PAGE_POSITION);
        }

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new DetailsFragmentPagerAdapter(getFragmentManager()));
        pager.setCurrentItem(mCurrentPosition);
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position;
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_CURRENT_PAGE_POSITION, mCurrentPosition);
    }

    private class DetailsFragmentPagerAdapter extends FragmentStatePagerAdapter {
        public DetailsFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return DetailsFragment.newInstance(position, mStartingPosition);
        }

        @Override
        public int getCount() {
            return ALBUM_IMAGE_URLS.length;
        }
    }
}
