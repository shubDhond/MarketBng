package com.marketbng.marketbng;

import com.interaxon.libmuse.MuseArtifactPacket;
import com.interaxon.libmuse.MuseDataListener;
import com.interaxon.libmuse.MuseDataPacket;
import com.interaxon.libmuse.MuseFileWriter;

/**
 * Created by Husham on 16-01-23.
 * This class will manage the Data Listener for Muse.
 */
public class DataListener extends MuseDataListener {

    private MuseFileWriter fileWriter;
    double[] averages = new double[6];
    double initialSum = 0;
    double sampleCounter = 0;

    /* Empty constructor for now */
    DataListener() {}

    @Override
    public void receiveMuseDataPacket(MuseDataPacket p) {

        /* Checks when we receive packet data0, adds it to the buffer and then flushes the buffer
         * to the file. */
        switch (p.getPacketType()) {
            case EEG:
                // Gets beta waves, involved with active thinking / stimulation
                initialSum += p.getValues().get(0);
                sampleCounter++;
                break;
            default:
                break;
        }
    }

    /* Handles artifact objects, which are calculated events like blinks, for now empty */
    @Override
    public void receiveMuseArtifactPacket(MuseArtifactPacket p) {
    }

    public void setFileWriter(MuseFileWriter fileWriter) {
        this.fileWriter  = fileWriter;
    }

    public void setinitialSum(double sum) {
        this.initialSum = sum;
    }

    public double getInitialSum() {
        return this.initialSum;
    }

    public double getSampleCounter() {
        return this.sampleCounter;
    }

    public void setSampleCounter(double count) {
        this.sampleCounter = count;
    }

    public void setAvg(int position, double avg) {
        this.averages[position] = avg;
    }
}
