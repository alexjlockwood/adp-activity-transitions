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

import static com.adp.activity.transitions.Utils.RADIOHEAD_ALBUM_IDS;
import static com.adp.activity.transitions.Utils.RADIOHEAD_ALBUM_NAMES;
import static com.adp.activity.transitions.Utils.RADIOHEAD_BACKGROUND_IDS;

public class DetailsFragment extends Fragment {
    private static final String TAG = "DetailsFragment";
    private static final boolean DEBUG = true;

    private static final String ARG_SELECTED_IMAGE_POSITION = "arg_selected_image_position";
    private static final SparseArray<Bitmap> BITMAP_CACHE = new SparseArray<>();

    public static DetailsFragment newInstance(int position) {
        Bundle args = new Bundle();
        args.putInt(ARG_SELECTED_IMAGE_POSITION, position);
        DetailsFragment fragment = new DetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_details, container, false);
        View revealContainer = root.findViewById(R.id.reveal_container);
        ImageView headerImage = (ImageView) revealContainer.findViewById(R.id.header_image);
        View infoText = root.findViewById(R.id.text_container);
        TextView titleText = (TextView) infoText.findViewById(R.id.title);
        ImageView backgroundImage = (ImageView) revealContainer.findViewById(R.id.background_image);

        int selectedPosition = getArguments().getInt(ARG_SELECTED_IMAGE_POSITION);
        headerImage.setTransitionName(RADIOHEAD_ALBUM_NAMES[selectedPosition]);
        headerImage.setImageResource(RADIOHEAD_ALBUM_IDS[selectedPosition]);
        titleText.setText(RADIOHEAD_ALBUM_NAMES[selectedPosition]);

        root.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                root.getViewTreeObserver().removeOnPreDrawListener(this);
                getActivity().startPostponedEnterTransition();
                return true;
            }
        });

        int imageResource = RADIOHEAD_BACKGROUND_IDS[selectedPosition];
        Bitmap bitmap = BITMAP_CACHE.get(imageResource);
        if (BITMAP_CACHE.get(imageResource) == null) {
            backgroundImage.setImageResource(RADIOHEAD_BACKGROUND_IDS[selectedPosition]);
            bitmap = (((BitmapDrawable) backgroundImage.getDrawable()).getBitmap());
            BITMAP_CACHE.put(imageResource, bitmap);
        } else {
            backgroundImage.setImageBitmap(bitmap);
        }

        return root;
    }

    /**
     * Returns the shared element that should be transitioned back to the previous Activity,
     * or null if the view is not visible on screen.
     */
    @Nullable
    public View getSharedElement() {
        View view = getView().findViewById(R.id.header_image);
        if (Utils.isViewInBounds(getView().findViewById(R.id.scroll_view), view)) {
                return view;
        }
        return null;
    }
}
