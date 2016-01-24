package com.marketbng.marketbng;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

import com.interaxon.libmuse.AnnotationData;
import com.interaxon.libmuse.ConnectionState;
import com.interaxon.libmuse.LibMuseVersion;
import com.interaxon.libmuse.MessageType;
import com.interaxon.libmuse.Muse;
import com.interaxon.libmuse.MuseConfiguration;
import com.interaxon.libmuse.MuseDataPacket;
import com.interaxon.libmuse.MuseDataPacketType;
import com.interaxon.libmuse.MuseFileFactory;
import com.interaxon.libmuse.MuseFileReader;
import com.interaxon.libmuse.MuseFileWriter;
import com.interaxon.libmuse.MuseManager;
import com.interaxon.libmuse.MusePreset;
import com.interaxon.libmuse.MuseVersion;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class StartSurveyActivity extends Activity implements OnItemSelectedListener, OnClickListener {


    private Muse muse = null;
    private DataListener dataListener = null;
    private boolean dataTransmission = true;
    private MuseFileWriter fileWriter = null;
    Button btn;
    String surveyId;

    public StartSurveyActivity() {
        dataListener = new DataListener();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_survey);
 
        /* Setting the start button to disabled */
        btn = (Button) findViewById(R.id.startButton);
        btn.setEnabled(false);
 
        /* Making the file and filewriter */
        File dir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        fileWriter = MuseFileFactory.getMuseFileWriter(new File(dir, "new_muse_file.muse"));
        Log.i("Muse Headband", "libmuse version=" + LibMuseVersion.SDK_VERSION);

        fileWriter.addAnnotationString(1, "StartSurveyActivity onCreate");
        dataListener.setFileWriter(fileWriter);

        surveyId = (String) getIntent().getCharSequenceExtra("surveyId");
    }

    @Override
    public void onClick(View v) {

        Spinner musesSpinner = (Spinner) findViewById(R.id.museSpinner);
        musesSpinner.setOnItemSelectedListener(this);

        /** REFRESH BUTTON ACTION WHEN WE ARE TRYING TO FIND PAIRED MUSES
         * This should go in the start field where we select the muse to pair with.
         * There is a drop down menu (spinner) that takes a list of the paired muse
         * and displays one and lets you pick it. This is the refresh portion of that */

        if (v.getId() == R.id.refreshButton) {

            MuseManager.refreshPairedMuses();
            List<Muse> pairedMuses = MuseManager.getPairedMuses();
            List<String> spinnerItems = new ArrayList<String>();

            for (Muse m : pairedMuses) {
                String dev_id = m.getName() + "-" + m.getMacAddress();
                Log.i("Muse Headband", dev_id);
                spinnerItems.add(dev_id);
            }

            ArrayAdapter<String> adapterArray = new ArrayAdapter<String>(
                    this, android.R.layout.simple_spinner_item, spinnerItems);

            musesSpinner.setAdapter(adapterArray);
        }
 
        /* WHEN WE PRESS START BUTTON HERE WE CONNECT TO A MUSE AND START LISTENING */
        else if (v.getId() == R.id.startButton) {
           Intent intent = new Intent(this, SurveyActivity.class);
            intent.putExtra("surveyId",surveyId);
            intent.putExtra("baseAvg", (dataListener.getInitialSum() / dataListener.getSampleCounter()));
            startActivity(intent);
        }
    }

    /* Event handler for when the person selects their Muse device */
    public void onItemSelected(AdapterView<?> p, View v, int position, long id) {

        Spinner musesSpinner = (Spinner) findViewById(R.id.museSpinner);
        List<Muse> pairedMuses = MuseManager.getPairedMuses();

        if (pairedMuses.size() < 1 || musesSpinner.getAdapter().getCount() < 1) {
            Log.w("Muse Headband", "There is nothing to connect to");
        } else {
 
            /* Gets the selected item in the spinner (drop down-list) and sets it as muse*/
            muse = pairedMuses.get(musesSpinner.getSelectedItemPosition());
            ConnectionState state = muse.getConnectionState();

            if (state == ConnectionState.CONNECTED || state == ConnectionState.CONNECTING) {
                Log.w("Muse Headband",
                        "doesn't make sense to connect second time to the same muse");
                return;
            }

            configureLibrary();
            fileWriter.open();
            fileWriter.addAnnotationString(1, "Configuration started");

            /**
             * In most cases libmuse native library takes care about
             * exceptions and recovery mechanism, but native code still
             * may throw in some unexpected situations (like bad bluetooth
             * connection). Print all exceptions here.
             */
            try {
                /* Here is where we do the logic to set up */
                muse.runAsynchronously();

                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            Thread.sleep(15000);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        StartSurveyActivity.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                btn.setEnabled(true);

                            }
                        });
                    }
                }).start();

                muse.disconnect(false);
                dataListener.setAvg(0, (dataListener.getInitialSum() / dataListener.getSampleCounter()));
                dataListener.setinitialSum(0);
                dataListener.setSampleCounter(0);
            } catch (Exception e) {
                Log.e("Muse Headband", e.toString());
            }
        }
    }

    /* Event handler for when no devices are selected in the spinner, keep empty for now */
    public void onNothingSelected(AdapterView<?> p) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start_survey, menu);
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

    /**
     * Configures the library to register our data listener and start transmitting concentration
     * data. *
     */
    private void configureLibrary() {
        muse.registerDataListener(dataListener,
                MuseDataPacketType.EEG);
        muse.setPreset(MusePreset.PRESET_14);
        muse.enableDataTransmission(dataTransmission);
    }
}