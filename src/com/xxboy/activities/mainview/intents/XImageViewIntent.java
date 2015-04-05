package com.xxboy.activities.mainview.intents;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;

import com.xxboy.activities.imageview.XViewActivity;

public class XImageViewIntent extends Intent implements XIntentInterface {
	// extra value keys
	private static final String INTENT_VAR_PATH = "INTENT_VAR_PATH";
	private static final String INTENT_VAR_PATHES = "INTENT_VAR_PATHES";
	// image view class
	private static final Class<?> clazz = XViewActivity.class;
	// -- xIntent context
	private final Context context;
	private final ArrayList<String> resources;
	private final String choosedResource;

	public XImageViewIntent(Context packageContext, ArrayList<String> resources, String choosedResource) {
		super(packageContext, clazz);
		this.context = packageContext;
		this.resources = resources;
		this.choosedResource = choosedResource;

		// -- put extra value for xcamera intent
		putExtraValues();
	}

	private void putExtraValues() {
		this.putExtra(INTENT_VAR_PATH, this.choosedResource);
		this.putStringArrayListExtra(INTENT_VAR_PATHES, this.resources);
	}

	public String getChoosedResource() {
		return this.choosedResource;
	}

	public ArrayList<String> getResources() {
		return this.resources;
	}

	@Override
	public void start() {
		this.context.startActivity(this);
	}

}
