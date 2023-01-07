package com.sunbuy.qrcardid_java.scan;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;

public class QRCodeImageGallery {
    private int[] barcodeFormats;
    private QRCodeAnalyzerCallback callback;
    private BarcodeScanner barcodeScanner;
    private BarcodeScannerOptions.Builder optionsBuilder;

    public QRCodeImageGallery(int[] barcodeFormats) {
        this.barcodeFormats = barcodeFormats;
        if (barcodeFormats.length > 1) {
            optionsBuilder = new BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_QR_CODE, Barcode.FORMAT_UPC_A);
        } else {
            optionsBuilder = new BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS);
        }
    }

    public void setQRCodeAnalyzerCallback(QRCodeAnalyzerCallback callback) {
        this.callback = callback;

        try {
            barcodeScanner = BarcodeScanning.getClient(optionsBuilder.build());
        } catch (Exception exception) {
            callback.onFailure(exception);
        }
    }

    public void onProcess(InputImage inputImage) {
        if (barcodeScanner != null) {
            barcodeScanner.process(inputImage)
                    .addOnSuccessListener(barcodes -> {
                        if (barcodes.size() > 0) {
                            if (barcodes.get(0) != null) {
                                callback.onSuccess(barcodes.get(0));
                            } else {
                                callback.notFound();
                            }
                        }else {
                            callback.notFound();
                        }
                    }).addOnFailureListener(e -> {
                        callback.onFailure(e);
                    });
        }
    }
}
