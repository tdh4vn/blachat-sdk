package com.blameo.chatsdk.repositories;

import android.content.Context;
import android.graphics.BlendMode;
import android.text.TextUtils;
import android.util.Log;

import com.blameo.chatsdk.models.bla.BlaMessage;
import com.blameo.chatsdk.models.bodies.CreateMessageBody;
import com.blameo.chatsdk.models.bodies.MarkStatusMessageBody;
import com.blameo.chatsdk.models.entities.Message;
import com.blameo.chatsdk.models.entities.MessageWithUserReact;
import com.blameo.chatsdk.models.entities.User;
import com.blameo.chatsdk.models.entities.UserReactMessage;
import com.blameo.chatsdk.models.results.GetMessageByIDResult;
import com.blameo.chatsdk.models.results.GetMessagesResult;
import com.blameo.chatsdk.repositories.local.BlaChatSDKDatabase;
import com.blameo.chatsdk.repositories.local.dao.ChannelDao;
import com.blameo.chatsdk.repositories.local.dao.MessageDao;
import com.blameo.chatsdk.repositories.local.dao.UserDao;
import com.blameo.chatsdk.repositories.local.dao.UserInChannelDao;
import com.blameo.chatsdk.repositories.local.dao.UserReactMessageDao;
import com.blameo.chatsdk.repositories.remote.api.APIProvider;
import com.blameo.chatsdk.repositories.remote.api.MessageAPI;
import com.blameo.chatsdk.repositories.remote.api.BlaChatAPI;
import com.blameo.chatsdk.utils.ChatSdkDateFormatUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Response;

public class MessageRepositoryImpl implements MessageRepository {

    private ChannelDao channelDao;

    private UserDao userDao;

    private UserInChannelDao userInChannelDao;

    private UserReactMessageDao userReactMessageDao;

    private MessageDao messageDao;

    private MessageAPI messageAPI;

    private BlaChatAPI blaChatAPI;

    private String TAG = "mess_repo";

    public MessageRepositoryImpl(Context context) {
        this.channelDao = BlaChatSDKDatabase.getInstance(context).channelDao();
        this.userDao = BlaChatSDKDatabase.getInstance(context).userDao();
        this.userInChannelDao = BlaChatSDKDatabase.getInstance(context).userInChannelDao();
        this.messageDao = BlaChatSDKDatabase.getInstance(context).messageDao();
        this.userReactMessageDao = BlaChatSDKDatabase.getInstance(context).userReactMessageDao();

        this.messageAPI = APIProvider.INSTANCE.getMessageAPI();
    }

    @Override
    public List<BlaMessage> getMessages(String channelId, String lastMessageId, long limit) throws IOException {
        long lastUpdate = new Date().getTime();
        if (limit < 0) limit = 50;

//        Log.i(TAG, "EXPORT MESSAGE DB");
//
//        ArrayList<Message> allMessages = (ArrayList<Message>) messageDao.getAllMessageInChannel(channelId);
//        for (Message m: allMessages) {
//            Log.i(TAG, m.getChannelId() + " " +m.getId() + " "+m.getContent() + " "+m.getCreatedAt());
//        }

        Log.i(TAG, "last id: "+lastMessageId + " "+lastUpdate);
        if (!TextUtils.isEmpty(lastMessageId)) {
            Message lastMessage = messageDao.getMessageById(lastMessageId);
            if (lastMessage != null){

                lastUpdate = lastMessage.getSentAt().getTime()/1000;
                Log.i(TAG, ""+lastUpdate);
            }
        }

        List<Message> messages = messageDao.getMessagesOfChannel(channelId, lastUpdate, limit);
        Log.i(TAG, "local messages size: "+messages.size());

        if(messages.size()  == 0){
            Response<GetMessagesResult> response =
                    messageAPI.getMessagesInChannel(channelId, lastMessageId).execute();


            messages = response.body().getData();
            messageDao.insertMany(response.body().getData());
        }else {
            Collections.reverse(messages);
        }

        Log.i(TAG, "remote messages size: "+messages.size());

        List<BlaMessage> blaMessages = new ArrayList<>();
        for(Message message: messages) {
            blaMessages.add(new BlaMessage(message));
            MessageWithUserReact messageWithUserReactReceive = messageDao.getUserReactMessageByID(message.getId());
//            MessageWithUserReact messageWithUserReactSeen = messageDao.getUserReactMessageByID(message.getId());
            Log.i(TAG, "rec "+messageWithUserReactReceive.message.getId() + " "+messageWithUserReactReceive.users.size());
//            Log.i(TAG, "seen "+messageWithUserReactSeen.message.getId() + " "+messageWithUserReactSeen.users.size());
            for (User user : messageWithUserReactReceive.users) {
                Log.i(TAG, "rec "+user.getId() + " "+user.getName());
            }
//            for (User user : messageWithUserReactSeen.users) {
//                Log.i(TAG, "seen "+user.getId() + " "+user.getName());
//            }
//            List<UserReactMessage> userReactMessages = userReactMessageDao.userReactMessageList(message.getId());
//            Log.i(TAG, "users react size: "+userReactMessages.size());
//            for (UserReactMessage userReactMessage : userReactMessages) {
//                Log.i(TAG, "react: "+userReactMessage.getMessageId() + " "+userReactMessage.getUserId()
//                + " "+userReactMessage.getType() + " "+ userReactMessage.getDate());
//            }
        }

        return blaMessages;
    }

