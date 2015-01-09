package com.xxboy.listeners;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.xxboy.log.Logger;

public class XScrollListener implements OnScrollListener {

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// change the picture after the scroll stop.
		Logger.log("Scrolling state in scroll listener: " + scrollState);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		// change loading picture and recycle picture resource.
		Logger.log("Scroll state change and load resource in scroll listener: " + firstVisibleItem + "--" + visibleItemCount + "--" + totalItemCount);
	}
}
