package com.xxboy.activities.imageview.adapters;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xxboy.activities.mainview.XCamera;
import com.xxboy.activities.mainview.XCamera.XCameraConst;
import com.xxboy.activities.mainview.adapters.xdata.XAdapterBase;
import com.xxboy.common.XFunction;
import com.xxboy.log.Logger;
import com.xxboy.photo.R;
import com.xxboy.utils.XCacheUtil;

public class XImageViewAdapter extends BaseAdapter {

	private List<String> mData;
	private LayoutInflater mInflater;

	public XImageViewAdapter(XCamera xCamera, final List<XAdapterBase> privateData) {
		super();
		this.mData = initData(privateData);
		Logger.log("There're " + privateData.size() + " pictures in total");
		this.mInflater = (LayoutInflater) xCamera.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/**
	 * get base data from primary data, and remove the ones which aren't photos
	 * 
	 * @param mData
	 * @return
	 */
	private List<String> initData(final List<XAdapterBase> mData) {
		List<String> result = new LinkedList<String>();
		if (mData != null && mData.size() > 0) {
			Iterator<XAdapterBase> it = mData.iterator();
			while (it.hasNext()) {
				XAdapterBase item = it.next();
				result.add(item.get(XCameraConst.VIEW_NAME_IMAGE_ITEM).toString());
			}
		}
		return result;
	}

	@Override
	public int getCount() {
		return this.mData.size();
	}

	@Override
	public Object getItem(int position) {
		return this.mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Logger.debug("Loading: " + position);
		View resultView = createViewFromResource(position, convertView, parent, R.layout.ximage_item);
		return resultView;
	}

	private View createViewFromResource(int position, View convertView, ViewGroup parent, int resource) {
		View v = (convertView == null) ? this.mInflater.inflate(resource, parent, false) : convertView;
		ImageView tmpImageView = (ImageView) v.findViewById(R.id.xcamera_imageview);
		String path = this.mData.get(position);

		return v;
	}

	public void setViewText(TextView v, String text) {
		v.setText(text);
	}

	public void setViewImage(ImageView v, int value) {
		v.setImageResource(value);
	}

	public void setViewImage(int position, ImageView v, String value) {
		loadImage(position, value, v);
	}

	private void loadImage(int position, String imagePath, ImageView imageView) {
		Bitmap bitmapFromMemCache = XCacheUtil.getFromMemCache(imagePath);
		if (bitmapFromMemCache != null && !bitmapFromMemCache.isRecycled()) {
			imageView.setImageBitmap(bitmapFromMemCache);
		} else {
			imageView.setImageBitmap(null);
			if (XFunction.isImage(imagePath)) {
				// new ImageExecutor(position, imagePath, imageView).start();
			} else {
				imageView.setImageResource(R.drawable.ic_media_embed_play);
			}
		}
	}

}
