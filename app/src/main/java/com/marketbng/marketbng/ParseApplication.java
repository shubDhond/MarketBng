package com.marketbng.marketbng;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by sdhond on 2016-01-23.
 */
public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "6OsMY7JbzoLcCpP1UBgMUJdc4Ol68kDskzq8b3aw", "b80ioqHdkQb7YqclIkn33LXIVl8pl91j13Zz1M3f");
    }
}
