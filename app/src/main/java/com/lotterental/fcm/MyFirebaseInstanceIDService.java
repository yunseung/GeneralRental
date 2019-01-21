package com.lotterental.fcm;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.lotterental.LLog;
import com.lotterental.generalrental.util.preferences.LPreferences;


/**
 * 파이어베이스 푸쉬
 *
 * Created by n on 2017-04-25.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "TAG"; // 로그태그

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        if (refreshedToken.isEmpty()) {
            for (int i = 0; i < 10; i++) {
                refreshedToken = FirebaseInstanceId.getInstance().getToken();
                if (!refreshedToken.isEmpty()) {
                    break;
                }
            }
        }

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
        LPreferences.setToken(getApplicationContext(), refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        Log.d(TAG , "sendRegistrationToServer : " + token);
    }
}
