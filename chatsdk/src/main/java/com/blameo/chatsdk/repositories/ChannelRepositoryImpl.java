package com.blameo.chatsdk.repositories;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.blameo.chatsdk.models.bla.BlaChannel;
import com.blameo.chatsdk.models.bla.BlaChannelType;
import com.blameo.chatsdk.models.bla.EventType;
import com.blameo.chatsdk.models.bla.BlaUser;
import com.blameo.chatsdk.models.bodies.ChannelsBody;
import com.blameo.chatsdk.models.bodies.CreateChannelBody;
import com.blameo.chatsdk.models.bodies.InviteUserToChannelBody;
import com.blameo.chatsdk.models.bodies.RemoveUserFromChannelBody;
import com.blameo.chatsdk.models.bodies.UpdateChannelBody;
import com.blameo.chatsdk.models.bodies.UsersBody;
import com.blameo.chatsdk.models.entities.Channel;
import com.blameo.chatsdk.models.entities.ChannelWithLastMessage;
import com.blameo.chatsdk.models.entities.ChannelWithUser;
import com.blameo.chatsdk.models.entities.Message;
import com.blameo.chatsdk.models.entities.MessageWithUserReact;
import com.blameo.chatsdk.models.entities.User;
import com.blameo.chatsdk.models.entities.UserInChannel;
import com.blameo.chatsdk.models.entities.UserReactMessage;
import com.blameo.chatsdk.models.results.BaseResult;
import com.blameo.chatsdk.models.results.CreateChannelResult;
import com.blameo.chatsdk.models.results.GetChannelResult;
import com.blameo.chatsdk.models.results.GetChannelsResult;
import com.blameo.chatsdk.models.results.GetMembersOfMultiChannelResult;
import com.blameo.chatsdk.models.results.GetUsersByIdsResult;
import com.blameo.chatsdk.models.results.MembersInChannelRemoteDTO;
import com.blameo.chatsdk.repositories.local.BlaChatSDKDatabase;
import com.blameo.chatsdk.repositories.local.dao.ChannelDao;
import com.blameo.chatsdk.repositories.local.dao.MessageDao;
import com.blameo.chatsdk.repositories.local.dao.UserDao;
import com.blameo.chatsdk.repositories.local.dao.UserInChannelDao;
import com.blameo.chatsdk.repositories.remote.api.APIProvider;
import com.blameo.chatsdk.repositories.remote.api.MessageAPI;
import com.blameo.chatsdk.repositories.remote.api.BlaChatAPI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Response;

public class ChannelRepositoryImpl implements ChannelRepository {

    private ChannelDao channelDao;

    private UserDao userDao;

    private UserInChannelDao userInChannelDao;

    private MessageDao messageDao;

    private MessageAPI messageAPI;

    private BlaChatAPI blaChatAPI;

    private String TAG = "channel_repo";

    public ChannelRepositoryImpl(Context context) {
        this.channelDao = BlaChatSDKDatabase.getInstance(context).channelDao();
        this.userDao = BlaChatSDKDatabase.getInstance(context).userDao();
        this.userInChannelDao = BlaChatSDKDatabase.getInstance(context).userInChannelDao();
        this.messageDao = BlaChatSDKDatabase.getInstance(context).messageDao();
        this.blaChatAPI = APIProvider.INSTANCE.getBlaChatAPI();
        this.messageAPI = APIProvider.INSTANCE.getMessageAPI();
//        exportDB();
//        try {
//            blaChatAPI.getAllMembers().execute();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public List<MembersInChannelRemoteDTO> getRemoteUserInChannel(List<String> channelIds) throws IOException {
        Response<GetMembersOfMultiChannelResult> getMembersOfMultiChannelResultResponse = blaChatAPI.getMembersOfMultiChannel(new ChannelsBody(
                channelIds
        )).execute();

        Log.i(TAG, "channel repo : "+channelIds.get(0));

        if (getMembersOfMultiChannelResultResponse.isSuccessful()) {
            assert getMembersOfMultiChannelResultResponse.body() != null;
            return getMembersOfMultiChannelResultResponse.body().getMembersInChannelRemoteDTOS();
        }

        return new ArrayList<>();

    }

    @Override
    public ChannelWithUser getLocalUserInChannel(String channelId) {
        return channelDao.getChannelWithUserById(channelId);
    }

