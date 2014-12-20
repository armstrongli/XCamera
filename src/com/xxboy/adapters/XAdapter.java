package com.xxboy.adapters;

import java.util.List;
import java.util.Map;

import com.xxboy.photo.R;
import com.xxboy.xcamera.XCamera.XCameraConst;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class XAdapter extends BaseAdapter {

	private List<? extends Map<String, ?>> mData;

	private int resourceElementId = R.layout.xcamera_item;

	private static final int[] mTo = { R.id.ItemImage, R.id.ImageResource };
	private static final String[] mFrom = { XCameraConst.VIEW_NAME_IMAGE_ITEM, XCameraConst.VIEW_NAME_IMAGE_RESOURCE };

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}

}
