package com.example.amplifyaidemo;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
/*  POLLY #100 --*/
//import com.amazonaws.services.polly.AmazonPollyPresigningClient;
//import com.amazonaws.services.polly.model.OutputFormat;
//import com.amazonaws.services.polly.model.SynthesizeSpeechPresignRequest;
/* -- POLLY #100 */
import java.io.IOException;
import java.net.URL;

public class TTSPlayer {
    private static final String TAG = "Demo";

    private MediaPlayer mediaPlayer;
    private URL presignedSynthesizeSpeechUrl;

    public URL getURL(){
        return presignedSynthesizeSpeechUrl;
    }

    public TTSPlayer() {
        setupNewMediaPlayer();
    }
    /*  POLLY #100 --*/
   /*
   public void playVoice(AmazonPollyPresigningClient client, String voicesId, String textToRead) {
        try {
            SynthesizeSpeechPresignRequest synthesizeSpeechPresignRequest =
                    new SynthesizeSpeechPresignRequest()
                            // Set text to synthesize.
                            .withText(textToRead)
                            // Set voice selected by the user.
                            .withVoiceId(voicesId)
                            // Set format to MP3.
                            .withOutputFormat(OutputFormat.Mp3);

            presignedSynthesizeSpeechUrl =
                    client.getPresignedSynthesizeSpeechUrl(synthesizeSpeechPresignRequest);

            Log.i(TAG, "Playing speech from presigned URL: " + presignedSynthesizeSpeechUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }

        doPlay(presignedSynthesizeSpeechUrl);
    }
    */
    /* -- POLLY #100 */
    public void doPlay(URL url)
    {
        if (mediaPlayer.isPlaying()) {
            setupNewMediaPlayer();
        }
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mediaPlayer.setDataSource(url.toString());
        } catch (Exception e) {
            Log.e(TAG, "Unable to set data source for the media player! " + e.getMessage());
        }

        mediaPlayer.prepareAsync();
    }
    private void setupNewMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
                setupNewMediaPlayer();
            }
        });
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });
    }
}
