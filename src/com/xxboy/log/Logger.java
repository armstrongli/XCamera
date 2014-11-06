package com.xxboy.log;

public class Logger {
	public static void log(String msg) {
		System.out.println(msg);
	}

	public static void log(Throwable e) {
		e.printStackTrace();
	}
}
