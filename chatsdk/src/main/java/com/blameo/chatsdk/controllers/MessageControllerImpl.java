package com.blameo.chatsdk.controllers;

import com.blameo.chatsdk.models.bla.BlaMessage;
import com.blameo.chatsdk.models.bla.BlaMessageType;
import com.blameo.chatsdk.models.bla.BlaUser;
import com.blameo.chatsdk.models.entities.Message;
import com.blameo.chatsdk.models.entities.UserReactMessage;
import com.blameo.chatsdk.repositories.ChannelRepository;
import com.blameo.chatsdk.repositories.ChannelRepositoryImpl;
import com.blameo.chatsdk.repositories.MessageRepository;
import com.blameo.chatsdk.repositories.MessageRepositoryImpl;
import com.blameo.chatsdk.repositories.UserRepository;
import com.blameo.chatsdk.repositories.UserRepositoryImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class MessageControllerImpl implements MessageController {

    private MessageRepository messageRepository;

    private UserRepository userRepository;

    private ChannelRepository channelRepository;

    public MessageControllerImpl() {
        messageRepository = MessageRepositoryImpl.getInstance();
        userRepository = UserRepositoryImpl.getInstance();
        channelRepository = ChannelRepositoryImpl.getInstance();
    }

    @Override
    public BlaMessage onNewMessage(Message message) throws Exception {
        BlaMessage blaMessage = messageRepository.saveMessage(message);
        injectUserReactToMessage(blaMessage);
        injectAuthorToMessage(blaMessage);
        messageRepository.sendReceiveEvent(message.getChannelId(), message.getId(), message.getAuthorId());
        return blaMessage;
    }

    @Override
    public BlaMessage userReactMyMessage(String userId, String messageId, Date time, int type) throws Exception {
        BlaMessage message = messageRepository.getMessageById(messageId);
        BlaUser user = userRepository.getUserById(userId);
        if (message != null && user != null) {
            if (type == UserReactMessage.RECEIVE) {
                messageRepository.userReceiveMyMessage(user.getId(), message.getId(), time);
            } else {
                messageRepository.userSeenMyMessage(user.getId(), message.getId(), time);
            }
            injectAuthorToMessage(message);
            message.setSeenBy(new ArrayList<>(messageRepository.getUserSeenMessage(messageId)));
            message.setReceivedBy(new ArrayList<>(messageRepository.getUserReceiveMessage(messageId)));
            return message;
        }

        return null;
    }

    @Override
    public BlaMessage sendMessage(String content, String channelID, BlaMessageType type, Map<String, Object> customData) throws Exception {
        BlaMessage message = messageRepository.createMessage(
                String.valueOf(new Date().getTime()),
                userRepository.getMyId(),
                channelID,
                content,
                type.getType(),
                customData
        );

        message = messageRepository.sendMessage(message);
        injectAuthorToMessage(message);

        return message;
    }

    @Override
    public List<BlaMessage> getMessages(String channelId, String lastId, Integer limit) throws IOException {
        List<BlaMessage> messages;
        messages = injectAuthorToMessages(messageRepository.getMessages(channelId, lastId, limit));

        injectAuthorToMessages(messages);

        return messages;
    }

    @Override
    public void markReactMessage(String messageId, String channelId, int type) throws Exception {
        BlaMessage message = messageRepository.getMessageById(messageId);
        if (message == null) {
            return;
        }
        if (message.getAuthorId().equals(userRepository.getMyId())) {
            return;
        }
        if (UserReactMessage.SEEN == type) {
            messageRepository.sendSeenEvent(
                    channelId,
                    messageId,
                    message.getAuthorId());
        } else {
            messageRepository.sendReceiveEvent(
                    channelId,
                    messageId,
                    message.getAuthorId());
        }

    }

    private List<BlaMessage> injectAuthorToMessages(List<BlaMessage> messages) {

        for (BlaMessage message: messages){
            injectAuthorToMessage(message);
        }

        return messages;
    }

    private void injectAuthorToMessage(BlaMessage message) {
        BlaUser author = userRepository.getUserById(message.getAuthorId());

        if (author != null) {
            message.setAuthor(author);
        }
    }

    private void injectUserReactToMessage(BlaMessage message) {
        ArrayList<BlaUser> listReceive = new ArrayList<>();
        ArrayList<BlaUser> listSeen = new ArrayList<>();
        //TODO: Check user react vào message rồi tiêm vào nếu có
        message.setReceivedBy(listReceive);
        message.setSeenBy(listSeen);

    }
}
