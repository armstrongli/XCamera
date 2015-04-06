package com.xxboy.utils;

import android.graphics.Color;

public class XColorUtil {
	public static final int GRASS_GREEN = Color.argb(255, 153, 204, 0);

	private static final int[] COLOR_SET = {//
	//
	// Color.argb(50, 206, 209, 0),//
	// Color.argb(50, 164, 96, 244),//
	// Color.argb(50, 238, 104, 188),//
	// Color.argb(50, 128, 128, 240),//
	// Color.argb(50, 105, 180, 255),//
	// Color.argb(50, 48, 255, 155),//
			Color.argb(50, 200, 200, 200),//
			Color.argb(50, 250, 250, 250) //
	};

	private static Object colorLock = new Object();
	private static int round = 0;
	private static String DATE = "";

	private static int getBackgroundColor() {
		return COLOR_SET[((round++) % COLOR_SET.length)];
	}

	private static int getPriviousColor() {
		return COLOR_SET[round % COLOR_SET.length];
	}

	public static int getBackgroundColor(String date) {
		synchronized (colorLock) {
			if (DATE.equals(date)) {
				return getPriviousColor();
			} else {
				DATE = date;
				return getBackgroundColor();
			}
		}
	}
}
