package com.blameo.data.responses;

import com.google.gson.annotations.SerializedName;

public class Pageable {
    @SerializedName("pageSize")
    private int pageSize;

    @SerializedName("pageNumber")
    private int pageNumber;


    public Pageable(int pageSize, int pageNumber) {
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
    }

    public Pageable() {
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }
}
