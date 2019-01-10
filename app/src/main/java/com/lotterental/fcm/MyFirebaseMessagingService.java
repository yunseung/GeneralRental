package com.lotterental.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;
import com.lotterental.generalrental.R;
import com.lotterental.generalrental.activity.MainActivity;

import java.util.Random;


/**
 * 파이어 베이스 푸쉬 메시지
 *
 * Created by n on 2017-04-25.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessagingService"; // 로그태그

    /**
     * 메시지 전달받기
     *
     * @param remoteMessage
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

//        if (remoteMessage.getNotification() != null) {
//
//            RemoteMessage.Notification notification = remoteMessage.getNotification();
//            LLog.d(TAG , "title : " + notification.getTitle());
//            LLog.d(TAG , "body : " + notification.getBody());
//
//            final StringBuffer strLog = new StringBuffer();
//            strLog.append("푸쉬에서 받은 메시지\n");
//            strLog.append("제목 : " + notification.getTitle() + "\n");
//            strLog.append("내용 : " + notification.getBody());
//
//            Handler handler = new Handler(getMainLooper(), new Handler.Callback() {
//                @Override
//                public boolean handleMessage(Message msg) {
//
//
//                    CommonUtils.showToast(getApplicationContext() , strLog.toString());
//
//                    return false;
//                }
//            });
//
//            handler.sendEmptyMessage(0);
//
//
//        }


        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        // android os Oreo 부터 notification 방식이 바뀌어서 분기해놓음.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Sets an ID for the notification, so it can be updated.
            String CHANNEL_ID = "nsok_channel_1";// The id of the channel.
            // Create a notification and set the notification channel.
            Notification notification = new Notification.Builder(this)
                    .setContentTitle(remoteMessage.getData().get("title"))
                    .setContentText(remoteMessage.getData().get("body"))
                    .setSmallIcon(R.mipmap.ic_launcher)
//                    .setColor(ContextCompat.getColor(this, R.color.statusBarColor))
                    .setChannelId(CHANNEL_ID)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .build();
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "nsok_channel", NotificationManager.IMPORTANCE_HIGH);
            channel.setShowBadge(false);
            notificationManager.createNotificationChannel(channel);
            notificationManager.notify(m, notification);
        } else {
            android.support.v4.app.NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setContentTitle(remoteMessage.getData().get("title"))
                    .setContentText(remoteMessage.getData().get("body"))
                    .setAutoCancel(true)
//                    .setColor(ContextCompat.getColor(this, R.color.statusBarColor))
                    .setLights(000000255, 500, 2000)
                    .setVibrate(new long[]{1000, 1000, 1000})
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentIntent(pendingIntent);
            notificationManager.notify(m, notificationBuilder.build());
        }

    }

}
