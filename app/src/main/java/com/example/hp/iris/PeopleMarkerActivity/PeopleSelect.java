package com.example.hp.iris.PeopleMarkerActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.hp.iris.Main2Activity;
import com.example.hp.iris.MyLocationGetter;
import com.example.hp.iris.R;

import org.w3c.dom.Text;

import java.util.HashMap;



public class PeopleSelect extends AppCompatActivity implements  TextToSpeech.OnInitListener,TextToSpeech.OnUtteranceCompletedListener{

    Handler handler=new Handler();
   CardView peopleMarker, peopleId;

    TextToSpeech tvvs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.peopleselectlayout);
        peopleMarker=(CardView) findViewById(R.id.peopleMarker);
        peopleId=(CardView) findViewById(R.id.peopleId);
        tvvs=new TextToSpeech(PeopleSelect.this,PeopleSelect.this);



        peopleMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!tvvs.isSpeaking()){
                    HashMap<String,String> params=new HashMap<String, String>();
                    params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                    tvvs.speak("People Marker Option",TextToSpeech.QUEUE_ADD,params);
                }
                else{
                    tvvs.stop();
                }

            }
        });

        peopleMarker.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(!tvvs.isSpeaking()){
                    HashMap<String,String> params=new HashMap<String, String>();
                    params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                    tvvs.speak("Entered Marker Camera",TextToSpeech.QUEUE_ADD,params);
                }
                else{
                    tvvs.stop();
                }
                Intent intent = new Intent(PeopleSelect.this,PeopleMarkerCamera.class);
                intent.putExtra("PeopleIdentifier","peopleMarker");
                startActivity(intent);
                return false;
            }
        });
       peopleId.setOnLongClickListener(new View.OnLongClickListener() {
           @Override
           public boolean onLongClick(View view) {
               if(!tvvs.isSpeaking()){
                   HashMap<String,String> params=new HashMap<String, String>();
                   params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                   tvvs.speak("Entered Identifier Camera",TextToSpeech.QUEUE_ADD,params);
               }
               else{
                   tvvs.stop();
               }
                Intent intent = new Intent(PeopleSelect.this,PeopleMarkerCamera.class);
               intent.putExtra("PeopleIdentifier","peopleIdentity");
                startActivity(intent);
               return false;
           }
       });

        peopleId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!tvvs.isSpeaking()){
                    HashMap<String,String> params=new HashMap<String, String>();
                    params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                    tvvs.speak("People Identifier Option",TextToSpeech.QUEUE_ADD,params);
                }
                else{
                    tvvs.stop();
                }

            }
        });
    }
    @Override
    protected void onDestroy() {
        if(tvvs!=null)
        {
            tvvs.stop();
            tvvs.shutdown();

            tvvs=null;
        }


        super.onDestroy();
    }

    @Override
    public void onInit(int i) {

    }

    @Override
    public void onUtteranceCompleted(String s) {

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
            startActivity(new Intent(PeopleSelect.this,MyLocationGetter.class));
            return true;
        }
        return onKeyLongPress(keycode, event);
    }
}
