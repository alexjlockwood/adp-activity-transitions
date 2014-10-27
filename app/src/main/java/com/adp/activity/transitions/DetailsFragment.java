package com.adp.activity.transitions;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DetailsFragment extends Fragment {
    private static final int[] IMAGE_RESOURCES = {R.drawable.p241, R.drawable.p242, R.drawable.p243};
    private static final String[] CAPTIONS = {"Season 5 #1", "Season 5 #2", "Season 6"};
    private static final String ARG_SELECTED_IMAGE_POSITION = "arg_selected_image_position";

    private RecyclerView mRecyclerView;
    private CardAdapter mAdapter;
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

        mAdapter = new CardAdapter(getActivity(), IMAGE_RESOURCES, CAPTIONS);
        mRecyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mCurrentOffset += dy;
                mAdapter.translateHeader(mCurrentOffset * 0.5f);
            }
        });

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
        return mRecyclerView.findViewById(R.id.header_image);
    }

    private class CardAdapter extends RecyclerView.Adapter<RecyclerHolder> {
        private static final int ITEM_TYPE_HEADER = 0;
        private static final int ITEM_TYPE_CARD = 1;

        private TranslatableHeaderView mHeader;
        private final LayoutInflater mInflater;
        private final int[] mImageResources;
        private final String[] mCaptions;

        public CardAdapter(Context context, int[] imageResources, String[] captions) {
            mInflater = LayoutInflater.from(context);
            mImageResources = imageResources;
            mCaptions = captions;
        }

        @Override
        public RecyclerHolder onCreateViewHolder(ViewGroup viewGroup, int itemType) {
            if (itemType == ITEM_TYPE_HEADER) {
                mHeader = new TranslatableHeaderView(viewGroup.getContext());
                mHeader.addView(mInflater.inflate(R.layout.reveal_container, viewGroup, false));
                return new HeaderHolder(mHeader);
            } else {
                return new CardHolder(mInflater.inflate(R.layout.image_card, viewGroup, false),
                        mImageResources, mCaptions);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerHolder holder, int position) {
            holder.bind(position);
        }

        @Override
        public int getItemCount() {
            return mImageResources.length + 1;
        }

        @Override
        public int getItemViewType(int position) {
            return position == 0 ? ITEM_TYPE_HEADER : ITEM_TYPE_CARD;
        }

        public void translateHeader(float offset) {
            mHeader.setTranslationY(offset);
            mHeader.setClipY(Math.round(offset));
        }
    }

    private abstract class RecyclerHolder extends RecyclerView.ViewHolder {
        public RecyclerHolder(View itemView) {
            super(itemView);
        }
        public abstract void bind(int position);
    }

    private class HeaderHolder extends RecyclerHolder {
        private final ImageView mHeaderImage;

        public HeaderHolder(View itemView) {
            super(itemView);
            mHeaderImage = (ImageView) itemView.findViewById(R.id.header_image);
        }

        public void bind(int position) {
            int selectedPosition = getArguments().getInt(ARG_SELECTED_IMAGE_POSITION);
            mHeaderImage.setTransitionName(MainActivity.CAPTIONS[selectedPosition]);
            mHeaderImage.setImageResource(MainActivity.IMAGES[selectedPosition]);
        }
    }

    private class CardHolder extends RecyclerHolder {
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
            mImageView.setImageResource(mImageResources[position - 1]);
            mTextView.setText(mCaptions[position - 1]);
        }
    }

    private static class TranslatableHeaderView extends RelativeLayout {
        private final Rect mTmpRect = new Rect();
        private int mOffset;

        public TranslatableHeaderView(Context context) {
            super(context);
        }

        @Override
        protected void dispatchDraw(@NonNull Canvas canvas) {
            mTmpRect.set(getLeft(), getTop(), getRight(), getBottom() + mOffset);
            canvas.clipRect(mTmpRect);
            super.dispatchDraw(canvas);
        }

        public void setClipY(int offset) {
            mOffset = offset;
            invalidate();
        }
    }
}
