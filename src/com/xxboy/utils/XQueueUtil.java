package com.xxboy.utils;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class XQueueUtil {
	BlockingDeque<Runnable> rQueue = new LinkedBlockingDeque<Runnable>();
	
}
