package com.xxboy.adapters;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
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

		bindView(position, v);

		return v;
	}

	private void bindView(int position, View view) {
		final XAdapterBase dataSet = mData.get(position);
		if (dataSet == null) {
			return;
		}

		final ViewBinder binder = mViewBinder;
		final String[] from = dataSet.getMFrom();
		final int[] to = dataSet.getMTo();
		final int count = to.length;

		for (int i = 0; i < count; i++) {
			final View v = view.findViewById(to[i]);
			if (v != null) {
				final Object data = dataSet.get(from[i]);
				String text = data == null ? "" : data.toString();
				if (text == null) {
					text = "";
				}

				boolean bound = false;
				if (binder != null) {
					bound = binder.setViewValue(v, data, text);
				}

				if (!bound) {
					if (v instanceof Checkable) {
						if (data instanceof Boolean) {
							((Checkable) v).setChecked((Boolean) data);
						} else if (v instanceof TextView) {
							// Note: keep the instanceof TextView check at the
							// bottom of these
							// ifs since a lot of views are TextViews (e.g.
							// CheckBoxes).
							setViewText((TextView) v, text);
						} else {
							throw new IllegalStateException(v.getClass().getName() + " should be bound to a Boolean, not a " + (data == null ? "<unknown type>" : data.getClass()));
						}
					} else if (v instanceof TextView) {
						// Note: keep the instanceof TextView check at the
						// bottom of these
						// ifs since a lot of views are TextViews (e.g.
						// CheckBoxes).
						setViewText((TextView) v, text);
					} else if (v instanceof ImageView) {
						if (data instanceof Integer) {
							setViewImage((ImageView) v, (Integer) data);
						} else {
							setViewImage((ImageView) v, text);
						}
					} else if (v instanceof LinearLayout) {
						LinearLayout cameraContainerLinearLayout = (LinearLayout) v;
						XPreview preview = new XPreview(this.context);
						preview.setCamera((Camera) data);
						cameraContainerLinearLayout.setOnClickListener(new CallCameraListener(this.context, (Camera) data));
						cameraContainerLinearLayout.addView(preview);
					} else {
						throw new IllegalStateException(v.getClass().getName() + " is not a " + " view that can be bounds by this SimpleAdapter");
					}
				}
			}
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
		Bitmap resource = XCache.getFromMemCache(imagePath);
		if (resource == null) {
			imageView.setImageResource(R.drawable.big_load);
			new XBitmapCacheAsyncTask(imagePath, imageView, this.context).execute();
		} else {
			imageView.setImageBitmap(resource);
		}

	}
}
