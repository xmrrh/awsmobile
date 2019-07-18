package com.example.amplifyaidemo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/* AUTH #1 -- */
//import com.amazonaws.mobile.client.AWSMobileClient;
//import com.amazonaws.mobile.client.Callback;
//import com.amazonaws.mobile.client.SignInUIOptions;
//import com.amazonaws.mobile.client.UserStateDetails;
/* -- AUTH #1 */

public class AuthenticationActivity extends AppCompatActivity {

    private final String TAG = "demo";
    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication_layout);
    }

    @Override
    protected void onResume() {


        super.onResume();


        /* AUTH #2 -- */
        //AWSMobileClient.getInstance().initialize(getApplicationContext(), initlisenercallback);
        /* -- AUTH #2  */

    }

    /* AUTH #3 -- */
/*
    private void showSignIn() {
        try {
            finish();
            AWSMobileClient.getInstance().showSignIn(this,
                    SignInUIOptions.builder().nextActivity(MainActivity.class).build());//, lisenercallback);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    Callback<UserStateDetails> initlisenercallback = new Callback<UserStateDetails>() {

        @Override
        public void onResult(UserStateDetails userStateDetails) {
            Log.d(TAG, "First : userStateDetails.getUserState()="+ userStateDetails.getUserState());
            switch (userStateDetails.getUserState()) {
                case SIGNED_IN:
                    //  finish();
                    Intent i = new Intent(AuthenticationActivity.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    Log.d(TAG, "startActivity --!");
                    break;
                case SIGNED_OUT:
                    showSignIn();
                    break;
                case SIGNED_OUT_USER_POOLS_TOKENS_INVALID:
                    Log.d(TAG, "need to login again");
                    break;
                case SIGNED_OUT_FEDERATED_TOKENS_INVALID:
                    Log.d(TAG, "user logged in via federation, but currently needs new tokens");
                    break;
                default:
                    AWSMobileClient.getInstance().signOut();
                    showSignIn();
                    break;
            }
        }

        @Override
        public void onError(Exception e) {
            AWSMobileClient.getInstance().initialize(getApplicationContext(), initlisenercallback);
            Log.e(TAG, e.toString());
        }
    };
*/
    /* -- AUTH #3  */

}
