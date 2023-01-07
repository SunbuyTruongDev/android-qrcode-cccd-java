package com.sunbuy.qrcardid_java.scan;

import com.google.mlkit.vision.barcode.common.Barcode;

public interface QRCodeAnalyzerCallback {
    void onSuccess(Barcode barcode) ;
    void onFailure(Exception exception) ;
    void onPassCompleted(boolean complete) ;
    void notFound() ;
}
