package com.blameo.chatsdk.repositories.local;

import com.blameo.chatsdk.models.pojos.Message;

import java.text.ParseException;
import java.util.ArrayList;

public interface LocalMessageRepository {

    ArrayList<Message> getAllLocalMessages() throws ParseException;

    ArrayList<Message> getAllMessagesInChannel(String channelId, String lastID) throws ParseException;

    void addLocalMessage(Message message);

    int getTotalLocalMessages();

    int updateMessage(String temID, Message message);

    Message getMessageByID(String id) throws ParseException;

    void exportMessageDB() throws ParseException;

    void deleteMessageByID(String messageId);

    boolean checkIfMessageIsExist(String id);

    void clearAllLocalMessages();

    void updateStatusMessage(String messageId, String attr) throws ParseException;

    ArrayList<Message> getUnsentMessage() throws ParseException;
}
