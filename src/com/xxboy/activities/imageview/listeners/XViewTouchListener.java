package com.xxboy.activities.imageview.listeners;

import java.util.ArrayList;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.xxboy.log.Logger;
import com.xxboy.photo.R;
import com.xxboy.services.asynctasks.XLoadImageViewFlipperAsyncTask;

public class XViewTouchListener implements OnTouchListener {

	private float touchDownX;
	private float touchUpX;
	private ViewFlipper viewFlipper;

	private int counter = 0;// counter, used to identify the current image view
	private int currentIndex = 0;// current index of images
	private ArrayList<String> pathes = null;

	private boolean canFlip = false;

	private boolean isFirst() {
		return this.currentIndex <= 0;
	}

	private boolean isLast() {
		return this.currentIndex >= this.pathes.size() - 1;
	}

	public XViewTouchListener(int currentIndex, ArrayList<String> pathes) {
		this.pathes = pathes;
		this.currentIndex = currentIndex;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		this.viewFlipper = (ViewFlipper) v.findViewById(R.id.viewFlipper);
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			touchDownX = event.getX();
			return true;
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			touchUpX = event.getX();
			if (touchUpX - touchDownX > 100) {
				viewFlipper.setInAnimation(AnimationUtils.loadAnimation(v.getContext(), R.anim.slide_left_in));
				viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(v.getContext(), R.anim.slide_right_out));
				if (!this.isFirst()) {
					this.currentIndex--;
					this.counter++;
					canFlip = true;
				} else {
					canFlip = false;
				}
			} else if (touchDownX - touchUpX > 100) {
				viewFlipper.setInAnimation(AnimationUtils.loadAnimation(v.getContext(), R.anim.slide_right_in));
				viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(v.getContext(), R.anim.slide_left_out));
				if (!this.isLast()) {
					this.currentIndex++;
					this.counter++;
					canFlip = true;
				} else {
					canFlip = false;
				}
			}

			if (canFlip) {
				Logger.log("Loading image:" + currentIndex + "/" + this.pathes.get(currentIndex));
				ImageView imageView = this.counter % 2 == 0 ? (ImageView) this.viewFlipper.findViewById(R.id.xcamera_imageview) : (ImageView) this.viewFlipper.findViewById(R.id.xcamera_imageview1);
				new XLoadImageViewFlipperAsyncTask(this.pathes.get(this.currentIndex), this.viewFlipper, imageView).execute();
			}

			return true;
		}
		return false;
	}
}
