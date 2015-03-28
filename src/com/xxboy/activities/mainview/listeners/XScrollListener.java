package com.xxboy.activities.mainview.listeners;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.xxboy.log.Logger;
import com.xxboy.services.pool.RunnablePool;
import com.xxboy.utils.XQueueUtil;
import com.xxboy.xcamera.XCamera.XCameraConst;

public class XScrollListener implements OnScrollListener {

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// change the picture after the scroll stop.
		Logger.debug("Scroll state change: " + scrollState);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		// change loading picture and recycle picture resource.
		RunnablePool.syncRunnableIndexes(firstVisibleItem, firstVisibleItem + visibleItemCount);
		XQueueUtil.syncVisableIndexes(firstVisibleItem, firstVisibleItem + visibleItemCount);
		if (XCameraConst.GLOBAL_X_GRIDVIEW_VISIABLE_COUNT < visibleItemCount) {
			XCameraConst.GLOBAL_X_GRIDVIEW_VISIABLE_COUNT = visibleItemCount;
		}
	}
}
