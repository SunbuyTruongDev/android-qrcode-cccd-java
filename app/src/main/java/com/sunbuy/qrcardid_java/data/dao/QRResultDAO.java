package com.sunbuy.qrcardid_java.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sunbuy.qrcardid_java.data.entities.QRCodeResult;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface QRResultDAO {

    @Query("SELECT * FROM QRResult ORDER BY id DESC LIMIT 15")
    List<QRCodeResult> getAllHistory() ;

    @Query("DELETE FROM QRResult WHERE id NOT IN(SELECT id FROM QRResult ORDER BY id DESC LIMIT 15) AND favorite = 0")
    void limitHistory() ;

    @Query("SELECT * FROM QRResult WHERE favorite = 1 ")
    List<QRCodeResult> getAllFavorite() ;

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(QRCodeResult qrCodeResult) ;

    @Delete
    void delete(QRCodeResult qrCodeResult) ;

    @Query("UPDATE QRResult SET favorite = :isFavorite WHERE id =:qrCodeResultId")
    void update(long qrCodeResultId, int isFavorite) ;

    @Query("UPDATE QRResult SET favorite = 0 WHERE id IN(:listId)")
    void removeAllFavorite(ArrayList<Long> listId) ;

    @Query("DELETE FROM QRResult WHERE id in (:listId)")
    void deleteHistories(ArrayList<Long>  listId) ;

    @Query("DELETE FROM QRResult")
    void deleteAllHistory();

    @Query("SELECT * FROM QRResult WHERE id = :id LIMIT 1")
    QRCodeResult getResult(long id) ;

    @Query("SELECT COUNT(id) FROM QRResult")
    long getCount();

}
