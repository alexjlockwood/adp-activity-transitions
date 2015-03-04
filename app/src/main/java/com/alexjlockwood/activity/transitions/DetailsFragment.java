package com.alexjlockwood.activity.transitions;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import static com.alexjlockwood.activity.transitions.Constants.ALBUM_IMAGE_URLS;
import static com.alexjlockwood.activity.transitions.Constants.ALBUM_NAMES;
import static com.alexjlockwood.activity.transitions.Constants.BACKGROUND_IMAGE_URLS;

public class DetailsFragment extends Fragment {
    private static final String TAG = DetailsFragment.class.getSimpleName();
    private static final boolean DEBUG = false;

    private static final String ARG_SELECTED_IMAGE_POSITION = "arg_selected_image_position";

    public static DetailsFragment newInstance(int position) {
        Bundle args = new Bundle();
        args.putInt(ARG_SELECTED_IMAGE_POSITION, position);
        DetailsFragment fragment = new DetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);

        ImageView albumImage = (ImageView) rootView.findViewById(R.id.details_album_image);
        ImageView backgroundImage = (ImageView) rootView.findViewById(R.id.details_background_image);

        View textContainer = rootView.findViewById(R.id.details_text_container);
        TextView albumTitleText = (TextView) textContainer.findViewById(R.id.details_album_title);

        int selectedPosition = getArguments().getInt(ARG_SELECTED_IMAGE_POSITION);
        String albumImageUrl = ALBUM_IMAGE_URLS[selectedPosition];
        String backgroundImageUrl = BACKGROUND_IMAGE_URLS[selectedPosition];
        String albumName = ALBUM_NAMES[selectedPosition];

        Picasso.with(getActivity()).load(albumImageUrl).fit().centerCrop().into(albumImage);
        Picasso.with(getActivity()).load(backgroundImageUrl).fit().centerCrop().into(backgroundImage);
        albumTitleText.setText(albumName);

        return rootView;
    }
}
