package com.alexjlockwood.activity.transitions;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import static com.alexjlockwood.activity.transitions.Constants.ALBUM_NAMES;

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
        View root = inflater.inflate(R.layout.fragment_details, container, false);
        View revealContainer = root.findViewById(R.id.reveal_container);
        ImageView headerImage = (ImageView) revealContainer.findViewById(R.id.header_image);
        View infoText = root.findViewById(R.id.text_container);
        TextView titleText = (TextView) infoText.findViewById(R.id.title);
        ImageView backgroundImage = (ImageView) revealContainer.findViewById(R.id.background_image);
        int selectedPosition = getArguments().getInt(ARG_SELECTED_IMAGE_POSITION);
        Picasso.with(getActivity()).load(Constants.ALBUM_IMAGE_URLS[selectedPosition]).fit().into(headerImage);
        titleText.setText(ALBUM_NAMES[selectedPosition]);
        Picasso.with(getActivity()).load(Constants.BACKGROUND_IMAGE_URLS[selectedPosition]).fit().centerCrop().into(backgroundImage);
        return root;
    }
}
