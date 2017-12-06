package com.example.android.climbtogether.Model;

/**
 * Created by MD on 2017-01-28.
 */

public class User {
    private String userName;
    private String userEmail;
    private String userPhotoUri;


    private String userLevel;
    private String userContact;
    private String userGym;
    private String userLocation;

    //기본생성자 이름과 유저레벨은 필수
    public User (String userName, String userEmail, String userPhotoUri) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhotoUri = userPhotoUri;
    }
    //전체 생성자 이걸 하나씩 따로 만들어야하나 모르겠네 여튼 모든정보 입력시 이 생성자가 호출됨
    public User(String userName, String userLevel, String userContact, String userGym, String userLocation) {
        this.userName = userName;
        this.userLevel = userLevel;
        this.userContact = userContact;
        this.userGym = userGym;
        this.userLocation = userLocation;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhotoUri() {
        return userPhotoUri;
    }

    public void setUserPhotoUri(String userPhotoUri) {
        this.userPhotoUri = userPhotoUri;
    }

    public String getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(String userLevel) {
        this.userLevel = userLevel;
    }

    public String getUserContact() {
        return userContact;
    }

    public void setUserContact(String userContact) {
        this.userContact = userContact;
    }

    public String getUserGym() {
        return userGym;
    }

    public void setUserGym(String userGym) {
        this.userGym = userGym;
    }

    public String getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(String userLocation) {
        this.userLocation = userLocation;
    }
}
