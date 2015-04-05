package com.xxboy.activities.imageview;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView;

import com.xxboy.activities.imageview.adapters.XImageViewAdapter;
import com.xxboy.activities.imageview.asynctasks.ImageViewAsync;
import com.xxboy.activities.imageview.views.XGallery;
import com.xxboy.log.Logger;
import com.xxboy.photo.R;

public class XViewActivity extends Activity {

	private XGallery xGallery;
	private LinkedList<String> pathes = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ximage_view);

		this.xGallery = (XGallery) findViewById(R.id.id_gallery_image_view);

		String path = getIntent().getStringExtra(XViewActivity.INTENT_VAR_PATH);
		this.pathes = treatPath(getIntent().getStringArrayListExtra(INTENT_VAR_PATHES));

		int defaultPicture = this.pathes.indexOf(path);
		if (defaultPicture < 0) {
			defaultPicture = 0;
		}

		this.xGallery.setAdapter(new XImageViewAdapter(this, this.pathes));
		// this.xGallery.setAdapter(new ImageAdapter(this));
		this.xGallery.setSelection(defaultPicture);
	}

	private LinkedList<String> treatPath(ArrayList<String> pathes) {
		LinkedList<String> result = new LinkedList<String>();
		result.addAll(pathes);
		Iterator<String> it = result.iterator();
		while (it.hasNext()) {
			String item = it.next();
			if (item == null || item.trim().length() == 0) {
				it.remove();
			}
		}
		return result;
	}

	public class ImageAdapter extends BaseAdapter {

		private Context mContext;

		public ImageAdapter(Context c) {
			mContext = c;
		}

		@Override
		public int getCount() {
			return pathes.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ImageView imageView = new ImageView(mContext);

			imageView.setImageBitmap(null);
			imageView.setAdjustViewBounds(true);
			imageView.setLayoutParams(new Gallery.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			imageView.setBackgroundColor(Color.BLACK);

			new ImageViewAsync(pathes.get(position), imageView).execute();

			return imageView;
		}

	}
}
