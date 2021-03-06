package com.blameo.chatsdk.controllers;

import com.blameo.chatsdk.models.bla.BlaMessage;
import com.blameo.chatsdk.models.bla.BlaMessageType;
import com.blameo.chatsdk.models.entities.Message;
import com.blameo.chatsdk.models.entities.UserReactMessage;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface MessageController {
    BlaMessage onNewMessage(Message message) throws Exception;

    BlaMessage userReactMyMessage(String userId, String messageId, Date time, int type) throws Exception;

    BlaMessage sendMessage(String content, String channelID, BlaMessageType type, Map<String, Object> customData) throws Exception;

    List<BlaMessage> getMessages(String channelId, String lastId, Integer limit) throws IOException;

    void markReactMessage(String messageId, String channelId, int type) throws Exception;

}
