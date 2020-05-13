package com.blameo.chatsdk.models.bla;

import com.blameo.chatsdk.models.entities.Message;
import com.blameo.chatsdk.utils.GsonUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class BlaMessage extends Message {
    private ArrayList<BlaUser> seenBy;
    private ArrayList<BlaUser> receivedBy;

    public ArrayList<BlaUser> getSeenBy() {
        return seenBy;
    }

    public void setSeenBy(ArrayList<BlaUser> seenBy) {
        this.seenBy = seenBy;
    }

    public ArrayList<BlaUser> getReceivedBy() {
        return receivedBy;
    }

    public void setReceivedBy(ArrayList<BlaUser> receivedBy) {
        this.receivedBy = receivedBy;
    }

    public BlaMessage(String id, String authorId, String channelId, String content, Date createdAt, Date updatedAt, Date sentAt, int isSystemMessage, HashMap<String, Object> customData, ArrayList<BlaUser> seenBy, ArrayList<BlaUser> receivedBy) {
        super(id, authorId, channelId, content, createdAt, updatedAt, sentAt, isSystemMessage, customData);
        this.seenBy = seenBy;
        this.receivedBy = receivedBy;
    }

    public BlaMessage(ArrayList<BlaUser> seenBy, ArrayList<BlaUser> receivedBy) {
        this.seenBy = seenBy;
        this.receivedBy = receivedBy;
    }

    public BlaMessage(String id, String authorId, String channelId, String content, Date createdAt, Date updatedAt, Date sentAt, int isSystemMessage, HashMap<String, Object> customData) {
        super(id, authorId, channelId, content, createdAt, updatedAt, sentAt, isSystemMessage, customData);
    }

    public BlaMessage(Message message) {
        super(message.getId(),
                message.getAuthorId(),
                message.getChannelId(),
                message.getContent(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getSentAt(),
                message.getIsSystemMessage(),
                message.getCustomData());

    }
}
