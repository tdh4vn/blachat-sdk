package com.blameo.chatsdk;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import io.github.centrifugal.centrifuge.Client;
import io.github.centrifugal.centrifuge.ConnectEvent;
import io.github.centrifugal.centrifuge.DisconnectEvent;
import io.github.centrifugal.centrifuge.EventListener;
import io.github.centrifugal.centrifuge.Options;
import io.github.centrifugal.centrifuge.SubscribeSuccessEvent;
import io.github.centrifugal.centrifuge.Subscription;
import io.github.centrifugal.centrifuge.SubscriptionEventListener;

public class SocketActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket);

        final TextView tv = findViewById(R.id.textView);


        EventListener listener = new EventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onConnect(Client client, ConnectEvent event) {
                SocketActivity.this.runOnUiThread(() -> tv.setText("Connected with client ID " + event.getClient()));
            }
            @SuppressLint("SetTextI18n")
            @Override
            public void onDisconnect(Client client, DisconnectEvent event) {
                SocketActivity.this.runOnUiThread(() -> tv.setText("Disconnected: " + event.getReason()));
            }
        };

        Client client = new Client(
                "ws://159.65.2.104:8001/connection/websocket?format=protobuf",
                new Options(),
                listener
        );
        client.setToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjaGFubmVsIjoiJGNoYXQ6YzUwODI2NzAtZDYwYi00YWRjLTk1NDctZDY1OWZmZGM0NjY5IiwiY2xpZW50IjoiYzUwODI2NzAtZDYwYi00YWRjLTk1NDctZDY1OWZmZGM0NjY5IiwiZXhwIjoxNTg5NDMxMDA4LCJzdWIiOiJjNTA4MjY3MC1kNjBiLTRhZGMtOTU0Ny1kNjU5ZmZkYzQ2NjkiLCJ1c2VySWQiOiJjNTA4MjY3MC1kNjBiLTRhZGMtOTU0Ny1kNjU5ZmZkYzQ2NjkifQ.cf6XdyxrCdZzsvS838FH9n0u4SA6XG2wUUPn-tLlatQ");
        client.connect();

//        SubscriptionEventListener subListener = new SubscriptionEventListener() {
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onSubscribeSuccess(Subscription sub, SubscribeSuccessEvent event) {
//                MainActivity.this.runOnUiThread(() -> tv.setText("Subscribed to " + sub.getChannel()));
//            }
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onSubscribeError(Subscription sub, SubscribeErrorEvent event) {
//                MainActivity.this.runOnUiThread(() -> tv.setText("Subscribe error " + sub.getChannel() + ": " + event.getMessage()));
//            }
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onPublish(Subscription sub, PublishEvent event) {
//                String data = new String(event.getData(), UTF_8);
//                MainActivity.this.runOnUiThread(() -> tv.setText("Message from " + sub.getChannel() + ": " + data));
//            }
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onUnsubscribe(Subscription sub, UnsubscribeEvent event) {
//                MainActivity.this.runOnUiThread(() -> tv.setText("Unsubscribed from " + sub.getChannel()));
//            }
//        };
//
//        Subscription sub;
//        try {
//            sub = client.newSubscription("chat:index", subListener);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return;
//        }
//        sub.subscribe();
    }
}
