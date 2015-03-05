package com.xxboy.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class XView extends GridView {

	public XView(Context context) {
		super(context);
	}

	public XView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public XView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
	}

}
