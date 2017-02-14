package com.cm.hybridmessagingplugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cm.hybridmessagingsdk.HybridMessaging;

/**
 * Created by portalski on 09/01/17.
 */

public class NotificationListenerStarterBroadcastReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(final Context context, final Intent intent) {
		if (HybridMessaging.isInitialized() == false) {
			HybridMessaging.initialize(context.getApplicationContext());
		}

		if (HybridNotificationManager.isInitialized() == false) {
			HybridNotificationManager.init();
			HybridNotificationManager.enableBackgroundModeListener();
		}
	}
}
