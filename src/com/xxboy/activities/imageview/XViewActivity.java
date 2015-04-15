package com.xxboy.activities.imageview;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.xxboy.activities.imageview.adapters.XImageViewAdapter;
import com.xxboy.activities.imageview.listeners.XImageViewItemClickListener;
import com.xxboy.activities.imageview.views.XGallery;
import com.xxboy.activities.mainview.intents.XImageViewIntent;
import com.xxboy.photo.R;

public class XViewActivity extends Activity {

	private XGallery xGallery;
	private List<String> pathes = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ximage_view);

		this.xGallery = (XGallery) findViewById(R.id.id_gallery_image_view);

		Intent xIntent = getIntent();
		int pasition = XImageViewIntent.getPosition(xIntent);
		this.pathes = treatPath(XImageViewIntent.getResources(xIntent));

		int defaultPicture = pasition;
		if (defaultPicture < 0) {
			defaultPicture = 0;
		}

		this.xGallery.setAdapter(new XImageViewAdapter(this, this.pathes));
		this.xGallery.setOnItemClickListener(new XImageViewItemClickListener());
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

}
