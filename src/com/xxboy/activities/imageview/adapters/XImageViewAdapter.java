package com.xxboy.activities.imageview.adapters;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xxboy.activities.imageview.asynctasks.ImageViewAsync;
import com.xxboy.log.Logger;
import com.xxboy.photo.R;

public class XImageViewAdapter extends BaseAdapter {

	private List<String> mData;
	private LayoutInflater mInflater;

	public XImageViewAdapter(Context context, final List<String> privateData) {
		super();
		this.mData = privateData;
		Logger.log("There're " + privateData.size() + " pictures in total");
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
		final ImageView tmpImageView = (ImageView) v.findViewById(R.id.xcamera_imageview);
		final TextView barTxt = (TextView) v.findViewById(R.id.id_bar_txt);
		final LinearLayout barBg = (LinearLayout) v.findViewById(R.id.id_bar_bg);

		barBg.setBackgroundColor(Color.GREEN);
		String path = this.mData.get(position);
		barTxt.setText(path);
		tmpImageView.setImageBitmap(null);
		// set image
		new ImageViewAsync(path, tmpImageView).execute();

		return v;
	}

}
