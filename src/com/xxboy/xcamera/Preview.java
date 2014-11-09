package com.xxboy.xcamera;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.xxboy.log.Logger;

/**
 * Part of the content is from web.<br/>
 * You can see it here:<br/>
 * http://www.cnblogs.com/mengdd/archive/2013/04/06/3002975.html<br/>
 * The author could be the primary one.<br/>
 * Start it for studying.
 * 
 * @author Armstrong
 * 
 */
public class Preview extends SurfaceView implements SurfaceHolder.Callback {

	private SurfaceHolder mHolder;
	private Camera mCamera;
	Size mPreviewSize;
	List<Size> mSupportedPreviewSizes;

	public Preview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public Preview(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public Preview(Context context) {
		super(context);
		init();
	}

	private void init() {
		Logger.log("CameraPreview initialize");

		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);
		// deprecated setting, but required on Android versions prior to 3.0
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

	}

	public void setCamera(Camera camera) {

		mCamera = camera;
		if (mCamera != null) {
			mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
			requestLayout();
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Logger.log("surfaceCreated");
		// The Surface has been created, now tell the camera where to draw the
		// preview.
		try {
			if (null != mCamera) {
				mCamera.setPreviewDisplay(holder);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			if (null != mCamera) {
				mCamera.startPreview();
			}

		} catch (Exception e) {
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

		// If your preview can change or rotate, take care of those events here.
		// Make sure to stop the preview before resizing or reformatting it.

		if (null == mHolder.getSurface()) {
			// preview surface does not exist
			return;
		}

		// stop preview before making changes
		try {
			if (null != mCamera) {
				mCamera.stopPreview();
			}
		} catch (Exception e) {
			// ignore: tried to stop a non-existent preview
		}

		// set preview size and make any resize, rotate or
		// reformatting changes here

		if (null != mCamera) {
			Camera.Parameters parameters = mCamera.getParameters();
			parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);

			requestLayout();

			mCamera.setParameters(parameters);
			mCamera.setDisplayOrientation(90);
			Logger.log("camera set parameters successfully!: " + parameters);

		}
		// 这里可以用来设置尺寸

		// start preview with new settings
		try {
			if (null != mCamera) {

				mCamera.setPreviewDisplay(mHolder);
				mCamera.startPreview();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Logger.log("surfaceDestroyed");

		if (null != mCamera) {
			mCamera.stopPreview();
		}

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		// We purposely disregard child measurements because act as a
		// wrapper to a SurfaceView that centers the camera preview instead
		// of stretching it.
		final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
		final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
		setMeasuredDimension(width, height);

		if (mSupportedPreviewSizes != null) {
			mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
		}
	}

	private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
		final double ASPECT_TOLERANCE = 0.1;
		double targetRatio = (double) w / h;
		if (sizes == null)
			return null;

		Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;

		int targetHeight = h;

		// Try to find an size match aspect ratio and size
		for (Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
				continue;
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}

		// Cannot find the one match the aspect ratio, ignore the requirement
		if (optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		return optimalSize;
	}

}