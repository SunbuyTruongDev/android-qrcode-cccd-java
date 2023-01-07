package com.sunbuy.qrcardid_java;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.sunbuy.qrcardid_java.data.QRScanDatabase;
import com.sunbuy.qrcardid_java.data.entities.QRCodeResult;
import com.sunbuy.qrcardid_java.databinding.ActivityShowResultContentBinding;
import com.sunbuy.qrcardid_java.model.CardIdResult;

import java.time.LocalDate;

public class ShowResultContentActivity extends AppCompatActivity {

    private CardIdResult cardIdResult ;
    private ActivityShowResultContentBinding binding ;

    public static void newInstance(Context context , QRCodeResult qrCodeResult , String type){
        Bundle bundle = new Bundle() ;
        bundle.putParcelable("qrResult",qrCodeResult);
        bundle.putString("type",type);
        Intent intent = new Intent(context,ShowResultContentActivity.class);
        intent.putExtras(bundle) ;
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowResultContentBinding.inflate(getLayoutInflater()) ;
        setContentView(binding.getRoot());
        initViews();


    }
    public void initViews() {
        Bundle bundle = getIntent().getExtras() ;
        QRCodeResult qrResult = bundle.getParcelable("qrResult");
        if ("CARD_ID".equals(bundle.getString("type"))) {
            binding.tvTitle.setText(getString(R.string.txt_card_id));
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

            binding.edtCardId.setFocusableInTouchMode(false);
            binding.edtCardIdOld.setFocusableInTouchMode(false);
            binding.edtFullName.setFocusableInTouchMode(false);
            binding.edtGender.setFocusableInTouchMode(false);
            binding.edtDateOfBirth.setFocusableInTouchMode(false);
            binding.edtIssuedOn.setFocusableInTouchMode(false);
            binding.edtExpirationDate.setFocusableInTouchMode(false);
            binding.edtAddress.setFocusableInTouchMode(false);
            binding.edtNationality.setFocusableInTouchMode(false);

            binding.edtCardId.setText(cardIdResult.getIdCardNumber());
            if (cardIdResult.getIdCardNumberOld().length() > 0){
                binding.txtInputCardIdOld.setVisibility(View.VISIBLE);
            }else {
                binding.txtInputCardIdOld.setVisibility(View.GONE);
            }
            binding.edtCardIdOld.setText(cardIdResult.getIdCardNumberOld()) ;
            binding.edtFullName.setText(cardIdResult.getPersonalName()) ;
            binding.edtGender.setText(cardIdResult.getGender()) ;
            binding.edtDateOfBirth.setText(cardIdResult.getDateOfBirth()) ;
            binding.edtIssuedOn.setText(cardIdResult.getDateIssued()) ;
            binding.edtExpirationDate.setText(cardIdResult.getExpirationDate()) ;
            binding.edtAddress.setText(cardIdResult.getAddress()) ;

            if (qrResult.isFavorite()){
                binding.imgFavorite.setImageResource(R.drawable.ic_importance_active);
            }else {
                binding.imgFavorite.setImageResource(R.drawable.ic_importance_inactive);
            }

            binding.imgFavorite.setOnClickListener(view -> {
                QRCodeResult result = QRScanDatabase.getInstance().resultDao().getResult(qrResult.getId()) ;
                if (result.isFavorite()){
                    QRScanDatabase.getInstance().resultDao().update(qrResult.getId(),0);
                    binding.imgFavorite.setImageResource(R.drawable.ic_importance_inactive);
                }else {
                    QRScanDatabase.getInstance().resultDao().update(qrResult.getId(),1);
                    binding.imgFavorite.setImageResource(R.drawable.ic_importance_active);
                }
            });

            binding.imgBack.setOnClickListener(view-> {
                onBackPressed();
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

    interface DataCallback{
        void dateCallback(String dateOfBirth , String expirationDate);
    }
}
