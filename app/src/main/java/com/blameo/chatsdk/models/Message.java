package com.blameo.chatsdk.models;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;
import com.stfalcon.chatkit.commons.models.MessageContentType;

import java.util.Date;

import static com.blameo.chatsdk.models.Message.MESSAGE_STATUS.SEEN;


public class Message implements IMessage, MessageContentType.Image, MessageContentType {

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
        SEEN, SENT, CREATED
    }

    public User getMyUser() {
        return myUser;
    }

    public void setMyUser(User myUser) {
        this.myUser = myUser;
    }

    private User myUser;
    private Image image;
    private System system;
    private Status messageStatus;

    private MESSAGE_STATUS status;

    private com.blameo.chatsdk.models.pojos.Message message;

    public Message(com.blameo.chatsdk.models.pojos.Message message) {
        this.message = message;
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

        public Status(String seenBy) {
            this.seenBy = seenBy;
        }

        public String getSeenBy() {
            return seenBy;
        }

        public boolean isShowing() {
            return isShowing;
        }

        public void setShowing(boolean isShowing){
            this.isShowing = isShowing;
        }
    }


    public String getStatus(){
        if(!TextUtils.isEmpty(message.getSeenAtString())){
            status = SEEN;
            return "Seen at:";
        }else if(!TextUtils.isEmpty(message.getSentAtString())){
            status = MESSAGE_STATUS.SENT;
            return "Sent at:";
        }
        else if(!TextUtils.isEmpty(message.getCreatedAtString())){
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
        return myUser;
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
            case SEEN :{
                return message.getSeenAt();
            }
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
