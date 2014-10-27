package com.adp.activity.transitions;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailsFragment extends Fragment {
    private static final int[] IMAGE_RESOURCES = {R.drawable.p241, R.drawable.p242, R.drawable.p243};
    private static final String[] CAPTIONS = {"Season 5 #1", "Season 5 #2", "Season 6"};
    private static final String ARG_SELECTED_IMAGE_POSITION = "arg_selected_image_position";

    private ImageView mSharedView;

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

        int selectedPosition = getArguments().getInt(ARG_SELECTED_IMAGE_POSITION);
        mSharedView = (ImageView) root.findViewById(R.id.header_image);
        mSharedView.setImageResource(MainActivity.IMAGES[selectedPosition]);
        mSharedView.setTransitionName(MainActivity.CAPTIONS[selectedPosition]);

        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new CardAdapter(getActivity(), IMAGE_RESOURCES, CAPTIONS));

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

    public View getSharedView() {
        return mSharedView;
    }

    private static class CardAdapter extends RecyclerView.Adapter<CardHolder> {
        private final LayoutInflater mInflater;
        private final int[] mImageResources;
        private final String[] mCaptions;

        public CardAdapter(Context context, int[] imageResources, String[] captions) {
            mInflater = LayoutInflater.from(context);
            mImageResources = imageResources;
            mCaptions = captions;
        }

        @Override
        public CardHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new CardHolder(mInflater.inflate(R.layout.image_card, viewGroup, false),
                    mImageResources, mCaptions);
        }

        @Override
        public void onBindViewHolder(CardHolder holder, int position) {
            holder.bind(position);
        }

        @Override
        public int getItemCount() {
            return mImageResources.length;
        }
    }

    private static class CardHolder extends RecyclerView.ViewHolder {
        private final ImageView mImageView;
        private final TextView mTextView;
        private final int[] mImageResources;
        private final String[] mCaptions;

        public CardHolder(View itemView, int[] colors, String[] captions) {
            super(itemView);
            itemView.setClickable(true);
            mImageView = (ImageView) itemView.findViewById(R.id.image);
            mTextView = (TextView) itemView.findViewById(R.id.text);
            mImageResources = colors;
            mCaptions = captions;
        }

        public void bind(int position) {
            mImageView.setImageResource(mImageResources[position]);
            mTextView.setText(mCaptions[position]);
        }
    }
}
