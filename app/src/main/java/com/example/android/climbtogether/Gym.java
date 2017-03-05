package com.example.android.climbtogether;

/**
 * Created by MD on 2017-01-28.
 */
//Gym 클래스 이름/연락처/위치/사진정보/ [Problem의 슈퍼클래스?]
public class Gym {
    private String gymName;
    private String gymLocation;
    private String gymContact;
    private int gymPrice;
    private int gymPhotoResourceId;


    //요건다시보기
    private Problem gymProblem;
    public Gym() {}

    public Gym(String gymName, String gymLocation, int gymPhotoResourceId, String gymContact, int gymPrice) {
        this.gymName = gymName;
        this.gymLocation = gymLocation;
        this.gymPhotoResourceId = gymPhotoResourceId;
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

    public int getGymPhotoResourceId() {
        return gymPhotoResourceId;
    }

    public void setGymPhotoResourceId(int gymPhotoResourceId) {
        this.gymPhotoResourceId = gymPhotoResourceId;
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
