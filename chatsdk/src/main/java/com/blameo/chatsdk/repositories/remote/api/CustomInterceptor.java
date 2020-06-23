package com.blameo.chatsdk.repositories.remote.api;

import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class CustomInterceptor implements Interceptor {

    private static CustomInterceptor instance;
    private CustomInterceptor(){

    }

    public static CustomInterceptor getInstance(){
        if (instance == null){
            instance = new CustomInterceptor();
        }
        return instance;
    }
    private TransferData transferData;

    public void setListener(TransferData transferData){
        this.transferData = transferData;
    }


    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        Response response = chain.proceed(request);

        if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {

            transferData.sendData("Token expired");
            response = chain.proceed(request.newBuilder().build());
        }
        return response;
    }

}
