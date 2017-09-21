package com.example.android.climbtogether.Model;

/**
 * Created by MD on 2017-01-28.
 */
//Gym 클래스 이름/연락처/위치/사진정보/ [Problem의 슈퍼클래스?]
public class Gym {
    private String gymName;
    public String gymAddress;
    public String gymContact;
    public int gymPrice;
    public String gymPhotoUri;
    public double gymLat;
    public double gymLng;
    public double gymAlt;

    //accuracy
    public float providerAccuracy;
    //bearing
    public float providerBearing;
    //provider
    public   String providerName;
    //time
    public long resisteredTime;


    //요건다시보기
    /*private Problem gymProblem;*/
    public Gym() {}

    public Gym(String gymName, String gymAddress, String gymContact, int gymPrice, String gymPhotoUri,
               double gymLat, double gymLng, double gymAlt, float providerAccuracy, float providerBearing,
               String providerName, long resisteredTime) {
        this.gymName = gymName;
        this.gymAddress = gymAddress;
        this.gymPhotoUri = gymPhotoUri;
        this.gymContact = gymContact;
        this.gymPrice = gymPrice;
        this.gymLat = gymLat;
        this.gymLng = gymLng;
        this.gymAlt = gymAlt;
        this.providerAccuracy = providerAccuracy;
        this.providerBearing = providerBearing;
        this.providerName = providerName;
        this.resisteredTime = resisteredTime;
    }



    public String getGymName() {
        return gymName;
    }

    public void setGymName(String gymName) {
        this.gymName = gymName;
    }

    public String getGymAddress() {
        return gymAddress;
    }

    public void setGymAddress(String gymAddress) {
        this.gymAddress = gymAddress;
    }

    public String getGymPhotoUri() {
        return gymPhotoUri;
    }

    public void setGymPhotoUri(String gymPhotoUri) {
        this.gymPhotoUri = gymPhotoUri;
    }

    public String getGymContact() {
        return gymContact;
    }

    public void setGymContact(String gymContact) {
        this.gymContact = gymContact;
    }

    public int getGymPrice() {
        return gymPrice;
    }

    public void setGymPrice(int gymPrice) {
        this.gymPrice = gymPrice;
    }

    public double getGymLat() {
        return gymLat;
    }

    public void setGymLat(double gymLat) {
        this.gymLat = gymLat;
    }

    public double getGymLng() {
        return gymLng;
    }

    public void setGymLng(double gymLng) {
        this.gymLng = gymLng;
    }

    public double getGymAlt() {
        return gymAlt;
    }

    public void setGymAlt(double gymAlt) {
        this.gymAlt = gymAlt;
    }

    public float getProviderAccuracy() {
        return providerAccuracy;
    }

    public void setProviderAccuracy(float providerAccuracy) {
        this.providerAccuracy = providerAccuracy;
    }

    public float getProviderBearing() {
        return providerBearing;
    }

    public void setProviderBearing(float providerBearing) {
        this.providerBearing = providerBearing;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public long getResisteredTime() {
        return resisteredTime;
    }

    public void setResisteredTime(long resisteredTime) {
        this.resisteredTime = resisteredTime;
    }
}
