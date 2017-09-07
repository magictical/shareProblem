package com.example.android.climbtogether.Model;

import com.example.android.climbtogether.Model.Problem;

/**
 * Created by MD on 2017-01-28.
 */
//Gym 클래스 이름/연락처/위치/사진정보/ [Problem의 슈퍼클래스?]
public class Gym {
    public String gymName;
    public String gymLocation;
    public String gymContact;
    public int gymPrice;
    public String gymPhotoUri;


    //요건다시보기
    private Problem gymProblem;
    public Gym() {}

    public Gym(String gymName, String gymLocation, String gymPhotoUri, String gymContact, int gymPrice) {
        this.gymName = gymName;
        this.gymLocation = gymLocation;
        this.gymPhotoUri = gymPhotoUri;
        this.gymContact = gymContact;
        this.gymPrice = gymPrice;
    }

    public String getGymName() {
        return gymName;
    }

    public void setGymName(String gymName) {
        this.gymName = gymName;
    }

    public String getGymLocation() {
        return gymLocation;
    }

    public void setGymLocation(String gymLocation) {
        this.gymLocation = gymLocation;
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
}
