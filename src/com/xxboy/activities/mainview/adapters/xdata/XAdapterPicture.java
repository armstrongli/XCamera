package com.xxboy.activities.mainview.adapters.xdata;

import java.util.Map;

import com.xxboy.activities.mainview.XCamera.XCameraConst;
import com.xxboy.photo.R;

public class XAdapterPicture extends XAdapterBase {

	private Map<String, Object> data;
	private int aRGB;

	public XAdapterPicture(Map<String, Object> data, int aRGB) {
		super();
		this.data = data;
		this.aRGB = aRGB;
	}

	private static final int[] mTo = { R.id.ItemImage };
	private static final String[] mFrom = { XCameraConst.VIEW_NAME_IMAGE_ITEM };

	@Override
	public int getResource() {
		return R.layout.xitem_image;
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

	@Override
	public int getBackgroundColor() {
		return this.aRGB;
	}
}
