package com.example.amplifyaidemo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.config.AWSConfiguration;
import java.util.ArrayList;
import java.util.List;

/* TRANSLATE #100 --*/
//import com.amazonaws.services.translate.AmazonTranslateAsyncClient;
//import com.amazonaws.services.translate.model.TranslateTextRequest;
//import com.amazonaws.services.translate.model.TranslateTextResult;
/* -- TRANSLATE #100 */

/*  POLLY #100 --*/
//import com.amazonaws.services.polly.AmazonPollyPresigningClient;
//import com.amazonaws.services.polly.model.DescribeVoicesRequest;
//import com.amazonaws.services.polly.model.DescribeVoicesResult;
//import com.amazonaws.services.polly.model.Voice;
/* -- POLLY #100 */


/* API #100 -- */
/*
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.amazonaws.mobileconnectors.appsync.sigv4.CognitoUserPoolsAuthProvider;
import com.amazonaws.amplify.generated.graphql.CreateTranslatedHistoryMutation;
import com.amazonaws.amplify.generated.graphql.DeleteTranslatedHistoryMutation;
import com.amazonaws.amplify.generated.graphql.ListTranslatedHistorysQuery;
import javax.annotation.Nonnull;
import type.CreateTranslatedHistoryInput;
import type.DeleteTranslatedHistoryInput;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
*/
/* API #100 -- */

/* NOTIFICATIONS #100 --*/
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.firebase.iid.InstanceIdResult;
//import com.amazonaws.mobileconnectors.pinpoint.PinpointConfiguration;
//import com.amazonaws.mobileconnectors.pinpoint.PinpointManager;
/* -- NOTIFICATIONS #100 */



