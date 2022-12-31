package com.sunbuy.qrcardid_java.data;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.sunbuy.qrcardid_java.QRCodeApplication;
import com.sunbuy.qrcardid_java.data.dao.QRResultDAO;
import com.sunbuy.qrcardid_java.data.entities.QRCodeResult;

import java.sql.Array;
import java.util.List;

@Database(entities = {QRCodeResult.class}, version = 1, exportSchema = false)
public abstract class QRScanDatabase extends RoomDatabase {
    public abstract QRResultDAO resultDao();

    public static QRScanDatabase instance;

    public static QRScanDatabase getInstance() {
        QRScanDatabase ret = instance;
        if (ret == null) {
            ret = Room.databaseBuilder(QRCodeApplication.Companion.getInstance(), QRScanDatabase.class, "QRScan").allowMainThreadQueries().build();
            instance = ret ;
        }
        return ret ;
    }

    public void getAllHistory(DataBaseCallback callback){
        callback.listQrResults(resultDao().getAllHistory());
    }

    public void getAllFavorite(DataBaseCallback callback){
        callback.listQrResults(resultDao().getAllFavorite());
    }
}
