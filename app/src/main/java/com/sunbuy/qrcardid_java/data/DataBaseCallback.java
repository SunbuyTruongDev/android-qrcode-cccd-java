package com.sunbuy.qrcardid_java.data;

import com.sunbuy.qrcardid_java.data.entities.QRCodeResult;

import java.util.List;

public interface DataBaseCallback{
    void listQrResults(List<QRCodeResult> resultList) ;
}
