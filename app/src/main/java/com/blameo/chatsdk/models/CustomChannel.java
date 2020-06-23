package com.blameo.chatsdk.models;

import android.util.Log;

import com.blameo.chatsdk.models.bla.BlaChannel;
import com.blameo.chatsdk.models.bla.BlaUser;
import com.blameo.chatsdk.utils.UserSP;
import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.ArrayList;
import java.util.List;

public class CustomChannel implements IDialog<CustomMessage> {

    private BlaChannel channel;
    private CustomMessage lastMessage;

    public CustomChannel(BlaChannel channel){
        this.channel = channel;
        if(channel.getLastMessage() != null)
            lastMessage = new CustomMessage(channel.getLastMessage());
    }

    @Override
    public String getId() {
        return channel.getId();
    }

    @Override
    public String getDialogPhoto() {
        return channel.getAvatar();
    }

    @Override
    public String getDialogName() {
        return channel.getName();
    }

    @Override
    public List<? extends IUser> getUsers() {
        return getAllUsers();
    }

    @Override
    public CustomMessage getLastMessage() {
        return lastMessage;
    }

    @Override
    public void setLastMessage(CustomMessage message) {
        lastMessage = message;
    }

    @Override
    public int getUnreadCount() {
        Log.i("CUSTOM", "real: "+channel.getUnreadMessages() + " fake: "+ channel.getNumberMessageUnread());
        if(channel.getNumberMessageUnread().contains("+"))
            return 20;
        return Integer.parseInt(channel.getNumberMessageUnread());
    }

    private static ArrayList<CustomUser> getAllUsers() {
        ArrayList<CustomUser> users = new ArrayList<>();
        users.add(new CustomUser(new BlaUser(UserSP.getInstance().getID(), "", "https://png.pngtree.com/svg/20161027/service_default_avatar_182956.png", null, null)));
        return users;
    }

    public BlaChannel getChannel(){
        return channel;
    }


}
