package com.sunbuy.qrcardid_java.model;

import android.os.Parcel;
import android.os.Parcelable;

public class CardIdResult implements Parcelable {

    String idCardNumber, idCardNumberOld, personalName, gender, dateOfBirth, dateIssued, expirationDate, address;

    public CardIdResult(String idCardNumber, String idCardNumberOld, String personalName, String gender, String dateOfBirth, String dateIssued, String expirationDate, String address) {
        this.idCardNumber = idCardNumber;
        this.idCardNumberOld = idCardNumberOld;
        this.personalName = personalName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.dateIssued = dateIssued;
        this.expirationDate = expirationDate;
        this.address = address;
    }

    protected CardIdResult(Parcel in) {
        idCardNumber = in.readString();
        idCardNumberOld = in.readString();
        personalName = in.readString();
        gender = in.readString();
        dateOfBirth = in.readString();
        dateIssued = in.readString();
        expirationDate = in.readString();
        address = in.readString();
    }

    public static final Creator<CardIdResult> CREATOR = new Creator<CardIdResult>() {
        @Override
        public CardIdResult createFromParcel(Parcel in) {
            return new CardIdResult(in);
        }

        @Override
        public CardIdResult[] newArray(int size) {
            return new CardIdResult[size];
        }
    };

    public String getIdCardNumber() {
        return idCardNumber;
    }

    public void setIdCardNumber(String idCardNumber) {
        this.idCardNumber = idCardNumber;
    }

    public String getIdCardNumberOld() {
        return idCardNumberOld;
    }

    public void setIdCardNumberOld(String idCardNumberOld) {
        this.idCardNumberOld = idCardNumberOld;
    }

    public String getPersonalName() {
        return personalName;
    }

    public void setPersonalName(String personalName) {
        this.personalName = personalName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getDateIssued() {
        return dateIssued;
    }

    public void setDateIssued(String dateIssued) {
        this.dateIssued = dateIssued;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(idCardNumber);
        dest.writeString(idCardNumberOld);
        dest.writeString(personalName);
        dest.writeString(gender);
        dest.writeString(dateOfBirth);
        dest.writeString(dateIssued);
        dest.writeString(expirationDate);
        dest.writeString(address);
    }
}
