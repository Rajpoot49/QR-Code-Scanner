package com.inventerit.qrcodescanner.Model;

public class User {

    public String userId;
    private String profile_pic;

    public String getProfile_pic(){
        return profile_pic;
    }

    public String getUserId() {
        return userId;
    }

    public User(String userId, String fullname, String email, String id, String profile_pic) {
        this.userId = userId;
        this.fullname = fullname;
        this.email = email;
        this.id = id;
        this.profile_pic = profile_pic;
    }

    public String getFullname() {
        return fullname;
    }

    public String getEmail() {
        return email;
    }
    private String fullname;
    private String email;

    public User(String fullname, String email, String id){
        this.fullname = fullname;
        this.email = email;
        this.id = id;
    }

    private String id;
    public String getId() {
        return id;
    }

}
