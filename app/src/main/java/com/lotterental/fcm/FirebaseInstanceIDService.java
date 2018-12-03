package com.lotterental.fcm;


/**
 * Created by HKH on 2016-06-28.
 */

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.lotterental.LLog;

/**
 * 파이어베이스 푸쉬
 *
 */
public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService"; // 로그태그

    // [START refresh_token]

    /**
     * 푸쉬 토큰 갱신하기
     *
     */
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String token = FirebaseInstanceId.getInstance().getToken();
        LLog.d(TAG, "token: " + token);

    }

}
