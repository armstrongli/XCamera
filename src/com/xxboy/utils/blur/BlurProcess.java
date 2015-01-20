package com.xxboy.utils.blur;

import android.graphics.Bitmap;

public abstract class BlurProcess {
	/**
	 * Process the given image, blurring by the supplied radius. If radius is 0, this will return original
	 * 
	 * @param original
	 *            the bitmap to be blurred
	 * @param radius
	 *            the radius in pixels to blur the image
	 * @return the blurred version of the image.
	 */
	public abstract Bitmap blur(Bitmap original, float radius);

	public static BlurProcess build() {
		return new JavaBlurProcess();
	}
}
