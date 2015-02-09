package com.xxboy.utils;

import android.graphics.Color;

public class XColorUtil {
	private static final int[] COLOR_SET = {//
	//
			Color.argb(50, 206, 209, 0),//
			Color.argb(50, 164, 96, 244),//
			Color.argb(50, 238, 104, 188),//
			Color.argb(50, 128, 128, 240),//
			Color.argb(50, 105, 180, 255),//
			Color.argb(50, 48, 255, 155),//
			Color.argb(50, 185, 15, 255),//
			Color.argb(50, 204, 50, 153) //
	};

	private static int round = 0;

	private static int getBackgroundColor() {
		return COLOR_SET[((round++) % COLOR_SET.length)];
	}

	private static int getPriviousColor() {
		return COLOR_SET[round];
	}

	private static String DATE = "";

	public static int getBackgroundColor(String date) {
		if (DATE.equals(date)) {
			return getPriviousColor();
		} else {
			DATE = date;
			return getBackgroundColor();
		}
	}
}
