package com.xxboy.activities.mainview.adapters;

import java.util.LinkedList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xxboy.activities.mainview.XCamera;
import com.xxboy.activities.mainview.XCamera.XCameraConst;
import com.xxboy.activities.mainview.adapters.xdata.XAdapterBase;
import com.xxboy.activities.mainview.adapters.xdata.XAdapterDate;
import com.xxboy.activities.mainview.asynctasks.ImageItemAsync;
import com.xxboy.common.XFunction;
import com.xxboy.log.Logger;
import com.xxboy.photo.R;
import com.xxboy.services.pool.RunnablePool;

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
		RunnablePool.syncFutureIndex(position);
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

		final XAdapterBase xData = mData.get(position);
		if (xData == null) {
			return v;
		}

		int microMdf = 6;
		switch (xData.getResource()) {
		case R.layout.xitem_camera: {
			LinearLayout cameraContainerLinearLayout = (LinearLayout) v.findViewById(R.id.id_camera_preview);
			if (cameraContainerLinearLayout == null) {
				v = mInflater.inflate(resource, parent, false);
				cameraContainerLinearLayout = (LinearLayout) v.findViewById(R.id.id_camera_preview);
			}
			cameraContainerLinearLayout.getLayoutParams().height = XCameraConst.PHOTO_ITEM_HEIGHT - microMdf;
			ImageView Image = (ImageView) v.findViewById(R.id.id_camera_image);
			setViewImage(Image, R.drawable.ic_menu_camera);
			break;
		}
		case R.layout.xitem_image: {
			LinearLayout ImageContainer = (LinearLayout) v.findViewById(R.id.ImageContainer);
			if (ImageContainer == null) {
				v = mInflater.inflate(resource, parent, false);
				ImageContainer = (LinearLayout) v.findViewById(R.id.ImageContainer);
			}
			final ImageView Image = (ImageView) v.findViewById(R.id.ItemImage);
			final TextView txtPath = (TextView) v.findViewById(R.id.ItemResource);
			final String path = xData.get(XCameraConst.VIEW_NAME_IMAGE_ITEM).toString();
			txtPath.setText(path);
			ImageContainer.getLayoutParams().height = XCameraConst.PHOTO_ITEM_HEIGHT - microMdf;
			ImageContainer.setBackgroundColor(xData.getBackgroundColor());
			setViewImage(position, Image, path);
			break;
		}
		case R.layout.xitem_date: {
			XAdapterDate xDate = (XAdapterDate) xData;
			LinearLayout xDateContainer = (LinearLayout) v.findViewById(R.id.id_item_date);
			if (xDateContainer == null) {
				v = mInflater.inflate(resource, parent, false);
				xDateContainer = (LinearLayout) v.findViewById(R.id.id_item_date);
			}
			xDateContainer.setBackgroundColor(xDate.getBackgroundColor());
			xDateContainer.getLayoutParams().height = XCameraConst.PHOTO_ITEM_HEIGHT - microMdf;
			final TextView xMonth = (TextView) v.findViewById(R.id.id_item_date_month);
			final TextView xDay = (TextView) v.findViewById(R.id.id_item_date_day);
			final TextView xYear = (TextView) v.findViewById(R.id.id_item_date_year);
			xMonth.setText(xDate.getMonth());
			xDay.setText(xDate.getDay());
			xYear.setText(xDate.getYear());
			break;
		}
		default:
			;
		}

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
		if (XFunction.isImage(imagePath)) {
			new ImageItemAsync(imagePath, imageView).execute();
		} else {
			imageView.setImageResource(R.drawable.ic_media_embed_play);
		}
	}

}
