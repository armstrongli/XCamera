package com.xxboy.adapters;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

import com.xxboy.common.XCache;
import com.xxboy.listeners.CallCameraListener;
import com.xxboy.log.Logger;
import com.xxboy.photo.R;
import com.xxboy.services.asynctasks.XBitmapCacheAsyncTask;
import com.xxboy.view.XPreview;
import com.xxboy.xcamera.XCamera;
import com.xxboy.xcamera.XCamera.XCameraConst;

public class XAdapter extends BaseAdapter {

	private List<XAdapterBase> mData;
	private LayoutInflater mInflater;
	private ViewBinder mViewBinder;
	private XCamera context;

	public XAdapter(XCamera context, List<XAdapterBase> mData) {
		super();
		this.context = context;
		this.mData = mData;

		Logger.log("There're " + mData.size() + " pictures in total");

		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public XAdapter setData(List<XAdapterBase> data) {
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
		Logger.log("Loading: " + position);
		return createViewFromResource(position, convertView, parent, this.mData.get(position).getResource());
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

		if (dataSet.getResource() == R.layout.xcamera_camera) {
			Logger.log("Setting camera");
			Camera data = (Camera) dataSet.get(XCameraConst.VIEW_NAME_CAMERA_ID);
			LinearLayout cameraContainerLinearLayout = (LinearLayout) view.findViewById(R.id.id_camera_preview);
			XPreview preview = new XPreview(this.context);
			preview.setCamera(data);
			cameraContainerLinearLayout.getLayoutParams().height = XCameraConst.PHOTO_ITEM_HEIGHT;
			cameraContainerLinearLayout.setOnClickListener(new CallCameraListener(this.context, data));
			cameraContainerLinearLayout.addView(preview);
		} else if (dataSet.getResource() == R.layout.xcamera_item) {
			final LinearLayout ImageContainer = (LinearLayout) view.findViewById(R.id.ImageContainer);
			final ImageView Image = (ImageView) view.findViewById(R.id.ItemImage);

			ImageContainer.getLayoutParams().height = XCameraConst.PHOTO_ITEM_HEIGHT;
			setViewImage(Image, dataSet.get(XCameraConst.VIEW_NAME_IMAGE_ITEM).toString());
		}

	}

	public void setViewText(TextView v, String text) {
		v.setText(text);
	}

	public void setViewImage(ImageView v, int value) {
		v.setImageResource(value);
	}

	public void setViewImage(ImageView v, String value) {
		loadImage(value, v);
	}

	private void loadImage(String imagePath, ImageView imageView) {
		imageView.setImageResource(R.drawable.big_load);
		new XBitmapCacheAsyncTask(imagePath, imageView, this.context).execute();
	}
}
