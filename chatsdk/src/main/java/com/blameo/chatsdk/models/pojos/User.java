package com.blameo.chatsdk.models.pojos;

import com.google.gson.annotations.SerializedName;

public class User extends CustomData{

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name = "";

    @SerializedName("avatar")
    private String avatar;

    @SerializedName("created_at")
    private String created_at;

    @SerializedName("updated_at")
    private String updated_at;

    @SerializedName("email")
    private String email = "";

    @SerializedName("role")
    private Role role = Role.User;

    @SerializedName("gender")
    private String gender = "";

    private String connection_status;
    private String last_active_at;
    public boolean isCheck = false;

    public String dobString = "";

    public User(String id, String name, String avatar, String connection_status, String last_active_at) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.connection_status = connection_status;
        this.last_active_at = last_active_at;
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

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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

    public String getConnection_status() {
        return connection_status;
    }

    public void setConnection_status(String connection_status) {
        this.connection_status = connection_status;
    }

    public String getLast_active_at() {
        return last_active_at;
    }

    public void setLast_active_at(String last_active_at) {
        this.last_active_at = last_active_at;
    }

    public enum Role {
        User,
        Sale
    }
}
