package com.blameo.data.models.pojos;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

public class User {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name = "";

    @SerializedName("avatar")
    private String avatar;

    @SerializedName("fcmTokens")
    private ArrayList<String> fcmTokens = new ArrayList<>();

    @SerializedName("birthDate")
    private Date birthDate;

    @SerializedName("email")
    private String email = "";

    @SerializedName("role")
    private Role role = Role.User;

    @SerializedName("gender")
    private String gender = "";

    public String dobString = "";

    public User(String id, String name, String avatar, ArrayList<String> fcmTokens, String firebaseID, Date birthDate, Role role) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.fcmTokens = fcmTokens;
        this.birthDate = birthDate;
        this.role = role;
    }

    public User(String id, String name, String avatar, Role role) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.role = role;
    }

    public User(String id, String name, String avatar, Integer number, Role role) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.role = role;
    }

    public User(String id, String name, String avatar, Date birthDate, Role role) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.birthDate = birthDate;
        this.role = role;
    }

    public User(String id, String name, String avatar, ArrayList<String> fcmTokens, Date birthDate, String email, Role role) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.fcmTokens = fcmTokens;
        this.birthDate = birthDate;
        this.email = email;
        this.role = role;
    }

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        if (avatar == null) avatar = "";
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public ArrayList<String> getFcmTokens() {
        return fcmTokens;
    }

    public void setFcmTokens(ArrayList<String> fcmTokens) {
        this.fcmTokens = fcmTokens;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        if (gender == null) gender = "";
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public enum Role {
        User,
        Sale
    }
}
