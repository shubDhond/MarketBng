package com.marketbng.marketbng;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.interaxon.libmuse.Muse;
import com.interaxon.libmuse.MuseDataPacketType;
import com.interaxon.libmuse.MuseManager;
import com.interaxon.libmuse.MusePreset;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SurveyActivity extends Activity {
    ImageView image;
    Button next;
    String [] urls = new String[5];
    int currentImage = 0;
    Muse muse;
    DataListener dataListener;
    Double [] avgs = new Double[6];
    ParseObject survey;

    public SurveyActivity() {
        dataListener = new DataListener();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        image = (ImageView) findViewById(R.id.survey_image);
        next = (Button) findViewById(R.id.next);
        next.setEnabled(false);

        muse = (Muse)MuseManager.getPairedMuses().get(0);

        Intent intent = getIntent();

        avgs[0] = (Double)intent.getDoubleExtra("baseAvg",0);

        String surveyId = (String)intent.getCharSequenceExtra("surveyId");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("surveys");
        query.whereEqualTo("objectId", surveyId);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    // row of Object Id "U8mCwTHOaC"
                    survey = objects.get(0);
                    survey.put("c"+(0),avgs[0]);
                    survey.saveInBackground();
                    for (int i = 0; i < 5; i++) {
                        urls[i] = survey.getString("file_url" + (i + 1));
                    }
                    startMonitoring();
                } else {
                    // error
                    Toast.makeText(SurveyActivity.this, "Failed to load survey.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_survey, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void cancelSurvey(View view){
        Intent intent  = new Intent(this, SelectSurveyActivity.class);
        startActivity(intent);
    }

    public void nextImage(View view) {
        if(currentImage == 4){
            //survey.addAll("sub1", Arrays.asList(avgs));
            //survey.saveInBackground();
            Intent intent = new Intent(this, SelectSurveyActivity.class);
            startActivity(intent);
        }else {
            currentImage++;
            next.setEnabled(false);
            startMonitoring();
        }
    }

    public void startMonitoring(){
        Picasso.with(SurveyActivity.this).load(urls[currentImage]).into(image);
        try {
                /* Here is where we do the logic to set up */
            configureLibrary();
            muse.runAsynchronously();

            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    SurveyActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            next.setEnabled(true);

                        }
                    });
                }
            }).start();
            muse.disconnect(true);
            dataListener.setAvg(0, (dataListener.getInitialSum() / dataListener.getSampleCounter()));
            avgs[currentImage+1] = (dataListener.getInitialSum() / dataListener.getSampleCounter());
            survey.put("c"+(currentImage+1),avgs[currentImage+1]);
            Toast.makeText(this, ""+dataListener.getInitialSum()+":"+dataListener.getSampleCounter(), Toast.LENGTH_SHORT).show();
            survey.saveInBackground();
            dataListener.setinitialSum(0);
            dataListener.setSampleCounter(0);
        } catch (Exception e) {
            Log.e("Muse Headband", e.toString());
        }
    }

    private void configureLibrary() {
        muse.registerDataListener(dataListener,
                MuseDataPacketType.EEG);
        muse.setPreset(MusePreset.PRESET_14);
        muse.enableDataTransmission(true);
    }
}
