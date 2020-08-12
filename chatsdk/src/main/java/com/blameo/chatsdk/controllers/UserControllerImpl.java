package com.blameo.chatsdk.controllers;

import com.blameo.chatsdk.repositories.ChannelRepository;
import com.blameo.chatsdk.repositories.ChannelRepositoryImpl;
import com.blameo.chatsdk.repositories.MessageRepository;
import com.blameo.chatsdk.repositories.MessageRepositoryImpl;
import com.blameo.chatsdk.repositories.UserRepository;
import com.blameo.chatsdk.repositories.UserRepositoryImpl;

import java.io.IOException;

public class UserControllerImpl implements UserController {

    private UserRepository userRepository;

    private ChannelRepository channelRepository;

    private MessageRepository messageRepository;

    public UserControllerImpl() {
        userRepository = UserRepositoryImpl.getInstance();
        channelRepository = ChannelRepositoryImpl.getInstance();
        messageRepository = MessageRepositoryImpl.getInstance();
    }

    @Override
    public void updateFCMToken(String imei, String fcmToken) throws IOException {
        userRepository.updateFCMToken(imei, fcmToken);
    }

    @Override
    public void deleteFCMToken(String imei) throws IOException {
        userRepository.deleteFCMToken(imei);
    }

    @Override
    public String getMyId() {
        return userRepository.getMyId();
    }
}
