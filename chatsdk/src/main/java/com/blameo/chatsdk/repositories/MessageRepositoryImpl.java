package com.blameo.chatsdk.repositories;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.blameo.chatsdk.models.bla.BlaMessage;
import com.blameo.chatsdk.models.bla.BlaUser;
import com.blameo.chatsdk.models.bodies.CreateMessageBody;
import com.blameo.chatsdk.models.bodies.DeleteMessageBody;
import com.blameo.chatsdk.models.bodies.MarkStatusMessageBody;
import com.blameo.chatsdk.models.bodies.UpdateMessageBody;
import com.blameo.chatsdk.models.entities.Message;
import com.blameo.chatsdk.models.entities.MessageWithUserReact;
import com.blameo.chatsdk.models.entities.User;
import com.blameo.chatsdk.models.entities.UserReactMessage;
import com.blameo.chatsdk.models.results.BaseResult;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Response;

public class MessageRepositoryImpl implements MessageRepository {

    private ChannelDao channelDao;

    private UserDao userDao;

    private UserInChannelDao userInChannelDao;

    private UserReactMessageDao userReactMessageDao;

    private MessageDao messageDao;

    private MessageAPI messageAPI;

    private BlaChatAPI blaChatAPI;

    private String myId = "";

    private String TAG = "mess_repo";

    private static MessageRepository messageRepository = null;

    public static MessageRepository getInstance(Context context, String myId) {
        if (messageRepository == null) {
            messageRepository = new MessageRepositoryImpl(context, myId);
        }
        return messageRepository;
    }

    public static MessageRepository getInstance() {
        if (messageRepository != null) {
            return messageRepository;
        }
        throw new RuntimeException("Instance need init first");
    }

    private MessageRepositoryImpl(Context context, String myId) {
        this.channelDao = BlaChatSDKDatabase.getInstance(context).channelDao();
        this.userDao = BlaChatSDKDatabase.getInstance(context).userDao();
        this.userInChannelDao = BlaChatSDKDatabase.getInstance(context).userInChannelDao();
        this.messageDao = BlaChatSDKDatabase.getInstance(context).messageDao();
        this.userReactMessageDao = BlaChatSDKDatabase.getInstance(context).userReactMessageDao();
        this.myId = myId;

        this.messageAPI = APIProvider.INSTANCE.getMessageAPI();
    }

    @Override
    public List<BlaMessage> getMessages(String channelId, String lastMessageId, int limit) throws IOException {
        long lastUpdate = new Date().getTime();
        if (limit < 0) limit = 50;

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

//            String json = new Gson().toJson( response.body().getData());
//            Log.i(TAG, "mess json: "+json);
        } else {
            Collections.reverse(messages);
        }


        List<BlaMessage> blaMessages = new ArrayList<>();
        for(Message message: messages) {
            BlaMessage blaMessage = new BlaMessage(message);
            MessageWithUserReact messageWithUserReact = messageDao.getUserReactMessageByID(message.getId());
            Log.i(TAG, "react "+messageWithUserReact.message.getId() + " "+
                    messageWithUserReact.users.size() + " "+ messageWithUserReact.userReactMessages.size());
            ArrayList<BlaUser> usersReceivedMessage = new ArrayList<>();
            ArrayList<BlaUser> usersSeenMessage = new ArrayList<>();
            for (UserReactMessage userReactMessage : messageWithUserReact.userReactMessages){
                BlaUser targetUser = getUserReactMessage((ArrayList<User>)messageWithUserReact.users, userReactMessage.getUserId());

                if(userReactMessage.getType() == UserReactMessage.RECEIVE){
                    usersReceivedMessage.add(targetUser);
                } else {
                    usersSeenMessage.add(targetUser);
                }
            }
            blaMessage.setReceivedBy(usersReceivedMessage);
            blaMessage.setSeenBy(usersSeenMessage);
            blaMessages.add(blaMessage);
        }

