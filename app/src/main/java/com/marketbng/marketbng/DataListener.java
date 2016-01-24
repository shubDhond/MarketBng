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

    /* Empty constructor for now */
    DataListener() {}

    @Override
    public void receiveMuseDataPacket(MuseDataPacket p) {

        /* Checks when we receive packet data, adds it to the buffer and then flushes the buffer
         * to the file. */
        switch (p.getPacketType()) {
            case CONCENTRATION:
                fileWriter.addDataPacket(1, p);
                fileWriter.addAnnotationString(1, "<-- Concentration Value");
                fileWriter.flush();
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

}
