package com.sunbuy.qrcardid_java;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.multidex.MultiDex;

import com.base.common.base.BaseApplication;

import timber.log.Timber;


public class QRCodeApplication extends BaseApplication {

    @Override
    protected void attachBaseContext(@NonNull Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if(BuildConfig.DEBUG){
            Timber.plant(new Timber.Tree[]{});
        }else {
        }
    }
}
