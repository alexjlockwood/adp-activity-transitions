package com.adp.activity.transitions;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.SparseArray;
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
    private static final SparseArray<Bitmap> BITMAP_CACHE = new SparseArray<>();

    private static final int[] BACKGROUND_IMAGES = {
            R.drawable.thom1,
            R.drawable.thom2,
            R.drawable.thom3,
            R.drawable.thom4,
            R.drawable.thom5,
            R.drawable.thom6,
    };

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
        View revealContainer = root.findViewById(R.id.reveal_container);
        ImageView headerImage = (ImageView) revealContainer.findViewById(R.id.header_image);
        ImageView backgroundImage = (ImageView) revealContainer.findViewById(R.id.background_image);
        View infoText = root.findViewById(R.id.text_container);
        TextView titleText = (TextView) infoText.findViewById(R.id.title);
        TextView descText = (TextView) infoText.findViewById(R.id.description);

        int selectedPosition = getArguments().getInt(ARG_SELECTED_IMAGE_POSITION);
        headerImage.setTransitionName(MainActivity.CAPTIONS[selectedPosition]);
        headerImage.setImageResource(MainActivity.IMAGES[selectedPosition]);
        titleText.setText(MainActivity.CAPTIONS[selectedPosition]);

        root.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                root.getViewTreeObserver().removeOnPreDrawListener(this);
                getActivity().startPostponedEnterTransition();
                return true;
            }
        });


        int imageResource = BACKGROUND_IMAGES[selectedPosition];
        Bitmap bitmap = BITMAP_CACHE.get(imageResource);
        if (BITMAP_CACHE.get(imageResource) == null) {
            backgroundImage.setImageResource(BACKGROUND_IMAGES[selectedPosition]);
            bitmap = (((BitmapDrawable) backgroundImage.getDrawable()).getBitmap());
            BITMAP_CACHE.put(imageResource, bitmap);
        } else {
            backgroundImage.setImageBitmap(bitmap);
        }

//        Palette palette = Palette.generate(bitmap, 24);
        //getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(palette.getDarkMutedColor(Color.WHITE)));
//        titleText.setTextColor(palette.getDarkVibrantColor(Color.BLACK));
//        descText.setTextColor(palette.getVibrantColor(Color.BLACK));
//        infoText.setBackgroundColor(palette.getLightMutedColor(Color.WHITE));

        return root;
    }

    // TODO: need to reimplement this so that it returns null when the image is off screen.
    @Nullable // Might return null if the header image is no longer on screen.
    public View getSharedView() {
        if (getView() == null) {
            return null;
        }
        return getView().findViewById(R.id.header_image);
    }
}
