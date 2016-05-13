package com.lindo.collector.view;

import com.lindo.collector.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioGroup;

public class LineBreakLayout extends RadioGroup {
	private static int PAD_W = 0, PAD_V = 0; // Space between child views. PAD_V：垂直方向间距
	private int mHeight;

	public LineBreakLayout(Context context) {
		super(context);
		PAD_W = (int) this.getResources().getDimension(R.dimen.linebreaklayout_padding_width);
		PAD_V = (int) this.getResources().getDimension(R.dimen.linebreaklayout_padding_height);
	}

	public LineBreakLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		PAD_W = (int) this.getResources().getDimension(R.dimen.linebreaklayout_padding_width);
		PAD_V = (int) this.getResources().getDimension(R.dimen.linebreaklayout_padding_height);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		int count=getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).setEnabled(enabled);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		assert (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.UNSPECIFIED);
		final int width = MeasureSpec.getSize(widthMeasureSpec)
				- getPaddingLeft() - getPaddingRight();
		int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop()
				- getPaddingBottom();
		final int count = getChildCount();
		int xpos = getPaddingLeft();
		int ypos = getPaddingTop();
		int childHeightMeasureSpec;
		if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST)
			childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
					MeasureSpec.AT_MOST);
		else
			childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		mHeight = 0;
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() != GONE) {
				child.measure(
						MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST),
						childHeightMeasureSpec);
				final int childw = child.getMeasuredWidth();
				mHeight = Math.max(mHeight, child.getMeasuredHeight() + PAD_V);
				if (xpos + childw > width) {
					xpos = getPaddingLeft();
					ypos += mHeight;
				}
				xpos += childw + PAD_W;
			}
		}
		if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED) {
			height = ypos + mHeight;
		} else if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
			if (ypos + mHeight < height) {
				height = ypos + mHeight;
			}
		}
		height += 5; // Fudge to avoid clipping bottom of last row.
		setMeasuredDimension(width, height);
	} // end onMeasure()

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int width = r - l;
		int xpos = getPaddingLeft();
		int ypos = getPaddingTop();
		for (int i = 0; i < getChildCount(); i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() != GONE) {
				final int childw = child.getMeasuredWidth();
				final int childh = child.getMeasuredHeight();
				if (xpos + childw > width) {
					xpos = getPaddingLeft();
					ypos += mHeight;
				}
				child.layout(xpos, ypos, xpos + childw, ypos + childh);
				xpos += childw + PAD_W;
			}
		}
	} // end onLayout()
}