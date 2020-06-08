package com.blameo.chatsdk.models;

import androidx.annotation.Nullable;

import com.blameo.chatsdk.models.bla.BlaMessage;
import com.blameo.chatsdk.models.bla.BlaUser;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;
import com.stfalcon.chatkit.commons.models.MessageContentType;

import java.io.Serializable;
import java.util.Date;

import static com.blameo.chatsdk.models.CustomMessage.MESSAGE_STATUS.RECEIVE;
import static com.blameo.chatsdk.models.CustomMessage.MESSAGE_STATUS.SEEN;


public class CustomMessage implements Serializable, IMessage, MessageContentType.Image, MessageContentType {

    @Nullable
    @Override
    public String getImageUrl() {
        return image == null ? null : image.url;
    }

    public Status getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(Status messageStatus) {
        this.messageStatus = messageStatus;
    }

    public enum MESSAGE_STATUS {
        SEEN, RECEIVE, SENT, CREATED
    }

    public CustomUser getMyCustomUser() {
        return myCustomUser;
    }

    public void setMyCustomUser(CustomUser myCustomUser) {
        this.myCustomUser = myCustomUser;
    }

    private CustomUser myCustomUser;
    private Image image;
    private System system;
    private Status messageStatus;

    private MESSAGE_STATUS status;

    private BlaMessage message;

    public CustomMessage(BlaMessage message) {
        this.message = message;
    }

    public BlaMessage getMessage(){
        return message;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void setSystem(System system) {
        this.system = system;
    }

    public System getSystem() {
        return system;
    }

    public static class System {

        private String url;
        private int duration;

        public System(String url, int duration) {
            this.url = url;
            this.duration = duration;
        }

        public String getUrl() {
            return url;
        }

        public int getDuration() {
            return duration;
        }
    }

    public static class Status {

        private boolean isShowing;
        private String seenBy;
        private String receivedBy;

        public Status(String receivedBy, String seenBy, boolean isShowing) {
            this.receivedBy = receivedBy;
            this.seenBy = seenBy;
            this.isShowing = isShowing;
        }

        public String getSeenBy() {
            return seenBy;
        }

        public void setSeenBy(String seenBy){
            this.seenBy = seenBy;
        }

        public boolean isShowing() {
            return isShowing;
        }

        public void setShowing(boolean isShowing){
            this.isShowing = isShowing;
        }

        public String getReceivedBy() {
            return receivedBy;
        }

        public void setReceivedBy(String receivedBy) {
            this.receivedBy = receivedBy;
        }
    }


    public String getStatus(){
        if(message.getSentAt() != null){
            status = MESSAGE_STATUS.SENT;
            return "Sent at:";
        }
        else if(message.getCreatedAt() != null){
            status = MESSAGE_STATUS.CREATED;
            return "Created at:";
        }

        return "";
    }

    @Override
    public String getId() {
        return message.getId();
    }

    @Override
    public String getText() {
        return message.getContent();
    }

    @Override
    public IUser getUser() {
        return myCustomUser == null ? new CustomUser(new BlaUser("","", "", null)) : myCustomUser;
    }

    public static class Image {

        private String url;

        public Image(String url) {
            this.url = url;
        }
    }

    @Override
    public Date getCreatedAt() {

        if (status == null)
            getStatus();

        switch (status){
            case SENT: {
                return message.getSentAt();
            }
            case CREATED:{
                return message.getCreatedAt();
            }
            default:
                return message.getCreatedAt();
        }
    }
}
