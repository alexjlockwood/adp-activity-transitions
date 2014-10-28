package com.adp.activity.transitions;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
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

    private View mHeader;

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

        mHeader = root.findViewById(R.id.reveal_container);

        int selectedPosition = getArguments().getInt(ARG_SELECTED_IMAGE_POSITION);
        ImageView headerImage = (ImageView) mHeader.findViewById(R.id.header_image);
        headerImage.setTransitionName(MainActivity.CAPTIONS[selectedPosition]);
        headerImage.setImageResource(MainActivity.IMAGES[selectedPosition]);
        colorize(root, (((BitmapDrawable) headerImage.getDrawable()).getBitmap()));
        ((TextView) root.findViewById(R.id.title)).setText(MainActivity.CAPTIONS[selectedPosition]);

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
        return mHeader.findViewById(R.id.header_image);
    }

    private void colorize(View view, Bitmap photo) {
        Palette palette = Palette.generate(photo);
        applyPalette(view, palette);
    }

    private void applyPalette(View view, Palette palette) {
        getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(palette.getDarkMutedSwatch().getRgb()));

        TextView titleView = (TextView) view.findViewById(R.id.title);
        titleView.setTextColor(palette.getVibrantColor(Color.BLACK));

        TextView descriptionView = (TextView) view.findViewById(R.id.description);
        descriptionView.setTextColor(palette.getLightVibrantColor(Color.BLACK));

        View infoView = view.findViewById(R.id.text_container);
        infoView.setBackgroundColor(palette.getLightMutedColor(Color.WHITE));
    }
}
