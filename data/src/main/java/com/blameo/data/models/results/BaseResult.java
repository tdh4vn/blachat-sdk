package com.blameo.data.models.results;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

class BaseResult implements Serializable {

    public String getResultMessage() {
        return message;
    }

    public void setResultMessage(String message) {
        this.message = message;
    }

    @SerializedName("message")
    private String message;
}
