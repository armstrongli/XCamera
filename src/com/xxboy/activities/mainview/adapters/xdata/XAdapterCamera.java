package com.xxboy.activities.mainview.adapters.xdata;

import java.util.Map;

import com.xxboy.photo.R;
import com.xxboy.utils.XColorUtil;
import com.xxboy.xcamera.XCamera.XCameraConst;

public class XAdapterCamera extends XAdapterBase {
	private Map<String, ?> data;

	public XAdapterCamera(Map<String, ?> data) {
		super();
		this.data = data;
	}

	private static final int[] mTo = { R.id.id_camera_preview };
	private static final String[] mFrom = { XCameraConst.VIEW_NAME_CAMERA_ID };

	@Override
	public int getResource() {
		return R.layout.xitem_camera;
	}

	@Override
	public String[] getMFrom() {
		return XAdapterCamera.mFrom;
	}

	@Override
	public int[] getMTo() {
		return XAdapterCamera.mTo;
	}

	@Override
	public Object get(String key) {
		return this.data.get(key);
	}

	@Override
	public void set2Resource() {
		throw new RuntimeException("Camera adapter doesn't support this function");
	}

	@Override
	public void set2Default() {
		throw new RuntimeException("Camera adapter doesn't support this function");
	}

	@Override
	public int getBackgroundColor() {
		return XColorUtil.getBackgroundColor("");
	}

}
