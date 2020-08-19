package com.jsc.quizapp;

import android.app.Application;

import com.rezwan.knetworklib.KNetwork;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        KNetwork.INSTANCE.initialize(this);
    }
}
