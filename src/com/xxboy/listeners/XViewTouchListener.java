package com.xxboy.listeners;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;

import com.xxboy.photo.R;

public class XViewTouchListener implements OnTouchListener {

	private float touchDownX; // 手指按下的X坐标
	private float touchUpX; // 手指松开的X坐标
	private ViewFlipper viewFlipper;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		this.viewFlipper = (ViewFlipper) v.findViewById(R.id.viewFlipper);
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// 取得左右滑动时手指按下的X坐标
			touchDownX = event.getX();
			return true;
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			// 取得左右滑动时手指松开的X坐标
			touchUpX = event.getX();
			// 从左往右，看前一个View
			if (touchUpX - touchDownX > 100) {
				// 显示上一屏动画
				viewFlipper.setInAnimation(AnimationUtils.loadAnimation(v.getContext(), R.anim.slide_left_in));
				viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(v.getContext(), R.anim.slide_right_out));
				// 显示上一屏的View
				viewFlipper.showPrevious();
				// 从右往左，看后一个View
			} else if (touchDownX - touchUpX > 100) {
				// 显示下一屏的动画
				viewFlipper.setInAnimation(AnimationUtils.loadAnimation(v.getContext(), R.anim.slide_right_in));
				viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(v.getContext(), R.anim.slide_left_out));
				// 显示下一屏的View
				viewFlipper.showNext();
			}
			return true;
		}
		return false;
	}

}
