package com.sunbuy.qrcardid_java.scan;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

public class QRCodeAnalyzer implements ImageAnalysis.Analyzer {
    private int[] barcodeFormats ;
    private QRCodeAnalyzerCallback callback ;
    private BarcodeScanner barcodeScanner ;
    private BarcodeScannerOptions.Builder optionsBuilder;
    private volatile boolean failureOccurred = false;
    private volatile long failureTimestamp = 0L;

    public QRCodeAnalyzer(int[] barcodeFormats) {
        this.barcodeFormats = barcodeFormats;

        if (barcodeFormats.length >1){
            optionsBuilder = new BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_QR_CODE,Barcode.FORMAT_UPC_A);
        }else {
            optionsBuilder = new BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS) ;
        }
    }

    public int[] getBarcodeFormats() {
        return barcodeFormats;
    }

    public void setBarcodeFormats(int[] barcodeFormats) {
        this.barcodeFormats = barcodeFormats;
    }

    public void setQRCodeAnalyzerCallback(QRCodeAnalyzerCallback callback){
        this.callback = callback ;

        try {
            barcodeScanner = BarcodeScanning.getClient(optionsBuilder.build());
        } catch (Exception exception) {
            callback.onFailure(exception);
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    @Override
    public void analyze(@NonNull ImageProxy image) {
        if (image.getImage() == null){
            return;
        }
        if (failureOccurred && System.currentTimeMillis() - failureTimestamp < 1000L) {
            image.close() ;
            return ;
        }

        failureOccurred = false ;

        if (barcodeScanner != null){
            barcodeScanner.process(InputImage.fromMediaImage(image.getImage(), image.getImageInfo().getRotationDegrees()))
                    .addOnSuccessListener(barcodes -> {
                        if (barcodes.size() > 0){
                            if(barcodes.get(0) != null){
                                callback.onSuccess(barcodes.get(0));
                            }
                        }
                    }).addOnFailureListener(e -> {
                        failureOccurred = true;
                        failureTimestamp = System.currentTimeMillis();
                        callback.onFailure(e);
                    }).addOnCompleteListener(task -> {
                       callback.onPassCompleted(failureOccurred);
                        image.close() ;
                    });
        }

    }
}