    @Override
    public List<BlaChannel> getChannels(String lastChannelId, long limit) throws Exception {
        long lastUpdate = new Date().getTime();
        if (limit < 0) {
            limit = 0;
        }
        if (!TextUtils.isEmpty(lastChannelId)) {
            Channel lastChannel = channelDao.getChannelById(lastChannelId);
            if (lastChannel != null) {
                lastUpdate = lastChannel.getUpdatedAt().getTime();
            }
        }

        List<ChannelWithLastMessage> channels = channelDao.getChannelsWithLastMessage(lastUpdate, limit);

        if (channels.isEmpty()) {
            Response<GetChannelsResult> response = blaChatAPI.getChannel(
                    limit,
                    lastChannelId
            ).execute();
            List<Channel> channelRemote = response.body().getData();

            channelDao.saveChannel(response.body().getData());

            if (response.isSuccessful() && channelRemote != null && !channelRemote.isEmpty()) {

                channelDao.insertMany(channelRemote);
                Set<String> userIds = new HashSet<>();
                List<UserInChannel> userInChannels = new ArrayList<>();
                ArrayList<String> channelIds = new ArrayList<>();
                ArrayList<Message> messagesFromChannels = new ArrayList<>();
                for (Channel channel: channelRemote) {

                    ChannelWithLastMessage channelWithLastMessage = new ChannelWithLastMessage();
                    channelWithLastMessage.channel = channel;

                    if(channel.getLastMessages() != null)
                    {
                        messagesFromChannels.addAll(channel.getLastMessages());
                        if(channel.getLastMessages().size() > 0)
                            channelWithLastMessage.lastMessage = channel.getLastMessages().get(0);
                        channel.setLastMessageId(channelWithLastMessage.lastMessage.getId());
                    }

                    messageDao.insertMany(messagesFromChannels);

                    channelIds.add(channel.getId());
                    Response<GetMembersOfMultiChannelResult> getMembersOfMultiChannelResultResponse = blaChatAPI.getMembersOfMultiChannel(new ChannelsBody(
                            channelIds
                    )).execute();

                    if (getMembersOfMultiChannelResultResponse.isSuccessful()
                            && getMembersOfMultiChannelResultResponse.body() != null) {
                        for (MembersInChannelRemoteDTO membersInChannelRemoteDTO : getMembersOfMultiChannelResultResponse.body().getMembersInChannelRemoteDTOS()){
                            userInChannels.addAll(membersInChannelRemoteDTO.toUserInChannel());
                        }
                    }

                    channels.add(channelWithLastMessage);
                }

                userInChannelDao.insertMany(userInChannels);

                Response<GetUsersByIdsResult> getUsersResponse = blaChatAPI.getUsersByIds(new UsersBody(
                        new ArrayList<>(userIds)
                )).execute();

                if (getUsersResponse.isSuccessful() && !getUsersResponse.body().getData().isEmpty()) {
                    userDao.insertMany(getUsersResponse.body().getData());
                }
            }
        }

        ArrayList<BlaChannel> blaChannels = new ArrayList<>();
        for (ChannelWithLastMessage c: channels) {
            BlaChannel channel = new BlaChannel(c.channel, c.lastMessage);

            if(c.lastMessage != null){
                MessageWithUserReact messageWithUserReact = messageDao.getUserReactMessageByID(c.lastMessage.getId());
                ArrayList<BlaUser> usersReceivedMesssage = new ArrayList<>();
                ArrayList<BlaUser> usersSeenMessage = new ArrayList<>();
                for (UserReactMessage userReactMessage : messageWithUserReact.userReactMessages){

                    BlaUser targetUser = getUserReactMessage((ArrayList<User>)messageWithUserReact.users, userReactMessage.getUserId());

                    if(userReactMessage.getType() == UserReactMessage.RECEIVE){
                        usersReceivedMesssage.add(targetUser);
                    }else
                        usersSeenMessage.add(targetUser);
                }
                channel.getLastMessage().setReceivedBy(usersReceivedMesssage);
                channel.getLastMessage().setSeenBy(usersSeenMessage);
            }
            blaChannels.add(channel);
        }

        return blaChannels;
    }

    private BlaUser getUserReactMessage(ArrayList<User> users, String targetUserId){
        for(User user : users){
            if(user.getId().equals(targetUserId))
                return new BlaUser(user);
        }
        return null;
    }

