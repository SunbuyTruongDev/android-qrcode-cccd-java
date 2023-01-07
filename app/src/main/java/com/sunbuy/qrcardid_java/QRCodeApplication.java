package com.sunbuy.qrcardid_java;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDex;

import timber.log.Timber;


public class QRCodeApplication extends Application {

    public static QRCodeApplication instance ;

    @Override
    protected void attachBaseContext(@NonNull Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static QRCodeApplication getInstance(){
        return instance ;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this ;
        if(BuildConfig.DEBUG){
            Timber.plant(new Timber.Tree[]{});
        }else {
        }
    }
}
