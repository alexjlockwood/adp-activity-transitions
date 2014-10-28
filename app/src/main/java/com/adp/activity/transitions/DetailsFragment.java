package com.adp.activity.transitions;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailsFragment extends Fragment {
    private static final String TAG = "DetailsFragment";
    private static final boolean DEBUG = true;

    private static final String ARG_SELECTED_IMAGE_POSITION = "arg_selected_image_position";

    private ObservableScrollView mScrollView;
    private ParallaxHeaderView mHeader;
    private int mCurrentOffset;

    public static DetailsFragment newInstance(int position) {
        final Bundle args = new Bundle();
        args.putInt(ARG_SELECTED_IMAGE_POSITION, position);
        final DetailsFragment fragment = new DetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_details, container, false);

        mScrollView = (ObservableScrollView) root.findViewById(R.id.scroll_view);
        mScrollView.setOnScrollListener(new ObservableScrollView.OnScrollListener() {
            @Override
            public void onScrolled(int l, int t, int oldl, int oldt) {
                Log.i(TAG, String.format("onScrolled(%d, %d, %d, %d)", l, t, oldl, oldt));
                    float backgroundTop = t * 0.5f;
                    backgroundTop = Math.max(0, backgroundTop);
                    Log.i(TAG, "setTranslationY(" + backgroundTop + ")");
                    mHeader.setTranslationY(backgroundTop);
                    Log.i(TAG, "getY()" + mHeader.getY());
            }
        });
        mHeader = (ParallaxHeaderView) root.findViewById(R.id.parallax_header);

        int selectedPosition = getArguments().getInt(ARG_SELECTED_IMAGE_POSITION);
        ImageView headerImage = (ImageView) mHeader.findViewById(R.id.header_image);
        headerImage.setTransitionName(MainActivity.CAPTIONS[selectedPosition]);
        headerImage.setImageResource(MainActivity.IMAGES[selectedPosition]);

        TextView title = (TextView) root.findViewById(R.id.title);
        title.setText(MainActivity.CAPTIONS[selectedPosition]);

        root.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                root.getViewTreeObserver().removeOnPreDrawListener(this);
                getActivity().startPostponedEnterTransition();
                return true;
            }
        });

        return root;
    }

    @Nullable // Might return null if the header image is no longer on screen.
    public View getSharedView() {
        return mScrollView.findViewById(R.id.header_image);
    }

}
