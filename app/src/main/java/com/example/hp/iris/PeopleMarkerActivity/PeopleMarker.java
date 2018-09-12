package com.example.hp.iris.PeopleMarkerActivity;

import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Contacts;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.hp.iris.ImagePathAdapter;
import com.example.hp.iris.Main2Activity;
import com.example.hp.iris.MyLocationGetter;
import com.example.hp.iris.R;
import com.github.zagum.speechrecognitionview.RecognitionProgressView;
import com.github.zagum.speechrecognitionview.adapters.RecognitionListenerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Aravindh balaji on 22-03-2018.
 */

public class PeopleMarker  extends AppCompatActivity implements TextToSpeech.OnInitListener,TextToSpeech.OnUtteranceCompletedListener{


    private SpeechRecognizer speechRecognizer;
    RecognitionProgressView recognitionProgressView;
    private TextToSpeech tvvs;
    String cv ;
    CognitoCachingCredentialsProvider credentialsProvider;
    AmazonS3 s3;
    AWSCredentials credentials = new BasicAWSCredentials("AKIAJ3CRZTWIQTKUOQUQ","ATsmUAR5+k0O8oM90DzTYcbjyO3MvgGuRmXNvNJW");
    TransferUtility transferUtility;

    TextToSpeech myTTS;
    private int MY_DATA_CHECK_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_before_message);
        cv = getIntent().getExtras().getString("cv");

        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-west-2:facd4c69-88b3-4765-bf79-4e0a2562ffb6", // Identity pool ID
                Regions.US_WEST_2 // Region
        );

        s3 = new AmazonS3Client(credentials);

        Intent checkTTSIntent = new Intent();
        checkTTSIntent
                .setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
        Thread logoTimer = new Thread() {
            public void run() {
                try {
                    try {
                        sleep(1000);
                        if(cv == "1"){
                            speakWords("Please tell the Name of the person ");
                        }
                        else if(cv == "2"){
                            speakWords("Please tell the relationship of the person ");
                        }
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }



                }

                finally {
                    //finish();
                }
            }

        };
        logoTimer.start();
        tvvs=new TextToSpeech(PeopleMarker.this,PeopleMarker.this);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        int[] colors = {
                ContextCompat.getColor(this, R.color.color3),
                ContextCompat.getColor(this, R.color.color3),
                ContextCompat.getColor(this, R.color.color3),
                ContextCompat.getColor(this, R.color.color3),
                ContextCompat.getColor(this, R.color.color3),
                ContextCompat.getColor(this, R.color.color3)

        };
        int[] heights = {60, 76, 58, 80, 55};
        recognitionProgressView= (RecognitionProgressView) findViewById(R.id.recognition_view);
        recognitionProgressView.setSpeechRecognizer(speechRecognizer);
        recognitionProgressView.setRecognitionListener(new RecognitionListenerAdapter() {
            @Override
            public void onResults(Bundle results) {
                showResults(results);
            }
        });

        recognitionProgressView.setColors(colors);
        recognitionProgressView.setBarMaxHeightsInDp(heights);
        recognitionProgressView.play();
        RelativeLayout relativeLayout=(RelativeLayout)findViewById(R.id.layout);

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!tvvs.isSpeaking()){
                    HashMap<String,String> params=new HashMap<String, String>();
                    params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                    tvvs.speak("Mic is enabled you can speak",TextToSpeech.QUEUE_ADD,params);
                }
                else{
                    tvvs.stop();
                }


            }
        });
    }
    private void speakWords(String speech) {

        // speak straight away
        if(myTTS != null)
        {
            myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
    private void showResults(Bundle results) {
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        Toast.makeText(this, matches.get(0), Toast.LENGTH_LONG).show();
        String s=matches.get(0).toLowerCase().toString();
//        char a=s.charAt(0);
          if(cv.equals("1")){
              ImagePathAdapter.name=s;
              Intent intent = new Intent(PeopleMarker.this, PeopleMarker.class);
              intent.putExtra("cv","2");
              startActivity(intent);
              finish();
          }
          if(cv.equals("2")){
              ImagePathAdapter.relation=s;
              new Image_Upload_S3().execute();
          }


       // String activity=getIntent().getExtras().get("Activity").toString();
//        if(activity.equals("Message")){
//            Intent intent=new Intent(PeopleMarker.this,Message.class);
//            intent.putExtra("alphabet",a);
//            startActivity(intent);
//
//        }
//        if(activity.equals("Call")){
//            Intent intent=new Intent(PeopleMarker.this,contacts2.class);
//            intent.putExtra("alphabet",a);
//            startActivity(intent);
//        }




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // the user has the necessary data - create the TTS
                myTTS = new TextToSpeech(this, this);
            } else {
                // no data - install it now
                Intent installTTSIntent = new Intent();
                installTTSIntent
                        .setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }

    @Override
    public void onUtteranceCompleted(String utteranceId) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(PeopleMarker.this, "COMPLETED", Toast.LENGTH_SHORT).show();
                recognitionProgressView.play();

                startRecognition();

            }
        });


    }
    private void startRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en");
        speechRecognizer.startListening(intent);
    }

    @Override
    public void onInit(int status) {
        try {
            if (status == TextToSpeech.SUCCESS) {
                if (myTTS.isLanguageAvailable(Locale.US) == TextToSpeech.LANG_AVAILABLE)
                    myTTS.setLanguage(Locale.US);
            } else if (status == TextToSpeech.ERROR) {
                Toast.makeText(this, "Sorry! Text To Speech failed...",
                        Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e)
        {
            System.out.print("eroro"+e);
        }
        tvvs.setOnUtteranceCompletedListener(this);
    }
    public  boolean onKeyDown(int keyCode,KeyEvent event)
    {
        if(keyCode==KeyEvent.KEYCODE_VOLUME_UP){
            event.startTracking();



            return true;
        }
        return super.onKeyDown(keyCode,event);

    }
    public boolean onKeyLongPress(int keycode,KeyEvent event){
        if(keycode==KeyEvent.KEYCODE_VOLUME_UP){
            startActivity(new Intent(PeopleMarker.this,MyLocationGetter.class));
            return true;
        }
        return onKeyLongPress(keycode, event);
    }

    public class Image_Upload_S3 extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... strings) {
            s3.setRegion(Region.getRegion(Regions.US_WEST_2));
            String key = ImagePathAdapter.name+"_"+ImagePathAdapter.relation;
            transferUtility =new TransferUtility(s3,getBaseContext());
            ObjectMetadata metadata=new ObjectMetadata();
            Map<String, String> usermetadata= new HashMap<>();
            usermetadata.put("imgRek",key);

            metadata.setUserMetadata(usermetadata);

            TransferObserver transferObserver = transferUtility.upload("irismec",key,new File(ImagePathAdapter.path),metadata);

            transferObserver.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    System.out.println(" state changed");
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                    int percentage = (int) (bytesCurrent/bytesTotal)*100;
                    System.out.println(" onProgressChanged "+percentage);

                    if(percentage == 100){
                        startActivity(new Intent(PeopleMarker.this, Main2Activity.class));
                    }

                }

                @Override
                public void onError(int id, Exception ex) {
                    System.out.println(" ERROR "+ex);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PeopleMarker.this, "Error in uploading..", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            return null;
        }
    }
}
