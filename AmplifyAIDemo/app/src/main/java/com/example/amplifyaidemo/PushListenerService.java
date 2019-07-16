package com.example.amplifyaidemo;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.amazonaws.mobileconnectors.pinpoint.targeting.notification.NotificationClient;
import com.amazonaws.mobileconnectors.pinpoint.targeting.notification.NotificationDetails;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import com.google.firebase.messaging.FirebaseMessagingService;

import java.util.HashMap;

public class PushListenerService extends FirebaseMessagingService {

    private static final String TAG = "ppLog";

    // Intent action used in local broadcast
    public static final String ACTION_PUSH_NOTIFICATION = "push-notification";
    // Intent keys
    public static final String INTENT_SNS_NOTIFICATION_FROM = "from";
    public static final String INTENT_SNS_NOTIFICATION_DATA = "data";

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);

        Log.d(TAG, "Registering push notifications token: " + token);

        MainActivity.getPinpointManager(getApplicationContext()).getNotificationClient().registerDeviceToken(token);

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "Message: " + remoteMessage.getData());

        final NotificationClient notificationClient = MainActivity.getPinpointManager(getApplicationContext()).getNotificationClient();

        final NotificationDetails notificationDetails = NotificationDetails.builder()
                .from(remoteMessage.getFrom())
                .mapData(remoteMessage.getData())
                .intentAction(NotificationClient.FCM_INTENT_ACTION)
                .build();

        NotificationClient.CampaignPushResult pushResult = notificationClient.handleCampaignPush(notificationDetails);

        if (!NotificationClient.CampaignPushResult.NOT_HANDLED.equals(pushResult)) {

            if (NotificationClient.CampaignPushResult.APP_IN_FOREGROUND.equals(pushResult)) {
                final HashMap<String, String> dataMap = new HashMap<>(remoteMessage.getData());
                broadcast(remoteMessage.getFrom(), dataMap);
            }
            return;
        }
    }

    private void broadcast(final String from, final HashMap<String, String> dataMap) {
        Intent intent = new Intent(ACTION_PUSH_NOTIFICATION);
        intent.putExtra(INTENT_SNS_NOTIFICATION_FROM, from);
        intent.putExtra(INTENT_SNS_NOTIFICATION_DATA, dataMap);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    public static String getMessage(Bundle data) {
        return ((HashMap) data.get("data")).toString();
    }

}
