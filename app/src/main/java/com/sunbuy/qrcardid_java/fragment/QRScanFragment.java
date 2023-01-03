package com.sunbuy.qrcardid_java.fragment;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.util.Size;
import android.view.HapticFeedbackConstants;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.core.TorchState;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;

import com.base.common.base.BaseFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.sunbuy.qrcardid_java.R;
import com.sunbuy.qrcardid_java.ShowResultContentActivity;
import com.sunbuy.qrcardid_java.data.QRScanDatabase;
import com.sunbuy.qrcardid_java.data.entities.QRCodeResult;
import com.sunbuy.qrcardid_java.databinding.FragmentQrScanBinding;
import com.sunbuy.scancode.MlKitErrorHandler;
import com.sunbuy.scancode.QRCodeAnalyzer;
import com.sunbuy.scancode.QRCodeImageGallery;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public class QRScanFragment extends BaseFragment<FragmentQrScanBinding> {

    private ExecutorService analysisExecutor;
    private int[] barcodeFormats = new int[]{Barcode.FORMAT_QR_CODE};
    private boolean hapticFeedback = true;
    private boolean onPipSound = true;
    private boolean onOpenFlash = false;
    private CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

    @Override
    public void setUpViews() {
        applyScannerConfig();
        analysisExecutor = Executors.newSingleThreadExecutor();

        getBinding().overlayView.flipCamera(() -> {
            if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
                cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
            } else {
                cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;
            }
            startCamera();

            return null;
        });

        getBinding().overlayView.setOpenSoundPip(() -> {
            onPipSound = !onPipSound;
            getBinding().overlayView.setSoundPipState(onPipSound);
            return null;
        });

        getBinding().overlayView.importFromGallery(() -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            pickImageResult.launch(intent);
            return null;
        });
        startCamera();
    }

    private ActivityResultLauncher<Intent> pickImageResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Uri data = result.getData().getData();
            try {
                InputImage inputImage = InputImage.fromFilePath(requireContext(), data);
                new QRCodeImageGallery(barcodeFormats, (barcode) -> {
                    onSuccess(barcode);
                    return null;
                }, () -> {
                    Snackbar.make(getBinding().getRoot(), getString(R.string.txt_qr_code_not_found), Snackbar.LENGTH_SHORT).show();
                    return null;
                }, (exception) -> {
                    onFailure(exception);
                    return null ;
                }).onProcess(inputImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    });

    public void startCamera() {
        getBinding().overlayView.setScanAnimationHide();
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(getBinding().previewView.getSurfaceProvider());
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder().setTargetResolution(new Size(1280, 720)).build();
                imageAnalysis.setAnalyzer(analysisExecutor, new QRCodeAnalyzer(barcodeFormats, (barcode) -> {
                    imageAnalysis.clearAnalyzer();
                    onSuccess(barcode);
                    return null;
                }, (exception) -> {
                    onFailure(exception);
                    return null;
                }, (failureOccurred) -> {
                    onPassCompleted();
                    return null;
                }));

                cameraProvider.unbindAll();
                Camera camera =cameraProvider.bindToLifecycle(this,cameraSelector,preview,imageAnalysis) ;
                getBinding().overlayView.setVisibility(View.VISIBLE);
                getBinding().overlayView.setScanAnimationShow();
                camera.getCameraInfo().getTorchState().observe(requireActivity(), torchState -> {
                    getBinding().overlayView.setOpenFlash(()->{
                        if (torchState == TorchState.ON){
                            camera.getCameraControl().enableTorch(false) ;
                            onOpenFlash = false;
                            getBinding().overlayView.setFlashState(false);
                        }else {
                            camera.getCameraControl().enableTorch(true) ;
                            onOpenFlash = true ;
                            getBinding().overlayView.setFlashState(true) ;
                        }
                        return null ;
                    });
                });

            } catch (Exception e) {
                getBinding().overlayView.setVisibility(View.INVISIBLE);
                onFailure(e);
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void onPassCompleted() {
    }

    private void onFailure(Exception exception) {
        if (!MlKitErrorHandler.INSTANCE.isResolvableError(requireActivity(), exception)) {
            requireActivity().finish();
        }
    }

    private void onSuccess(Barcode result) {
        if (hapticFeedback) {
            getBinding().overlayView.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP, HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING | HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
        }
        if (onPipSound) {
            new ToneGenerator(AudioManager.STREAM_MUSIC, 100).startTone(ToneGenerator.TONE_CDMA_PIP, 150);
        }

        String content = result.getRawValue();
        long currentTime = System.currentTimeMillis();
        String type = "CARD_ID";
        if (content != null) {
            if (isCardId(content)) {
                long size = QRScanDatabase.getInstance().resultDao().getCount();
                QRCodeResult qrCodeResult = new QRCodeResult(content, type, currentTime, false );
                QRScanDatabase.getInstance().resultDao().insert(qrCodeResult);
                QRScanDatabase.getInstance().resultDao().limitHistory();
                ShowResultContentActivity.newInstance(requireContext(), qrCodeResult, "CARD_ID");
            } else {
                Snackbar.make(getBinding().getRoot(), getString(R.string.txt_qr_not_equal_card_id), Snackbar.LENGTH_SHORT).show();
                startCamera();
            }
        }
    }

    private Boolean isCardId(String content) {
        return (content.split("\\|").length == 7
                && content.split("\\|")[0].length() == 12
                && content.split("\\|")[3].length() == 8
                && content.split("\\|")[6].length() == 8);
    }

    private void applyScannerConfig() {
        getBinding().overlayView.setHorizontalFrameRatio(1f);
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        getBinding().overlayView.setFlashState(false);
    }


    @Override
    public int getLayoutId() {
        return R.layout.fragment_qr_scan;
    }
}
