package com.blameo.chatsdk.repositories;

import com.blameo.chatsdk.models.bla.BlaMessage;
import com.blameo.chatsdk.models.bla.BlaMessageType;
import com.blameo.chatsdk.models.bla.BlaUser;
import com.blameo.chatsdk.models.entities.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public interface MessageRepository {
    List<BlaMessage> getMessages(String channelId, String lastMessageId, int limit) throws IOException;
    List<BlaMessage> getMessagesByType(String channelId, BlaMessageType blaMessageType) throws IOException;
    BlaMessage createMessage(String tmpId, String authorId, String channelId, String content, int type, Map<String, Object> customData);
    BlaMessage sendMessage(BlaMessage blaMessage) throws Exception;
    BlaMessage saveMessage(Message message);
    List<BlaMessage> saveMessages(List<Message> messages);
    boolean userSeenMyMessage(String userId, String messageId, Date time);
    boolean userReceiveMyMessage(String userId, String messageId, Date time);
    boolean sendSeenEvent(String channelId, String messageId, String authorId) throws Exception;
    boolean sendReceiveEvent(String chanelId, String messageId, String authorId) throws Exception;
    BlaMessage getMessageById(String messageId);
    void syncUnSentMessages() throws Exception;
    BlaMessage deleteMessage(BlaMessage message) throws Exception;
    BlaMessage updateMessage(BlaMessage message) throws Exception;

    List<BlaUser> getUserSeenMessage(String messageId);

    List<BlaUser> getUserReceiveMessage(String messageId);

    ArrayList<Message> getSendingMessageQueue();

}
