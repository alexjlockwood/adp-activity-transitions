package com.alexjlockwood.activity.transitions;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import static com.alexjlockwood.activity.transitions.MainActivity.EXTRA_CURRENT_ALBUM_POSITION;
import static com.alexjlockwood.activity.transitions.Constants.ALBUM_IMAGE_URLS;

public class DetailsActivity extends Activity {
    private static final String TAG = DetailsActivity.class.getSimpleName();
    private static final boolean DEBUG = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new DetailsFragmentPagerAdapter(getFragmentManager()));
        int currentPosition = getIntent().getIntExtra(EXTRA_CURRENT_ALBUM_POSITION, 0);
        pager.setCurrentItem(currentPosition);

        postponeEnterTransition();
    }

    private static class DetailsFragmentPagerAdapter extends FragmentStatePagerAdapter {
        public DetailsFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return DetailsFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return ALBUM_IMAGE_URLS.length;
        }
    }
}
