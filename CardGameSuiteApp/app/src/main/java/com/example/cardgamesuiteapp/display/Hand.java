package com.example.cardgamesuiteapp.display;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

public class Hand extends ViewGroup {
    public Hand(Context context) {
        super(context);
    }

    public Hand(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //idek what the fuck is supposed to go in this method
        int count = getChildCount();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        //None of this shit works
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final LayoutParams lp = (TableLayout.LayoutParams) child.getLayoutParams();
                Gravity.apply(((TableLayout.LayoutParams) lp).gravity, child.getMeasuredWidth(), child.getMeasuredHeight(), new Rect(), new Rect());
                child.layout(5, 5, 5, 5);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //Not even sure if this shit works
        super.onDraw(canvas);
        LayoutParams lp = new TableLayout.LayoutParams(75, 105, 1);
        this.addView(new Card(getContext()), lp);
        this.addView(new Card(getContext()), lp);
        this.addView(new Card(getContext()), lp);
        this.addView(new Card(getContext()), lp);
    }
}