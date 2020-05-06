package com.blameo.chatsdk.blachat;

public interface Callback<T> {
    void onSuccess(T result);
    void onFail(Exception e);
}
