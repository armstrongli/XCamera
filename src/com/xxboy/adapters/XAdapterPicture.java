package com.xxboy.adapters;

import java.util.Map;

import com.xxboy.photo.R;
import com.xxboy.xcamera.XCamera.XCameraConst;

public class XAdapterPicture extends XAdapterBase {

	private Map<String, Object> data;

	public XAdapterPicture(Map<String, Object> data) {
		super();
		this.data = data;
	}

	private static final int[] mTo = { R.id.ItemImage };
	private static final String[] mFrom = { XCameraConst.VIEW_NAME_IMAGE_ITEM };

	@Override
	public int getResource() {
		return R.layout.xcamera_item;
	}

	@Override
	public String[] getMFrom() {
		return XAdapterPicture.mFrom;
	}

	@Override
	public int[] getMTo() {
		return XAdapterPicture.mTo;
	}

	@Override
	public Object get(String key) {
		return this.data.get(key);
	}

	private void setImage(Object resource) {
		this.data.put(XCameraConst.VIEW_NAME_IMAGE_ITEM, resource);
	}

	@Override
	public void set2Default() {
		setImage(R.drawable.loading);
	}

	@Override
	public void set2Resource() {
		setImage(this.data.get(XCameraConst.VIEW_NAME_IMAGE_RESC));
	}
}
