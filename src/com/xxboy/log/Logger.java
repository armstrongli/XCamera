package com.xxboy.log;

import java.util.logging.Level;

import com.xxboy.common.XFunction;

public class Logger {
	private static final Boolean DEBUG = false;

	public static void debug(String msg) {
		if (DEBUG)
			log(msg);
	}

	public static void log(String msg) {
		java.util.logging.Logger.getLogger("XCamera").log(Level.INFO, new XFunction.XDate().getFull() + ">>>>X-->" + msg);
	}

	public static void debug(Throwable e) {
		if (DEBUG)
			log(e);
	}

	public static void log(Throwable e) {
		java.util.logging.Logger.getLogger("XCamera").log(Level.INFO, new XFunction.XDate().getFull() + "X-->" + e.getMessage(), e);
		e.printStackTrace();
	}

	public static void debug(String logMSG, Throwable e) {
		if (DEBUG)
			log(logMSG, e);
	}

	public static void log(String logMSG, Throwable e) {
		log(logMSG);
		java.util.logging.Logger.getLogger("XCamera").log(Level.INFO, new XFunction.XDate().getFull() + "X-->" + e.getMessage(), e);
		e.printStackTrace();
	}
}
