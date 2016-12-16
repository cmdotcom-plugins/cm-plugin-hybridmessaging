package com.cm.hybridmessagingplugin;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.app.PendingIntent;
import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;

import com.cm.hybridmessagingsdk.HybridMessaging;
import com.cm.hybridmessagingsdk.listener.HybridNotificationListener;
import com.cm.hybridmessagingsdk.util.Notification;
import com.cm.hybridmessagingsdk.util.NotificationUtil;

import java.util.concurrent.atomic.AtomicInteger;

public class HybridMessagingApplication extends Application implements HybridNotificationListener {
	private AtomicInteger notifIdGen;

	@Override
	public void onCreate() {
		super.onCreate();
		// initialize the Hybrid Messaging SDK
		HybridMessaging.initialize(this);

		HybridMessaging.fireNotificationsByDefault(false);
		HybridMessaging.setHybridNotificationListener(this);
		notifIdGen = new AtomicInteger();
	}

	@Override
	public void onReceiveHybridNotification(Context context, Notification notification) {
		int notificationId = notifIdGen.incrementAndGet();

		Context baseContext = getBaseContext();;

		Intent notificationIntent = baseContext.getPackageManager().getLaunchIntentForPackage(
		baseContext.getPackageName());

		notificationIntent.putExtra("notificationPayload", notification.getExtras());

		PendingIntent resultPendingIntent = PendingIntent.getActivity(
			baseContext,
			notificationId,
			notificationIntent,
			PendingIntent.FLAG_UPDATE_CURRENT
		);

		NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context)
			.setContentTitle(NotificationUtil.getInstance().getNotificationTitle())
			.setSmallIcon(NotificationUtil.getInstance().getNotificationIcon())
			.setContentIntent(resultPendingIntent)
			.setContentText(notification.getMessage())
			.setAutoCancel(true);

			// gets an instance of the NotificationManager service
			NotificationManager notificationManager =
			(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

			// show the notification
			notificationManager.notify(notificationId, notifBuilder.build());
	}
}
