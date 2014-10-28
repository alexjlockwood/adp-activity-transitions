package com.adp.activity.transitions;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class ObservableScrollView extends ScrollView {
    private OnScrollListener mScrollListener;

    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnScrollListener(OnScrollListener listener) {
        mScrollListener = listener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (mScrollListener != null) {
            mScrollListener.onScrolled(x - oldx, y - oldy);
        }
    }

    public interface OnScrollListener {
        void onScrolled(int dx, int dy);
    }
}