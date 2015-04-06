package com.xxboy.activities.imageview.listeners;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.xxboy.photo.R;

public class XImageViewItemClickListener implements OnItemClickListener {

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		View bar = view.findViewById(R.id.id_bar);
		switch (bar.getVisibility()) {
		case View.GONE:
			bar.setVisibility(View.VISIBLE);
			break;
		default:
			bar.setVisibility(View.GONE);
			break;
		}
	}

}
