package com.sunbuy.qrcardid_java;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.permissionx.guolindev.PermissionX;
import com.sunbuy.qrcardid_java.databinding.ActivityMainBinding;
import com.sunbuy.qrcardid_java.fragment.HistoryFragment;
import com.sunbuy.qrcardid_java.fragment.ImportanceFragment;
import com.sunbuy.qrcardid_java.fragment.QRScanFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private boolean doubleBackToExitPressedOnce = false ;
    private ActivityMainBinding binding ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater()) ;
        setContentView(binding.getRoot());
        initViews();
    }

    public void initViews() {
        requestPermission();

        binding.btnCancelCameraPermission.setOnClickListener(view ->{
            System.exit(0);
        });

        binding.btnOkCameraPermission.setOnClickListener(view -> {
            requestPermission();
        });
    }

    private void requestPermission(){
        List<String> permission = new ArrayList<>() ;
        permission.add(Manifest.permission.CAMERA) ;

        PermissionX.init(this).permissions(permission)
                .explainReasonBeforeRequest()
                .onExplainRequestReason((scope, deniedList) -> {
                    scope.showRequestReasonDialog(deniedList,getString(R.string.txt_permission_app_require),"OK","Cancel") ;
                }).request((allGranted, grantedList, deniedList) -> {
            if (allGranted){
                binding.clScan.setVisibility(View.VISIBLE);
                binding.clAlertPermission.setVisibility(View.GONE);
                BottomNavigationView navView = binding.navMain ;
                navView.getMenu().findItem(R.id.navigation_qrcode).setChecked(true) ;
                replaceFragment(new QRScanFragment());
                navView.setOnItemSelectedListener(item -> {
                    switch (item.getItemId()){
                        case R.id.navigation_qrcode :
                            navView.getMenu().findItem(R.id.navigation_qrcode).setChecked(true) ;
                            replaceFragment(new QRScanFragment());
                            break;
                        case R.id.navigation_history :
                            navView.getMenu().findItem(R.id.navigation_history).setChecked(true) ;
                            replaceFragment(new HistoryFragment());
                            break;
                        case R.id.navigation_favorite :
                            navView.getMenu().findItem(R.id.navigation_favorite).setChecked(true) ;
                            replaceFragment(new ImportanceFragment());
                            break;
                    }
                    return false;
                });

            }else {
                binding.clScan.setVisibility(View.GONE);
                binding.clAlertPermission.setVisibility(View.VISIBLE);
            }
        });
    }

    public void backToScanFragment(){
        BottomNavigationView navView = binding.navMain ;
        navView.getMenu().findItem(R.id.navigation_qrcode).setChecked(true) ;
        replaceFragment(new QRScanFragment());
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager() ;
        FragmentTransaction transaction = fragmentManager.beginTransaction() ;
        transaction.replace(R.id.nav_host_main,fragment).commit() ;
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce){
            System.exit(0);
        }
        doubleBackToExitPressedOnce = true ;
        new Handler(Looper.getMainLooper()).postDelayed(() -> doubleBackToExitPressedOnce = false,2000);
        Toast.makeText(MainActivity.this,getString(R.string.txt_double_back_to_exit),Toast.LENGTH_SHORT).show();
    }
}