        return blaMessages;
    }

    private BlaUser getUserReactMessage(ArrayList<User> users, String targetUserId){
        for(User user : users){
            if(user.getId().equals(targetUserId))
                return new BlaUser(user);
        }
        return null;
    }


    @Override
    public BlaMessage createMessage(String tmpId, String authorId, String channelId, String content, int type, Map<String, Object> customData) {
        HashMap<String, Object> data;
        if (customData == null) {
            data = new HashMap<>();
        } else {
            data = new HashMap<>(customData);
        }
        Message message = new Message(
                tmpId,
                authorId,
                channelId,
                content,
                type,
                new Date(),
                new Date(),
                null,
                false,
                data
        );

        messageDao.insert(message);
        return new BlaMessage(message);
    }

    @Override
    public BlaMessage sendMessage(BlaMessage blaMessage) throws Exception {

        Response<GetMessageByIDResult> response = messageAPI.sendMessage(new CreateMessageBody(
                blaMessage.getType(),
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
    public List<BlaMessage> saveMessages(List<Message> messages) {
        messageDao.insertMany(messages);
        List<BlaMessage> blaMessages = new ArrayList<>();
        for (Message m: messages) {
            blaMessages.add(new BlaMessage(m));
        }
        return blaMessages;
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

//        userInChannelDao.updateLastSeen(new UserInChannelDao.UpdateSeen(channelId, myId, new Date().getTime()));

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
        Message m = messageDao.getMessageById(messageId);
        if (m == null) {
            return null;
        }
        return new BlaMessage(m);
    }

    @Override
    public void syncUnSentMessages() throws Exception {
        List<Message> unsentMessages = messageDao.getUnSentMessages();
        for (Message m: unsentMessages) {
            sendMessage(new BlaMessage(m));
        }
    }

    @Override
    public BlaMessage deleteMessage(BlaMessage message) throws Exception {
        DeleteMessageBody body = new DeleteMessageBody(message.getId(), message.getChannelId());
        Response<BaseResult> response = messageAPI.deleteMessage(body).execute();
        if(response.isSuccessful() && response.body() != null){
            if(response.body().success()){
                messageDao.delete(message);
                return message;
            }
        }
        return null;
    }

    @Override
    public BlaMessage updateMessage(BlaMessage message) throws Exception {
        UpdateMessageBody body = new UpdateMessageBody(message.getContent(), message.getId(), message.getChannelId());
        Response<GetMessageByIDResult> response = messageAPI.updateMessage(body).execute();
        if(response.isSuccessful() && response.body() != null){
            Message m = response.body().getMessage();
            if(m != null) {
                messageDao.update(m);
                return new BlaMessage(m);
            }
            return null;
        }
        return null;
    }

    @Override
    public List<BlaUser> getUserSeenMessage(String messageId) {
        List<BlaUser> list = new ArrayList<>();
        MessageWithUserReact messageWithUserReact = messageDao.getUserReactMessageByID(messageId);
        for (UserReactMessage userReactMessage : messageWithUserReact.userReactMessages){
            BlaUser targetUser = getUserReactMessage((ArrayList<User>)messageWithUserReact.users, userReactMessage.getUserId());

            if(userReactMessage.getType() == UserReactMessage.SEEN){
                list.add(targetUser);
            }
        }

        return list;
    }

    @Override
    public List<BlaUser> getUserReceiveMessage(String messageId) {
        List<BlaUser> list = new ArrayList<>();
        MessageWithUserReact messageWithUserReact = messageDao.getUserReactMessageByID(messageId);
        for (UserReactMessage userReactMessage : messageWithUserReact.userReactMessages){
            BlaUser targetUser = getUserReactMessage((ArrayList<User>)messageWithUserReact.users, userReactMessage.getUserId());

            if(userReactMessage.getType() == UserReactMessage.RECEIVE){
                list.add(targetUser);
            }
        }

        return list;
    }
}
