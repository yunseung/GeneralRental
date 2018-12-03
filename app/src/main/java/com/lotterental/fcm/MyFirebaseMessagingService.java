package com.lotterental.fcm;

import android.os.Handler;
import android.os.Message;

import com.google.firebase.messaging.RemoteMessage;
import com.lotterental.LLog;
import com.lotterental.common.util.CommonUtils;


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

        if (remoteMessage.getNotification() != null) {

            RemoteMessage.Notification notification = remoteMessage.getNotification();
            LLog.d(TAG , "title : " + notification.getTitle());
            LLog.d(TAG , "body : " + notification.getBody());

            final StringBuffer strLog = new StringBuffer();
            strLog.append("푸쉬에서 받은 메시지\n");
            strLog.append("제목 : " + notification.getTitle() + "\n");
            strLog.append("내용 : " + notification.getBody());

            Handler handler = new Handler(getMainLooper(), new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {


                    CommonUtils.showToast(getApplicationContext() , strLog.toString());

                    return false;
                }
            });

            handler.sendEmptyMessage(0);


        }

    }

}
