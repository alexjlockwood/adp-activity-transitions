package com.adp.activity.transitions;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class ParallaxHeaderView extends RelativeLayout {
    private final Rect mTmpRect = new Rect();
    private int mVerticalOffset;

    public ParallaxHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void dispatchDraw(@NonNull Canvas canvas) {
        mTmpRect.set(getLeft(), getTop(), getRight(), getBottom() + mVerticalOffset);
        canvas.clipRect(mTmpRect);
        super.dispatchDraw(canvas);
    }

    public void setClipY(int offset) {
        mVerticalOffset = offset;
        invalidate();
    }
}
