package com.xxboy.view;

import com.xxboy.log.Logger;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.GridView;

public class XView extends GridView {

	public XView(Context context) {
		super(context);
	}

	public XView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public XView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

	@Override
	public void setOnScrollListener(OnScrollListener l) {
		// TODO Auto-generated method stub
		super.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// change the picture after the scroll stop.
				Logger.log("Scrolling state: " + scrollState);
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// change loading picture and recycle picture resource.
				Logger.log("Scroll state change and load resource: " + firstVisibleItem + "--" + visibleItemCount + "--" + totalItemCount);
			}
		});
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		// TODO Auto-generated method stub
		super.setOnClickListener(l);
	}

}
