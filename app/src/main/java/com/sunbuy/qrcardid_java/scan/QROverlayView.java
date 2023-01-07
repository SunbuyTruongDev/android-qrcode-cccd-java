package com.sunbuy.qrcardid_java.scan;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.sunbuy.qrcardid_java.R;
import com.sunbuy.qrcardid_java.databinding.OverlayViewBinding;

public class QROverlayView extends ConstraintLayout {

    private OverlayViewBinding binding ;
    private int grayColor ;
    private int accentColor ;
    private float horizontalFrameRatio = 1f;
    private QROverlayViewCallback callback ;

    public QROverlayView(@NonNull Context context) {
        super(context);
        binding = OverlayViewBinding.inflate(LayoutInflater.from(context),this,false);
        accentColor = getAccentColor() ;
        setWillNotDraw(false);
        addView(binding.getRoot());
    }

    public QROverlayView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        binding = OverlayViewBinding.inflate(LayoutInflater.from(context),this,false);
        accentColor = getAccentColor() ;
        setWillNotDraw(false);
        addView(binding.getRoot());
    }

    public QROverlayView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        binding = OverlayViewBinding.inflate(LayoutInflater.from(context),this,false);
        accentColor = getAccentColor() ;
        setWillNotDraw(false);
        addView(binding.getRoot());
    }

    public QROverlayView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        binding = OverlayViewBinding.inflate(LayoutInflater.from(context),this,false);
        accentColor = getAccentColor() ;
        setWillNotDraw(false);
        addView(binding.getRoot());
    }

    public void setQROverlayViewCallback(QROverlayViewCallback callback){
        this.callback = callback ;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void setHorizontalFrameRatio(Float ratio){
        if (ratio > 1f){
            horizontalFrameRatio = ratio;
        }
    }

    public void setScanAnimationShow(){
        binding.lottieScanAnimation.setVisibility(VISIBLE);
    }

    public void setScanAnimationHide(){
        binding.lottieScanAnimation.setVisibility(VISIBLE);
    }

    public void setFlashState(boolean isOn){
        if (isOn){
            binding.imgFlash.setImageResource(R.drawable.ic_flash_on) ;
        }else{
            binding.imgFlash.setImageResource(R.drawable.ic_flash_off) ;
        }
    }

    public void setOpenFlash(){
        binding.imgFlash.setOnClickListener(view -> {
            callback.openFlash();
        });
    }

    public void setOpenSoundPip(QROverlayViewCallback callback){
        binding.imgSoundPip.setOnClickListener(view -> {
            callback.onPipSound();
        });
    }

    public void setSoundPipState(boolean isOn){
        if (isOn){
            binding.imgSoundPip.setImageResource(R.drawable.ic_sound_pip_on ) ;
        }else {
            binding.imgSoundPip.setImageResource(R.drawable.ic_sound_pip_off ) ;
        }
    }

    public void importFromGallery(QROverlayViewCallback callback){
        binding.btnImportImage.setOnClickListener(view -> {
            callback.pickImage();
        });
    }

    public int getAccentColor() {
        TypedValue typedValue = new TypedValue() ;
        if (getContext().getTheme().resolveAttribute(android.R.attr.colorAccent,typedValue,true)){
            return typedValue.data ;
        }else {
           return ContextCompat.getColor(getContext(),R.color.accent_fallback) ;
        }
    }

}
