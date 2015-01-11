package com.xxboy.listeners;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.xxboy.log.Logger;
import com.xxboy.utils.XQueueUtil;
import com.xxboy.xcamera.XCamera.XCameraConst;

public class XScrollListener implements OnScrollListener {

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// change the picture after the scroll stop.
		Logger.log("Scroll state change: " + scrollState);
		switch (scrollState) {
		case SCROLL_STATE_IDLE:
			XQueueUtil.run();
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		// change loading picture and recycle picture resource.
		Logger.log("Scroll to: " + firstVisibleItem + "--" + visibleItemCount + "--" + totalItemCount);
		if (XCameraConst.GLOBAL_X_GRIDVIEW_VISIABLE_COUNT < visibleItemCount) {
			XCameraConst.GLOBAL_X_GRIDVIEW_VISIABLE_COUNT = visibleItemCount;
		}
	}
}
