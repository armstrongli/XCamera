package com.xxboy.adapters;

import java.util.LinkedList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xxboy.common.XFunction;
import com.xxboy.log.Logger;
import com.xxboy.photo.R;
import com.xxboy.services.asynctasks.XBitmapCacheAsyncTask;
import com.xxboy.utils.XCacheUtil;
import com.xxboy.utils.XQueueUtil;
import com.xxboy.xcamera.XCamera;
import com.xxboy.xcamera.XCamera.XCameraConst;

public class XAdapter extends BaseAdapter {

	private LinkedList<XAdapterBase> mData;
	private LayoutInflater mInflater;

	public XAdapter(XCamera xCamera, LinkedList<XAdapterBase> mData) {
		super();
		this.mData = mData;
		Logger.log("There're " + mData.size() + " pictures in total");
		this.mInflater = (LayoutInflater) xCamera.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public XAdapter setData(LinkedList<XAdapterBase> data) {
		this.mData = data;
		return this;
	}

	@Override
	public int getCount() {
		return this.mData.size();
	}

	@Override
	public Object getItem(int position) {
		return this.mData.get(position);
	}

	public XAdapterBase getXItem(int position) {
		return this.mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Logger.debug("Loading: " + position);
		XQueueUtil.addMaskTask(position);
		View resultView = createViewFromResource(position, convertView, parent, this.mData.get(position).getResource());
		return resultView;
	}

	private View createViewFromResource(int position, View convertView, ViewGroup parent, int resource) {
		View v;
		if (convertView == null) {
			v = mInflater.inflate(resource, parent, false);
		} else {
			v = convertView;
		}

		final XAdapterBase dataSet = mData.get(position);
		if ((dataSet.getResource() == R.layout.xcamera_item && v.findViewById(R.id.ImageContainer) == null) || (dataSet.getResource() == R.layout.xcamera_camera && v.findViewById(R.id.id_camera_preview) == null)) {
			v = mInflater.inflate(resource, parent, false);
		}
		bindView(position, v, dataSet);

		return v;
	}

	private void bindView(int position, View view, XAdapterBase dataSet) {
		if (dataSet == null) {
			return;
		}

		int microMdf = 6;

		if (dataSet.getResource() == R.layout.xcamera_camera) {
			LinearLayout cameraContainerLinearLayout = (LinearLayout) view.findViewById(R.id.id_camera_preview);
			cameraContainerLinearLayout.getLayoutParams().height = XCameraConst.PHOTO_ITEM_HEIGHT - microMdf;
			ImageView Image = (ImageView) view.findViewById(R.id.id_camera_image);
			setViewImage(Image, R.drawable.ic_menu_camera);
		} else if (dataSet.getResource() == R.layout.xcamera_item) {
			final LinearLayout ImageContainer = (LinearLayout) view.findViewById(R.id.ImageContainer);
			final ImageView Image = (ImageView) view.findViewById(R.id.ItemImage);
			final TextView txtPath = (TextView) view.findViewById(R.id.ItemResource);
			final String path = dataSet.get(XCameraConst.VIEW_NAME_IMAGE_ITEM).toString();
			txtPath.setText(path);
			ImageContainer.getLayoutParams().height = XCameraConst.PHOTO_ITEM_HEIGHT - microMdf;
			setViewImage(position, Image, path);
		}

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
		if (bitmapFromMemCache != null) {
			imageView.setImageBitmap(bitmapFromMemCache);
		} else {
			if (XFunction.isImage(imagePath)) {
				imageView.setImageResource(R.drawable.loading);
				new XBitmapCacheAsyncTask(position, imagePath, imageView).execute();
			} else {
				imageView.setImageResource(R.drawable.ic_media_embed_play);
			}
		}
	}

}