    @Override
    public BlaChannel updateChannel(BlaChannel newChannel) throws IOException {
        Response<GetChannelResult> channelResult = blaChatAPI.updateChannel(newChannel.getId(),
                new UpdateChannelBody(newChannel.getName(), newChannel.getAvatar()))
                .execute();

        Log.i(TAG, "update: "+newChannel.getId() + " "+newChannel.getName()
        + " "+ newChannel.getAvatar());

        if(channelResult.isSuccessful()){
            Log.i(TAG, "success: "+channelResult.body().getData());
        }else{
            Log.i(TAG, "error "+channelResult.errorBody().string());
        }

        Channel channel = channelResult.body().getData();
        channelDao.update(channel);

        return new BlaChannel(channel);
    }

    @Override
    public BlaChannel getChannelById(String id) throws IOException {
        Channel channel = channelDao.getChannelById(id);
        if (channel != null) {
            return new BlaChannel(channel);
        }

        Response<GetChannelsResult> response = blaChatAPI.getChannelByIds(new ChannelsBody(
                Collections.singletonList(id)
        )).execute();

        assert response.body() != null;
        if(response.body().getData() != null && response.body().getData().size() > 0)
            channel = response.body().getData().get(0);

        if(channel != null)
            channelDao.insert(channel);

        return new BlaChannel(channel);
    }

    @Override
    public boolean deleteChannel(String channelID) throws IOException {
         Response<BaseResult> result =  blaChatAPI.deleteChannelById(channelID).execute();
         if(result.isSuccessful()){
             assert result.body() != null;
             Log.i(TAG, "delete channel "+result.body().success());
             Channel channel = channelDao.getChannelById(channelID);
             if(channel != null)
                channelDao.delete(channel);
             return result.body().success();
         }else{
             Log.i(TAG, "e "+result.errorBody().string());
         }
        return false;
    }

    @Override
    public BlaChannel createChannel(String name, List<String> userIds, BlaChannelType blaChannelType) throws Exception {
        Response<CreateChannelResult> result = blaChatAPI.createChannel(new CreateChannelBody(
                userIds, name, blaChannelType.getValue(), ""
        )).execute();
        if (result.isSuccessful() && result.body() != null) {
            Channel channel = result.body().getData();
            channelDao.insert(channel);
            return new BlaChannel(channel, null);
        } else {
            throw new Exception(result.message());
        }
    }

    @Override
    public boolean updateLastMessage(String channelId, String messageId) {
        channelDao.updateLastMessage(new ChannelDao.UpdateLastMessageOfChannel(channelId, messageId));
        return true;
    }

    @Override
    public boolean addUserToChannel(String channelId, List<String> userIds) throws Exception {
        Response response = blaChatAPI.inviteUserToChannel(channelId, new InviteUserToChannelBody(
                userIds
        )).execute();
        if (response.isSuccessful()) {
            for(String id: userIds) {
                userInChannelDao.insert(new UserInChannel(channelId, id, new Date(), new Date()));
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean sendTypingEvent(String channelId, EventType typingEvent) throws Exception {
        if (typingEvent == EventType.START) {
            Response<BaseResult> baseResultResponse = blaChatAPI.putTypingEvent(channelId).execute();
            return baseResultResponse.isSuccessful();
        }
        Response<BaseResult> baseResultResponse = blaChatAPI.putStopTypingEvent(channelId).execute();
        return baseResultResponse.isSuccessful();
    }

    @Override
    public void saveUsersInChannel(List<UserInChannel> userInChannelList) {
        userInChannelDao.insertMany(userInChannelList);
    }

    @Override
    public void saveChannel(Channel channel) {
        channelDao.insert(channel);
    }

    @Override
    public boolean checkChannelIsExist(String channelId) {
        return channelDao.getChannelById(channelId) != null;
    }

    @Override
    public void usersAddedToChannel(String channelId, List<String> userIds) {
        for(String id: userIds) {
            userInChannelDao.insert(new UserInChannel(channelId, id, new Date(), new Date()));
        }
    }

    @Override
    public void removeUserFromChannel(String userId, String channelId) throws Exception {
        RemoveUserFromChannelBody body = new RemoveUserFromChannelBody(userId, channelId);
        Response<BaseResult> response = blaChatAPI.removeUserFromChannel(body).execute();
        if(response.isSuccessful()){
            userInChannelDao.delete(new UserInChannel(channelId, userId, new Date(), new Date()));
        }
    }
}
