package com.sunbuy.qrcardid_java.fragment;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.util.Size;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.core.TorchState;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.sunbuy.qrcardid_java.MlKitErrorHandler;
import com.sunbuy.qrcardid_java.R;
import com.sunbuy.qrcardid_java.ShowResultContentActivity;
import com.sunbuy.qrcardid_java.data.QRScanDatabase;
import com.sunbuy.qrcardid_java.data.entities.QRCodeResult;
import com.sunbuy.qrcardid_java.databinding.FragmentQrScanBinding;
import com.sunbuy.qrcardid_java.scan.QRCodeAnalyzer;
import com.sunbuy.qrcardid_java.scan.QRCodeAnalyzerCallback;
import com.sunbuy.qrcardid_java.scan.QROverlayViewCallback;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QRScanFragment extends Fragment {

    private ExecutorService analysisExecutor;
    private int[] barcodeFormats = new int[]{Barcode.FORMAT_QR_CODE};
    private boolean hapticFeedback = true;
    private boolean onPipSound = true;
    private boolean onOpenFlash = false;
    private CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
    private FragmentQrScanBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentQrScanBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpViews();
    }

    public void setUpViews() {
        applyScannerConfig();
        analysisExecutor = Executors.newSingleThreadExecutor();

        binding.overlayView.setOpenSoundPip(new QROverlayViewCallback() {
            @Override
            public void onPipSound() {
                onPipSound = !onPipSound;
                binding.overlayView.setSoundPipState(onPipSound);
            }
        });
        binding.overlayView.importFromGallery(new QROverlayViewCallback() {
            @Override
            public void pickImage() {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                pickImageResult.launch(intent);
            }
        });

        startCamera();
    }

    private final ActivityResultLauncher<Intent> pickImageResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Uri data = Objects.requireNonNull(result.getData()).getData();
            try {
                InputImage inputImage = InputImage.fromFilePath(requireContext(), data);

                com.sunbuy.qrcardid_java.scan.QRCodeImageGallery qrCodeImageGallery = new com.sunbuy.qrcardid_java.scan.QRCodeImageGallery(barcodeFormats);
                qrCodeImageGallery.setQRCodeAnalyzerCallback(new QRCodeAnalyzerCallback() {
                    @Override
                    public void onSuccess(Barcode barcode) {
                        QRScanFragment.this.onSuccess(barcode);
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        QRScanFragment.this.onFailure(exception);
                    }

                    @Override
                    public void onPassCompleted(boolean complete) {

                    }

                    @Override
                    public void notFound() {
                        Snackbar.make(binding.getRoot(), getString(R.string.txt_qr_code_not_found), Snackbar.LENGTH_SHORT).show();
                    }
                });

                qrCodeImageGallery.onProcess(inputImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    });

    public void startCamera() {
        binding.overlayView.setScanAnimationHide();
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(binding.previewView.getSurfaceProvider());
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder().setTargetResolution(new Size(1280, 720)).build();

                QRCodeAnalyzer qrCodeAnalyzer = new QRCodeAnalyzer(barcodeFormats);
                qrCodeAnalyzer.setQRCodeAnalyzerCallback(new QRCodeAnalyzerCallback() {
                    @Override
                    public void onSuccess(Barcode barcode) {
                        imageAnalysis.clearAnalyzer();
                        QRScanFragment.this.onSuccess(barcode);
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        QRScanFragment.this.onFailure(exception);
                    }

                    @Override
                    public void onPassCompleted(boolean complete) {
                        QRScanFragment.this.onPassCompleted();
                    }

                    @Override
                    public void notFound() {

                    }
                });

                imageAnalysis.setAnalyzer(analysisExecutor, qrCodeAnalyzer);

                cameraProvider.unbindAll();
                Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
                binding.overlayView.setVisibility(View.VISIBLE);
                binding.overlayView.setScanAnimationShow();
                camera.getCameraInfo().getTorchState().observe(requireActivity(), torchState -> {

                    binding.overlayView.setOpenFlash();
                    binding.overlayView.setQROverlayViewCallback(new QROverlayViewCallback() {
                        @Override
                        public void openFlash() {
                            if (torchState == TorchState.ON) {
                                camera.getCameraControl().enableTorch(false);
                                onOpenFlash = false;
                                binding.overlayView.setFlashState(false);
                            } else {
                                camera.getCameraControl().enableTorch(true);
                                onOpenFlash = true;
                                binding.overlayView.setFlashState(true);
                            }
                        }
                    });
                });

            } catch (Exception e) {
                binding.overlayView.setVisibility(View.INVISIBLE);
                onFailure(e);
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void onPassCompleted() {
    }

    private void onFailure(Exception exception) {
        if (!MlKitErrorHandler.isResolvableError(requireActivity(), exception)) {
            requireActivity().finish();
        }
    }

    private void onSuccess(Barcode result) {
        if (hapticFeedback) {
            binding.overlayView.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP, HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING | HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
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
                QRCodeResult qrCodeResult = new QRCodeResult(content, type, currentTime, false);
                QRScanDatabase.getInstance().resultDao().insert(qrCodeResult);
                QRScanDatabase.getInstance().resultDao().limitHistory();
                ShowResultContentActivity.newInstance(requireContext(), qrCodeResult, "CARD_ID");
            } else {
                Snackbar.make(binding.getRoot(), getString(R.string.txt_qr_not_equal_card_id), Snackbar.LENGTH_SHORT).show();
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
        binding.overlayView.setHorizontalFrameRatio(1f);
    }


    @Override
    public void onResume() {
        binding.overlayView.setFlashState(false);
        super.onResume();
    }
}