public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Demo";
    final int PERMISSION = 1;

    private EditText yousaid;
    private EditText translatedsentences = null;
    private Button sttBtn;
    private RecyclerView recyclerView;

    private SpeechRecognizer mRecognizer;
    private boolean mBoolVoiceRecoStarted;
    private String arsCountryCode = "ko-KR", translateCountryCode = "en-US";
    private String targetResult,srcResult;

    private AWSCredentials awsCredentials = null;
    private AWSCredentialsProvider awsCredentialsProvider = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sttBtn = findViewById(R.id.sttBtn);
        yousaid = findViewById(R.id.yousaid);
        translatedsentences = findViewById(R.id.translatedsentences);

        recyclerView = findViewById(R.id.history_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        /* API #100 -- */
        //mAdapter = new HistoryAdapter(getApplicationContext());
        //recyclerView.setAdapter(mAdapter);
        /* -- API #100  */

        checkPermission();
        setSpinnerSrcLanguage();
        setSpinnerDestinationLanguage();
        setButtonAndEvent();

        /* TRANSLATE #100 --*/

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    awsCredentials = AWSMobileClient.getInstance().getAWSCredentials();
                    awsCredentialsProvider = new CognitoCachingCredentialsProvider(getApplicationContext(), AWSMobileClient.getInstance().getConfiguration());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        /* -- TRANSLATE #101 */

        /* API #100 -- */
        //appSyncInit();
        //query();
        /* -- API #100 */

        /* NOTIFICATIONS #100 --*/
        //getPinpointManager(getApplicationContext());
        /* -- NOTIFICATIONS #100 */
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.actionbarmenu, menu);


        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:

                AWSMobileClient.getInstance().signOut();
                Intent refresh = new Intent(this, AuthenticationActivity.class);
                refresh.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(refresh);

                return true;

            case R.id.action_historydelete:
                //deleteHistory();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO}, 1);
        }
    }

    private void setButtonAndEvent() {
        Button sttBtn = findViewById(R.id.sttBtn);
        sttBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doASRStart();
            }

        });
    }

    private void doASRStart() {

        if (mBoolVoiceRecoStarted) {
            doASRStop();
            return;
        }

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getApplicationContext().getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, arsCountryCode);
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

        mRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        mRecognizer.setRecognitionListener(listener);
        mRecognizer.startListening(intent);
        mBoolVoiceRecoStarted = true;
    }

    private void doASRStop() {
        try {
            if (mRecognizer != null && mBoolVoiceRecoStarted) {
                mRecognizer.stopListening();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        mBoolVoiceRecoStarted = false;
    }

    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
            sttBtn.setText(getString(R.string.start));
        }

        @Override
        public void onBeginningOfSpeech() {
            sttBtn.setText(getString(R.string.listen));
        }

        @Override
        public void onRmsChanged(float rmsdB) {
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
        }

        @Override
        public void onEndOfSpeech() {
            sttBtn.setText(getString(R.string.say));
        }

        @Override
        public void onError(int error) {
            String message;

            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "ERROR_AUDIO";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "ERROR_CLIENT";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "ERROR_INSUFFICIENT_PERMISSIONS";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "ERROR_NETWORK";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "ERROR_NETWORK_TIMEOUT";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "ERROR_NO_MATCH";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "ERROR_RECOGNIZER_BUSY";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "ERROR_SERVER";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "ERROR_SPEECH_TIMEOUT";
                    break;
                default:
                    message = "ERROR";
                    break;
            }
            Toast.makeText(getApplicationContext(), " Error code :  " + message, Toast.LENGTH_SHORT).show();
            sttBtn.setText(getString(R.string.say));
            mBoolVoiceRecoStarted = false;
        }

        @Override
        public void onPartialResults(Bundle partialResults) {


            ArrayList<String> results = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            Log.e(TAG, "results = " + results.toString());
            yousaid.setText(results.get(0));
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
        }

        @Override
        public void onResults(Bundle results) {
            ArrayList<String> matches =
                    results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            srcResult = matches.get(0);
            yousaid.setText(srcResult);
            sttBtn.setText(getString(R.string.say));
            mBoolVoiceRecoStarted = false;

            /* TRANSLATE #100 --*/
            //doTranslate(srcResult);
            /* -- TRANSLATE #100 */

        }
    };


    private void setSpinnerDestinationLanguage() {
        // select box - language
        Spinner translatedlanguage = findViewById(R.id.translatedlanguage);
        translatedlanguage.setSelection(7);//english
        translatedlanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                translateCountryCode = Util.convertCountryCode(i);
                Log.e(TAG, "selected desc cc = " + translateCountryCode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void setSpinnerSrcLanguage() {

        //UI components
        Spinner srclanguage = findViewById(R.id.srclanguage);
        srclanguage.setSelection(4);//korean
        srclanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                arsCountryCode = Util.convertCountryCode(i);
                Log.e(TAG, "selected src cc = " + arsCountryCode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


    }


    /* TRANSLATE #101 --*/
    /*
    private void doTranslate(String txt) {

        AmazonTranslateAsyncClient translateAsyncClient = new AmazonTranslateAsyncClient(awsCredentials);
        TranslateTextRequest translateTextRequest = new TranslateTextRequest()
                .withText(txt)
                .withSourceLanguageCode(Util.convertCountryCodeToTranslate(arsCountryCode))
                .withTargetLanguageCode(Util.convertCountryCodeToTranslate(translateCountryCode));
        // .withTargetLanguageCode("zh");

        translateAsyncClient.translateTextAsync(translateTextRequest, new AsyncHandler<TranslateTextRequest, TranslateTextResult>() {
            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Error occurred in translating the text: " + e.getLocalizedMessage());
                targetResult = " Unsupported language pair  !! try to change target Language !!";
                runOnUiThread(mRunnable);
            }

            @Override
            public void onSuccess(TranslateTextRequest request, TranslateTextResult translateTextResult) {
                Log.d(TAG, "Original Text: " + request.getText());
                Log.d(TAG, "Translated Text: " + translateTextResult.getTranslatedText());
                targetResult = translateTextResult.getTranslatedText();
                runOnUiThread(mRunnable);

            }
        });
    }
    */
    /* -- TRANSLATE #101 */

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            translatedsentences.setText(targetResult);
            /*  POLLY #100 --*/
            //doTTS(targetResult);
            /*  -- POLLY #100*/

        }
    };

    /*  POLLY #101 --*/
/*
    private AmazonPollyPresigningClient client;
    private TTSPlayer tts;

    void doTTS(String txt) {
        final String ttsTxt = txt;
        client = new AmazonPollyPresigningClient(awsCredentialsProvider);
        Log.d(TAG, "onResult: Created polly pre-signing client = " + client);

        new Thread(new Runnable() {
            @Override
            public void run() {
                DescribeVoicesRequest describeVoicesRequest = new DescribeVoicesRequest();
                String ttsvoice = "";
                try {
                    DescribeVoicesResult describeVoicesResult = client.describeVoices(describeVoicesRequest);
                    List<Voice> voices = describeVoicesResult.getVoices();
                    for (int i = 0; i < voices.size(); i++) {
                        if (voices.get(i) != null && voices.get(i).getLanguageCode() != null &&
                                voices.get(i).getLanguageCode().equals(translateCountryCode)) {
                            ttsvoice = voices.get(i).getId();
                            Log.e(TAG, "*******Available Polly voices: " + ttsvoice);
                            break;
                        }
                    }
                } catch (RuntimeException e) {
                    Log.e(TAG, "*******Unable to get available voices.", e);
                }
                tts = new TTSPlayer();
                tts.playVoice(client, ttsvoice, ttsTxt);
                // API #100 --
                //addToHistory();
                // -- API #100
            }
        }).start();

    }
    */
    /*  -- POLLY #101*/

    /* API #100 -- */
    /*
    private AWSAppSyncClient mAWSAppSyncClient;
    private ArrayList<ListTranslatedHistorysQuery.Item> mHistorys;
    private HistoryAdapter mAdapter;

    private void appSyncInit() {
        mAWSAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .cognitoUserPoolsAuthProvider(new CognitoUserPoolsAuthProvider() {
                    @Override
                    public String getLatestAuthToken() {
                        try {

                            return AWSMobileClient.getInstance().getTokens().getIdToken().getTokenString();
                        } catch (Exception e) {
                            Log.e("APPSYNC_ERROR", e.getLocalizedMessage());
                            return e.getLocalizedMessage();
                        }
                    }
                }).build();
    }

    public void query() {
        mAWSAppSyncClient.query(ListTranslatedHistorysQuery.builder().build())
                .responseFetcher(AppSyncResponseFetchers.CACHE_AND_NETWORK)
                .enqueue(historyCallback);
    }

    private GraphQLCall.Callback<ListTranslatedHistorysQuery.Data> historyCallback = new GraphQLCall.Callback<ListTranslatedHistorysQuery.Data>() {
        @Override
        public void onResponse(@Nonnull Response<ListTranslatedHistorysQuery.Data> response) {
            Log.e(TAG, response.data().listTranslatedHistorys().items().toString());
            mHistorys = new ArrayList<>(response.data().listTranslatedHistorys().items());

            Log.i(TAG, "Retrieved list items: " + mHistorys.toString());

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.setItems(mHistorys);
                    mAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e(TAG, e.toString());
        }
    };


    private void addToHistory()
    {
        CreateTranslatedHistoryInput input = CreateTranslatedHistoryInput.builder()
                .id(Util.getUUID())
                .user(Util.getUserName())
                .src(srcResult)
                .target(targetResult)
                .link(tts.getURL().toString())
                .build();

        CreateTranslatedHistoryMutation addMutation = CreateTranslatedHistoryMutation.builder()
                .input(input)
                .build();
        mAWSAppSyncClient.mutate(addMutation).enqueue(addhistoryCallback);
    }

    private GraphQLCall.Callback<CreateTranslatedHistoryMutation.Data> addhistoryCallback = new GraphQLCall.Callback<CreateTranslatedHistoryMutation.Data>() {
        @Override
        public void onResponse(@Nonnull final Response<CreateTranslatedHistoryMutation.Data> response) {
            Log.e(TAG, "add item to history : onResponse");
            query();
        }

        @Override
        public void onFailure(@Nonnull final ApolloException e) {

            Log.e(TAG, "add item to history : failure");
        }
    };

    //private List<ListDueProductsQuery.Item> mData = new ArrayList<>();
    private void deleteHistory() {

        DeleteTranslatedHistoryInput input = DeleteTranslatedHistoryInput.builder()
                .build();

        DeleteTranslatedHistoryMutation deleteMutation = DeleteTranslatedHistoryMutation.builder()
                .input(input)
                .build();
        mAWSAppSyncClient.mutate(deleteMutation).enqueue(deletemutateCallback);


    }

   // Mutation callback code
    private GraphQLCall.Callback<DeleteTranslatedHistoryMutation.Data> deletemutateCallback = new GraphQLCall.Callback<DeleteTranslatedHistoryMutation.Data>() {
        @Override
        public void onResponse(@Nonnull final Response<DeleteTranslatedHistoryMutation.Data> response) {

            query();

        }

        @Override
        public void onFailure(@Nonnull final ApolloException e) {
        }
    };
    */
    /* -- API #100 */

    /* NOTIFICATIONS #100 --*/
    /*
    private static PinpointManager pinpointManager;

    public static PinpointManager getPinpointManager(final Context applicationContext) {
        if (pinpointManager == null) {
            final AWSConfiguration awsConfig = new AWSConfiguration(applicationContext);
            AWSMobileClient.getInstance().initialize(applicationContext, awsConfig, new Callback<UserStateDetails>() {
                @Override
                public void onResult(UserStateDetails result) {
                }

                @Override
                public void onError(Exception e) {

                }
            });

            PinpointConfiguration pinpointConfig = new PinpointConfiguration(
                    applicationContext,
                    AWSMobileClient.getInstance(),
                    awsConfig);

            pinpointManager = new PinpointManager(pinpointConfig);

            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            final String token = task.getResult().getToken();
                            Log.d(TAG, "Registering push notifications token: " + token);
                            pinpointManager.getNotificationClient().registerDeviceToken(token);
                        }
                    });
        }
        return pinpointManager;
    }
    */
    /* -- NOTIFICATIONS #100 */
}
