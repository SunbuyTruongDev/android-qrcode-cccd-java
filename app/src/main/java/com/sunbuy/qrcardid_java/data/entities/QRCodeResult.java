package com.sunbuy.qrcardid_java.data.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "QRResult")
public class QRCodeResult implements Parcelable {

    @ColumnInfo(name = "content")
    String content ;

    @ColumnInfo(name = "type")
    String type ;

    @ColumnInfo(name = "time")
    Long time ;

    @ColumnInfo(name = "favorite")
    boolean favorite ;

    @PrimaryKey(autoGenerate = true)
    long id ;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public QRCodeResult(String content, String type, Long time, boolean favorite, long id) {
        this.content = content;
        this.type = type;
        this.time = time;
        this.favorite = favorite;
        this.id =id ;
    }

    protected QRCodeResult(Parcel in) {
        content = in.readString();
        type = in.readString();
        if (in.readByte() == 0) {
            time = null;
        } else {
            time = in.readLong();
        }
        favorite = in.readByte() != 0;
        id = in.readLong();
    }

    public static final Creator<QRCodeResult> CREATOR = new Creator<QRCodeResult>() {
        @Override
        public QRCodeResult createFromParcel(Parcel in) {
            return new QRCodeResult(in);
        }

        @Override
        public QRCodeResult[] newArray(int size) {
            return new QRCodeResult[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(content);
        dest.writeString(type);
        if (time == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(time);
        }
        dest.writeByte((byte) (favorite ? 1 : 0));
        dest.writeLong(id);
    }
}
