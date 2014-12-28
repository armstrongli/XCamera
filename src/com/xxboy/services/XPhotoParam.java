package com.xxboy.services;

public class XPhotoParam {
	private String xCameraPath;
	private String xCachePath;

	private String defaultCameraPath;

	public XPhotoParam(String xCameraPath, String xCachePath, String defaultCameraPath) {
		super();
		this.xCameraPath = xCameraPath;
		this.xCachePath = xCachePath;
		this.defaultCameraPath = defaultCameraPath;
	}

	public String getxCameraPath() {
		return xCameraPath;
	}

	public void setxCameraPath(String xCameraPath) {
		this.xCameraPath = xCameraPath;
	}

	public String getxCachePath() {
		return xCachePath;
	}

	public void setxCachePath(String xCachePath) {
		this.xCachePath = xCachePath;
	}

	public String getDefaultCameraPath() {
		return defaultCameraPath;
	}

	public void setDefaultCameraPath(String defaultCameraPath) {
		this.defaultCameraPath = defaultCameraPath;
	}

}