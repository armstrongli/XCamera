package com.xxboy.adapters;

import java.util.Map;

import com.xxboy.photo.R;
import com.xxboy.xcamera.XCamera.XCameraConst;

public class XAdapterPicture extends XAdapterBase {

	private Map<String, ?> data;

	public XAdapterPicture(Map<String, ?> data) {
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

}
