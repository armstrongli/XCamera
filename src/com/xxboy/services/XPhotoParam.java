package com.xxboy.services;

public class XPhotoParam {
	private String xPath;
	private String xCachePath;

	private String cameraPath;

	public XPhotoParam(String xPath, String xCachePath, String cameraPath) {
		super();
		this.xPath = xPath;
		this.xCachePath = xCachePath;
		this.cameraPath = cameraPath;
	}

	public String getxPath() {
		return xPath;
	}

	public void setxPath(String xPath) {
		this.xPath = xPath;
	}

	public String getxCachePath() {
		return xCachePath;
	}

	public void setxCachePath(String xCachePath) {
		this.xCachePath = xCachePath;
	}

	public String getCameraPath() {
		return cameraPath;
	}

	public void setCameraPath(String cameraPath) {
		this.cameraPath = cameraPath;
	}

}