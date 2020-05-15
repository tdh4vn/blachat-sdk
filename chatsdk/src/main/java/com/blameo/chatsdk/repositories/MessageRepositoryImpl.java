package com.blameo.chatsdk.repositories;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.blameo.chatsdk.models.bla.BlaMessage;
import com.blameo.chatsdk.models.bodies.CreateMessageBody;
import com.blameo.chatsdk.models.bodies.MarkStatusMessageBody;
import com.blameo.chatsdk.models.entities.Message;
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
        if (!TextUtils.isEmpty(lastMessageId)) {
            Message lastMessage = messageDao.getMessageById(lastMessageId);
            if (lastMessage != null){
                lastUpdate = lastMessage.getSentAt().getTime();
            }
        }

        List<Message> messages = messageDao.getMessagesOfChannel(channelId, lastUpdate, limit);
        if(messages.size()  == 0){
            Response<GetMessagesResult> response =
                    messageAPI.getMessagesInChannel(channelId, lastMessageId).execute();

            messageDao.insertMany(response.body().getData());
        }

        Log.i(TAG, "local messages size: "+messages.size());

        List<BlaMessage> blaMessages = new ArrayList<>();
        for(Message message: messages) {
            blaMessages.add(new BlaMessage(message));
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
                new Date(0),
                false,
                customData
        );
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
            messageDao.updateIdMessage(blaMessage, response.body().getMessage());
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
        Response response = messageAPI.markSeenMessage(new MarkStatusMessageBody(
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
}
