package com.blameo.chatsdk.models.results;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BaseResult implements Serializable {

    public String getResultMessage() {
        return message;
    }

    public void setResultMessage(String message) {
        this.message = message;
    }

    @SerializedName("message")
    private String message;

    public boolean success(){
        if(message == null) return false;
        else return message.equals("success");
    }
}
