package com.blameo.data.models.results;

import com.google.gson.annotations.SerializedName;

public class LikeResult {

    @SerializedName("isLike")
    private Boolean isLike;

    @SerializedName("numberOfLike")
    private Integer numberOfLike;

    public Integer getNumberOfLike() {
        return numberOfLike;
    }

    public void setNumberOfLike(Integer numberOfLike) {
        this.numberOfLike = numberOfLike;
    }

    public Boolean getLike() {
        return isLike;
    }

    public void setLike(Boolean like) {
        isLike = like;
    }
}
