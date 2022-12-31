package com.sunbuy.qrcardid_java;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.base.common.base.BaseActivity;
import com.base.common.extensions.ViewExtensionsKt;
import com.sunbuy.qrcardid_java.data.QRScanDatabase;
import com.sunbuy.qrcardid_java.data.entities.QRCodeResult;
import com.sunbuy.qrcardid_java.databinding.ActivityShowResultContentBinding;
import com.sunbuy.qrcardid_java.model.CardIdResult;

import java.time.LocalDate;

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
        if ("CARD_ID".equals(bundle.getString("type"))) {
            getBinding().tvTitle.setText(getString(R.string.txt_card_id));
            String[] resultArray = qrResult.getContent().split("\\|");
            String idCardNumber = resultArray[0];
            String idCardNumberOld = resultArray[1];
            String personalName = resultArray[2];
            String gender = resultArray[4];
            String address = resultArray[5];
            String dIssued = resultArray[6].substring(0, 2) + "-" + resultArray[6].substring(2, 4) + "-" + resultArray[6].substring(4, 8);
            formatData(resultArray[3], (dateIssued, expirationDate) -> {
                cardIdResult = new CardIdResult(idCardNumber, idCardNumberOld, personalName, gender, dateIssued, dIssued, expirationDate, address);
            });

            getBinding().edtCardId.setFocusableInTouchMode(false);
            getBinding().edtCardIdOld.setFocusableInTouchMode(false);
            getBinding().edtFullName.setFocusableInTouchMode(false);
            getBinding().edtGender.setFocusableInTouchMode(false);
            getBinding().edtDateOfBirth.setFocusableInTouchMode(false);
            getBinding().edtIssuedOn.setFocusableInTouchMode(false);
            getBinding().edtExpirationDate.setFocusableInTouchMode(false);
            getBinding().edtAddress.setFocusableInTouchMode(false);
            getBinding().edtNationality.setFocusableInTouchMode(false);

            getBinding().edtCardId.setText(cardIdResult.getIdCardNumber());
            if (cardIdResult.getIdCardNumberOld().length() > 0){
                getBinding().txtInputCardIdOld.setVisibility(View.VISIBLE);
            }else {
                getBinding().txtInputCardIdOld.setVisibility(View.GONE);
            }
            getBinding().edtCardIdOld.setText(cardIdResult.getIdCardNumberOld()) ;
            getBinding().edtFullName.setText(cardIdResult.getPersonalName()) ;
            getBinding().edtGender.setText(cardIdResult.getGender()) ;
            getBinding().edtDateOfBirth.setText(cardIdResult.getDateOfBirth()) ;
            getBinding().edtIssuedOn.setText(cardIdResult.getDateIssued()) ;
            getBinding().edtExpirationDate.setText(cardIdResult.getExpirationDate()) ;
            getBinding().edtAddress.setText(cardIdResult.getAddress()) ;

            if (qrResult.isFavorite()){
                getBinding().imgFavorite.setImageResource(R.drawable.ic_importance_active);
            }else {
                getBinding().imgFavorite.setImageResource(R.drawable.ic_importance_inactive);
            }

            ViewExtensionsKt.setSingleClickListener(getBinding().imgFavorite,view -> {
                QRCodeResult result = QRScanDatabase.getInstance().resultDao().getResult(qrResult.getId()) ;
                if (result.isFavorite()){
                    QRScanDatabase.getInstance().resultDao().update(qrResult.getId(),0);
                    getBinding().imgFavorite.setImageResource(R.drawable.ic_importance_inactive);
                }else {
                    QRScanDatabase.getInstance().resultDao().update(qrResult.getId(),1);
                    getBinding().imgFavorite.setImageResource(R.drawable.ic_importance_active);
                }
                return null ;
            });

            ViewExtensionsKt.setSingleClickListener(getBinding().imgBack,view ->{
                onBackPressed();
                return null;
            });
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,MainActivity.class) ;
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK) ;
        startActivity(intent);
    }

    public void formatData(String date , DataCallback callback){
        String day  = date.substring(0,2) ;
        String month = date.substring(2,4) ;
        String year = date.substring(4,8) ;

        String dateOfBirth = day + "-" +month + "-" + year ;
        int currentYear = LocalDate.now().getYear() ;

        int oldRange = currentYear - Integer.parseInt(year) ;

        String eDate = "" ;
        if (14 <= oldRange && oldRange <= 22){
            eDate = day + "-" +month + "-" + (Integer.parseInt(year)+25) ;
        }else if (23 <= oldRange && oldRange <= 37){
            eDate = day + "-" +month + "-" + (Integer.parseInt(year)+40) ;
        }else if (38 <= oldRange && oldRange <= 57){
            eDate = day + "-" +month + "-" + (Integer.parseInt(year)+60) ;
        }

        callback.dateCallback(dateOfBirth, eDate);

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_show_result_content;
    }

    interface DataCallback{
        void dateCallback(String dateOfBirth , String expirationDate);
    }
}
