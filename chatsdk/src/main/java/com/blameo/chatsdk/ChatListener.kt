package com.blameo.chatsdk

import com.blameo.chatsdk.models.entities.Channel
import com.blameo.chatsdk.models.entities.Message
import com.blameo.chatsdk.models.entities.User
import com.blameo.chatsdk.models.results.UserStatus

open class ChatListener {

    // LISTENERS

    // CHANNEL

    interface GetChannelsListener{
        fun onChannelChanged(channel: Channel)
        fun onGetChannelsSuccess(channels: ArrayList<Channel>)
    }

    interface GetUsersInChannelListener{
        fun onGetUsersByIdsSuccess(channelId: String, users: ArrayList<User>)
    }

    interface CreateChannelListener{
        fun createChannelSuccess(channel: Channel)
    }

    interface InviteUsersToChannelListener{
        fun onInviteSuccess(channelId: String, users: ArrayList<User>)
    }


    // MESSAGE

    interface CreateMessageListener{
        fun createMessageSuccess(message: Message)
    }

    interface GetMessagesListener{
        fun getMessagesSuccess(messages: ArrayList<Message>)
        fun getMessageFailed(error: String)
    }

    interface MarkSeenMessageListener{
        fun onSuccess(messageId: String)
        fun onError(error: String)
    }

    interface MarkReceiveMessageListener{
        fun onError(error: String)
    }


    // USER

    interface GetAllMembersListener{
        fun onSuccess(users: ArrayList<User>)
    }

    interface UserStatusChangeListener {
        fun onStatusChanged(userStatus: UserStatus)
    }


    // USER IN CHANNEL
}