    @Override
    public BlaMessage createMessage(String tmpId, String authorId, String channelId, String content, HashMap<String, Object> customData) {
        Message message = new Message(
                tmpId,
                authorId,
                channelId,
                content,
                1,
                new Date(),
                new Date(),
                null,
                false,
                customData
        );

//        Log.i(TAG, "create ok "+ message.getId());
        messageDao.insert(message);
        return new BlaMessage(message);
    }

    @Override
    public BlaMessage sendMessage(BlaMessage blaMessage) throws Exception {

        Response<GetMessageByIDResult> response = messageAPI.createMessage(new CreateMessageBody(
                0,
                blaMessage.getContent(),
                blaMessage.getChannelId()
        )).execute();

        if (response.isSuccessful() && response.body() != null) {
            Message newMessage = response.body().getMessage();
            messageDao.updateIdMessage(blaMessage, newMessage);
            channelDao.updateLastMessage(
                    new ChannelDao.UpdateLastMessageOfChannel(
                            newMessage.getChannelId(),
                            newMessage.getId()));
//            Log.i(TAG, "update message: " +blaMessage.getId());
            return new BlaMessage(response.body().getMessage());
        }

        return null;
    }

    @Override
    public BlaMessage saveMessage(Message message) {
        messageDao.insert(message);
        return new BlaMessage(message);
    }

    @Override
    public boolean userSeenMyMessage(String userId, String messageId, Date time) {
        userReactMessageDao.insert(new UserReactMessage(
                messageId,
                userId,
                UserReactMessage.SEEN,
                time
        ));
        return true;
    }

    @Override
    public boolean userReceiveMyMessage(String userId, String messageId, Date time) {
        userReactMessageDao.insert(new UserReactMessage(
                messageId,
                userId,
                UserReactMessage.RECEIVE,
                time
        ));
        return true;
    }

    @Override
    public boolean sendSeenEvent(String channelId, String messageId, String authorId) throws Exception {
        Response response = messageAPI.markSeenMessage(new MarkStatusMessageBody(
                messageId,
                channelId,
                authorId
        )).execute();
        return response.isSuccessful();
    }

    @Override
    public boolean sendReceiveEvent(String channelId, String messageId, String authorId) throws Exception {
        Response response = messageAPI.markReceiveMessage(new MarkStatusMessageBody(
                messageId,
                channelId,
                authorId
        )).execute();
        return response.isSuccessful();
    }

    @Override
    public BlaMessage getMessageById(String messageId) {
        return new BlaMessage(messageDao.getMessageById(messageId));
    }

    @Override
    public void syncUnSentMessages() throws Exception {
        List<Message> unsentMessages = messageDao.getUnSentMessages();

//        Log.i(TAG, "unsent messages: "+unsentMessages.size());
        for (Message m: unsentMessages) {
            sendMessage(new BlaMessage(m));
        }
    }
}