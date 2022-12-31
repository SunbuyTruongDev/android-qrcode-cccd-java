package com.sunbuy.qrcardid_java;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.base.common.base.BaseActivity;
import com.sunbuy.qrcardid_java.data.entities.QRCodeResult;
import com.sunbuy.qrcardid_java.databinding.ActivityShowResultContentBinding;
import com.sunbuy.qrcardid_java.model.CardIdResult;

public class ShowResultContentActivity extends BaseActivity<ActivityShowResultContentBinding> {

    private CardIdResult cardIdResult ;

    public static void newInstance(Context context , QRCodeResult qrCodeResult , String type){
        Bundle bundle = new Bundle() ;
        bundle.putParcelable("qrResult",qrCodeResult);
        bundle.putString("type",type);
        Intent intent = new Intent(context,ShowResultContentActivity.class);
        intent.putExtras(bundle) ;
        context.startActivity(intent);
    }

    @Override
    public void initViews() {
        Bundle bundle = getIntent().getExtras() ;
        QRCodeResult qrResult = bundle.getParcelable("qrResult");
        switch (bundle.getString("type")){
            case "CARD_ID" : {
                getBinding().tvTitle.setText(getString(R.string.txt_card_id));
                String[] resultArray = qrResult.getContent().split("\\|");
                String idCardNumber = resultArray[0] ;
                String idCardNumberOld = resultArray[1] ;
                String personalName = resultArray[2] ;
                String gender = resultArray[4] ;
                String address = resultArray[5] ;
                String dIssued = resultArray[6].substring(0,2) + "-" + resultArray[6].substring(2,4)+ "-" + resultArray[6].substring(4,8) ;

            }
        }
    }

  

    @Override
    public int getLayoutId() {
        return R.layout.activity_show_result_content;
    }

}
