package com.alexjlockwood.activity.transitions;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import static com.alexjlockwood.activity.transitions.Constants.ALBUM_IMAGE_URLS;
import static com.alexjlockwood.activity.transitions.Constants.ALBUM_NAMES;
import static com.alexjlockwood.activity.transitions.Constants.BACKGROUND_IMAGE_URLS;

public class DetailsFragment extends Fragment {
    private static final String TAG = DetailsFragment.class.getSimpleName();
    private static final boolean DEBUG = false;

    private static final String ARG_ALBUM_IMAGE_POSITION = "arg_album_image_position";
    private static final String ARG_STARTING_ALBUM_IMAGE_POSITION = "arg_starting_album_image_position";

    public static DetailsFragment newInstance(int position, int startingPosition) {
        Bundle args = new Bundle();
        args.putInt(ARG_ALBUM_IMAGE_POSITION, position);
        args.putInt(ARG_STARTING_ALBUM_IMAGE_POSITION, startingPosition);
        DetailsFragment fragment = new DetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);

        final ImageView albumImage = (ImageView) rootView.findViewById(R.id.details_album_image);
        ImageView backgroundImage = (ImageView) rootView.findViewById(R.id.details_background_image);

        View textContainer = rootView.findViewById(R.id.details_text_container);
        TextView albumTitleText = (TextView) textContainer.findViewById(R.id.details_album_title);

        int albumPosition = getArguments().getInt(ARG_ALBUM_IMAGE_POSITION);
        String albumImageUrl = ALBUM_IMAGE_URLS[albumPosition];
        String backgroundImageUrl = BACKGROUND_IMAGE_URLS[albumPosition];
        String albumName = ALBUM_NAMES[albumPosition];
        albumImage.setTransitionName(albumName);

        Picasso.with(getActivity()).load(albumImageUrl).fit().centerCrop().into(albumImage);
        Picasso.with(getActivity()).load(backgroundImageUrl).fit().centerCrop().into(backgroundImage);
        albumTitleText.setText(albumName);

        int startingPosition = getArguments().getInt(ARG_STARTING_ALBUM_IMAGE_POSITION);
        if (savedInstanceState == null && albumPosition == startingPosition) {
            albumImage.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    albumImage.getViewTreeObserver().removeOnPreDrawListener(this);
                    getActivity().startPostponedEnterTransition();
                    return true;
                }
            });
        }

        return rootView;
    }
}
