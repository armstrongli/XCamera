package com.xxboy.activities.imageview;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.xxboy.activities.imageview.asynctasks.ImageViewAsync;
import com.xxboy.activities.imageview.listeners.XViewTouchListener;
import com.xxboy.log.Logger;
import com.xxboy.photo.R;

public class XViewActivity extends Activity {

	public static final String INTENT_VAR_PATH = "INTENT_VAR_PATH";
	public static final String INTENT_VAR_PATHES = "INTENT_VAR_PATHES";

	private Gallery xGallery;
	private ViewFlipper viewFlipper;
	private ArrayList<String> pathes = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ximage_view);

		this.xGallery = (Gallery) findViewById(R.id.id_gallery_image_view);

		String path = getIntent().getStringExtra(XViewActivity.INTENT_VAR_PATH);
		this.pathes = getIntent().getStringArrayListExtra(INTENT_VAR_PATHES);

		this.xGallery.setAdapter(new ImageAdapter(this));
	}

	private boolean setImage(int imageviewResId, String imagePath) {
		ImageView imageview = (ImageView) findViewById(imageviewResId);
		if (imageview == null) {
			return false;
		}
		new ImageViewAsync(imagePath, imageview).execute();
		return true;
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
			ImageView i = new ImageView(mContext);

			i.setImageResource(R.drawable.ic_media_embed_play);
			i.setAdjustViewBounds(true);
			i.setLayoutParams(new Gallery.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			i.setBackgroundResource(android.R.drawable.picture_frame);

			new ImageViewAsync(pathes.get(position), i).execute();

			return i;
		}

	}
}
