package com.alexjlockwood.activity.transitions;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import static com.alexjlockwood.activity.transitions.Utils.RADIOHEAD_ALBUM_URLS;

public class DetailsFragmentPagerAdapter extends FragmentStatePagerAdapter {
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
        return RADIOHEAD_ALBUM_URLS.length;
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