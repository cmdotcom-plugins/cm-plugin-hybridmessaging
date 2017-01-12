package com.cm.hybridmessagingplugin;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.cm.hybridmessagingsdk.HybridMessaging;
import com.cm.hybridmessagingsdk.listener.HybridNotificationListener;
import com.cm.hybridmessagingsdk.util.Notification;
import com.cm.hybridmessagingsdk.util.NotificationUtil;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by portalski on 10/01/17.
 */

public class HybridNotificationManager implements HybridNotificationListener {
    private static HybridNotificationManager instance;
    private AtomicInteger notifIdGen;

    public static void init() {
        if (instance == null) {
            instance = new HybridNotificationManager();
            HybridMessaging.fireNotificationsByDefault(false);
        }
    }

    public static boolean isInitialized() {
        return (instance != null);
    }

    public static void enableForegroundModeListener(HybridNotificationListener externalListener) {
        HybridMessaging.setHybridNotificationListener(externalListener);
    }

    public static void enableBackgroundModeListener() {
        HybridMessaging.setHybridNotificationListener(instance);
    }


    private HybridNotificationManager() {
        notifIdGen = new AtomicInteger(0);
    }

    @Override
    public void onReceiveHybridNotification(Context context, Notification notification) {
        int notificationId = notifIdGen.incrementAndGet();

        Context applicationContext = context.getApplicationContext();

        Intent notificationIntent = applicationContext.getPackageManager().getLaunchIntentForPackage(
                applicationContext.getPackageName());

        notificationIntent.putExtra("notificationPayload", notification.getExtras());

        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                applicationContext,
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

        // gets an instance of the HybridNotificationManager service
        android.app.NotificationManager notificationManager =
                (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // show the notification
        notificationManager.notify(notificationId, notifBuilder.build());
    }
}
