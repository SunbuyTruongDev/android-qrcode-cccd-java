package com.sunbuy.qrcardid_java.scan;

public interface QROverlayViewCallback {
    default void onPipSound(){} ;
    default void openFlash(){} ;
    default void pickImage(){} ;
}
