package com.inventory.gcm.service;

import java.util.List;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Sender;
import com.inventory.gcm.dao.GcmUserDao;

public class GCMPushing {
	
	private static final String GOOGLE_SERVER_KEY = "AIzaSyAmGX7iU9b60MvT7AwHc6hlb3fr9Z0g11c";
	static final String MESSAGE_KEY = "message";	

	public static void main(String[] args) {
		try {
			new GCMPushing().sendPushNotification("Hai. Im testing....");
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	

	public void sendPushNotification(String msg) throws Exception {

		try {
			
			Sender sender = new Sender(GOOGLE_SERVER_KEY);
			List devicesList = new GcmUserDao().getAllUsers();
			if(devicesList!=null&&devicesList.size()>0) {
				Message message = new Message.Builder().collapseKey("1")
						.timeToLive(3).delayWhileIdle(true)
						.addData(MESSAGE_KEY, msg).build();
				MulticastResult result = sender.send(message, devicesList, 1);
				sender.send(message, devicesList, 1);
				
				System.out.println(result.toString());
				if (result.getResults() != null) {
					int canonicalRegId = result.getCanonicalIds();
					if (canonicalRegId != 0) {
					}
				} else {
					int error = result.getFailure();
					System.out.println(error);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
			// TODO: handle exception
		}
	}
}
