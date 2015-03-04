package com.alexjlockwood.activity.transitions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import static com.alexjlockwood.activity.transitions.Constants.ALBUM_IMAGE_URLS;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final boolean DEBUG = false;

    static final String EXTRA_CURRENT_ALBUM_POSITION = "extra_current_item_position";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this,
                getResources().getInteger(R.integer.activity_main_num_grid_columns)));
        recyclerView.setAdapter(new CardAdapter());
    }

    private class CardAdapter extends RecyclerView.Adapter<CardHolder> {
        private final LayoutInflater mInflater;

        public CardAdapter() {
            mInflater = LayoutInflater.from(MainActivity.this);
        }

        @Override
        public CardHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            return new CardHolder(mInflater.inflate(R.layout.album_image_card, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(CardHolder holder, int position) {
            holder.bind(position);
        }

        @Override
        public int getItemCount() {
            return ALBUM_IMAGE_URLS.length;
        }
    }

    private class CardHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView mAlbumImage;
        private int mAlbumPosition;

        public CardHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mAlbumImage = (ImageView) itemView.findViewById(R.id.main_card_album_image);
        }

        public void bind(int position) {
            Picasso.with(MainActivity.this).load(ALBUM_IMAGE_URLS[position]).into(mAlbumImage);
            mAlbumPosition = position;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
            intent.putExtra(EXTRA_CURRENT_ALBUM_POSITION, mAlbumPosition);
            startActivity(intent);
        }
    }
}
