package com.sunbuy.qrcardid_java.adapter;

import com.sunbuy.qrcardid_java.data.entities.QRCodeResult;

import java.util.ArrayList;

public interface ResultAdapterListener {
    void onResultClick(QRCodeResult qrCodeResult) ;
    void onRemoveFavorite(QRCodeResult qrCodeResult) ;
    void onCountSelected(ArrayList<QRCodeResult> list) ;
}
