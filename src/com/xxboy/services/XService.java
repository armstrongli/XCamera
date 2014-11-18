package com.xxboy.services;

import com.xxboy.log.Logger;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class XService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Logger.log("Service Started");
		return null;
	}

}
