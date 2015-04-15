package com.xxboy.activities.mainview.intents;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;

import com.xxboy.activities.imageview.XViewActivity;

public class XImageViewIntent extends Intent implements XIntentInterface {
	// extra value keys
	private static final String INTENT_VAR_PASITION = "INTENT_VAR_PASITION";
	private static final String INTENT_VAR_PATHES = "INTENT_VAR_PATHES";
	// image view class
	private static final Class<?> clazz = XViewActivity.class;
	// -- xIntent context
	private final Context context;
	private final ArrayList<String> resources;
	private final int position;

	public XImageViewIntent(Context packageContext, ArrayList<String> resources, int position) {
		super(packageContext, clazz);
		this.context = packageContext;
		this.resources = resources;
		this.position = position;

		// -- put extra value for xcamera intent
		putExtraValues();
	}

	private void putExtraValues() {
		this.putExtra(INTENT_VAR_PASITION, this.position);
		this.putStringArrayListExtra(INTENT_VAR_PATHES, this.resources);
	}

	public int getPosition() {
		return this.position;
	}

	public ArrayList<String> getResources() {
		return this.resources;
	}

	@Override
	public void start() {
		this.context.startActivity(this);
	}

	public static final int getPosition(Intent defaultIntent) {
		return defaultIntent.getIntExtra(INTENT_VAR_PASITION, 0);
	}

	public static final ArrayList<String> getResources(Intent defaultIntent) {
		return defaultIntent.getStringArrayListExtra(INTENT_VAR_PATHES);
	}

}
