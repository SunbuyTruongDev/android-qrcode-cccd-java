package com.sunbuy.qrcardid_java;

import android.content.Context;

public class Utils {
    public static float dipToPix(float dpInFloat, Context context){
        float scale = context.getResources().getDisplayMetrics().density;
        return dpInFloat * scale * 0.5f ;
    }
}
