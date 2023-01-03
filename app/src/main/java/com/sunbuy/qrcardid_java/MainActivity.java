package com.sunbuy.qrcardid_java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ActivityKt;
import androidx.navigation.NavController;
import androidx.navigation.ui.BottomNavigationViewKt;
import androidx.navigation.ui.NavControllerKt;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.base.common.base.BaseActivity;
import com.base.common.extensions.ActivityExtensionsKt;
import com.base.common.extensions.ViewExtensionsKt;
import com.base.common.utils.PermissionResult;
import com.base.common.utils.UtilsKt;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sunbuy.qrcardid_java.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    private boolean doubleBackToExitPressedOnce = false ;

    @Override
    public void initViews() {
        requestPermission();

        ViewExtensionsKt.setSingleClickListener(getBinding().btnCancelCameraPermission, view -> {
            System.exit(0);
            return  null ;
        });

        ViewExtensionsKt.setSingleClickListener(getBinding().btnOkCameraPermission, view -> {
            requestPermission();
            return null ;
        });
    }

    private void requestPermission(){
        List<String> permission = new ArrayList<>() ;
        permission.add(Manifest.permission.CAMERA) ;

        UtilsKt.requestPermission(MainActivity.this,permission , new PermissionResult() {
            @Override
            public void requestSuccess() {
                ViewExtensionsKt.show(getBinding().clScan);
                ViewExtensionsKt.hide(getBinding().clAlertPermission);
                BottomNavigationView navView = getBinding().navMain ;
                NavController navController = ActivityKt.findNavController(MainActivity.this,R.id.nav_host_main) ;
                BottomNavigationViewKt.setupWithNavController(navView,navController);
            }

            @Override
            public void requestDenied() {
                ViewExtensionsKt.hide(getBinding().clScan);
                ViewExtensionsKt.show(getBinding().clAlertPermission);
            }
        });
    }

    public void backToScanFragment(){
        BottomNavigationView navView = getBinding().navMain ;
        NavController navController = ActivityKt.findNavController(MainActivity.this,R.id.nav_host_main) ;
        BottomNavigationViewKt.setupWithNavController(navView,navController);
        ActivityKt.findNavController(MainActivity.this,R.id.nav_host_main).navigate(R.id.navigation_qrcode);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce){
            System.exit(0);
        }
        doubleBackToExitPressedOnce = true ;
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            doubleBackToExitPressedOnce = false ;
        },2000);
        ActivityExtensionsKt.showMessage(MainActivity.this,getString(R.string.txt_double_back_to_exit));
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

}