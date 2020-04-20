package com.blameo.chatsdk.local;

import com.blameo.chatsdk.models.pojos.Message;

import java.util.ArrayList;

public interface LocalMessageRepository {

    ArrayList<Message> getAllLocalMessages();

    ArrayList<Message> getAllMessagesInChannel(String channelId, String lastID);

    void addLocalMessage(Message message);

    int getTotalLocalMessages();

    int updateMessage(String temID, Message message);

    Message getMessageByID(String id);

    void exportMessageDB();

    void deleteMessageByID(String messageId);

    boolean checkIfMessageIsExist(String id);

    void clearAllLocalMessages();

    void updateStatusMessage(String messageId, String attr);
}
