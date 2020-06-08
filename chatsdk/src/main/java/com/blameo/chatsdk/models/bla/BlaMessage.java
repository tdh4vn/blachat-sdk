package com.blameo.chatsdk.models.bla;

import com.blameo.chatsdk.models.entities.Message;
import com.blameo.chatsdk.utils.GsonUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class BlaMessage extends Message {
    private ArrayList<BlaUser> seenBy;
    private ArrayList<BlaUser> receivedBy;
    private BlaUser author;

    public ArrayList<BlaUser> getSeenBy() {
        if(seenBy == null)
            seenBy = new ArrayList<>();
        return seenBy;
    }

    public void setSeenBy(ArrayList<BlaUser> seenBy) {
        this.seenBy = seenBy;
    }

    public ArrayList<BlaUser> getReceivedBy() {
        if(receivedBy == null)
            receivedBy = new ArrayList<>();
        return receivedBy;
    }

    public void setReceivedBy(ArrayList<BlaUser> receivedBy) {
        this.receivedBy = receivedBy;
    }

    public BlaMessage(String id, String authorId, String channelId, String content, int type, Date createdAt, Date updatedAt, Date sentAt, boolean isSystemMessage, HashMap<String, Object> customData, ArrayList<BlaUser> seenBy, ArrayList<BlaUser> receivedBy) {
        super(id, authorId, channelId, content, type, createdAt, updatedAt, sentAt, isSystemMessage, customData);
        this.seenBy = seenBy;
        this.receivedBy = receivedBy;
    }

    public BlaMessage(ArrayList<BlaUser> seenBy, ArrayList<BlaUser> receivedBy) {
        this.seenBy = seenBy;
        this.receivedBy = receivedBy;
    }

    public BlaMessage(String id, String authorId, String channelId, String content, int type, Date createdAt, Date updatedAt, Date sentAt, boolean isSystemMessage, HashMap<String, Object> customData) {
        super(id, authorId, channelId, content, type, createdAt, updatedAt, sentAt, isSystemMessage, customData);
    }

    public BlaMessage(Message message) {
        super(message.getId(),
                message.getAuthorId(),
                message.getChannelId(),
                message.getContent(),
                message.getType(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getSentAt(),
                message.getIsSystemMessage(),
                message.getCustomData());

    }

    public BlaUser getAuthor() {
        return author;
    }

    public void setAuthor(BlaUser author) {
        this.author = author;
    }
}
