package com.blameo.chatsdk

import com.blameo.chatsdk.models.pojos.Channel
import com.blameo.chatsdk.models.pojos.Message
import com.blameo.chatsdk.models.pojos.User

open class ChatListener {

    // LISTENERS

    // CHANNEL

    interface GetChannelsListener{
        fun onChannelChanged(channel: Channel)
        fun onGetChannelsSuccess(channels: ArrayList<Channel>)
    }

    interface GetUsersInChannelListener{
        fun onGetUsersByIdsSuccess(users: ArrayList<User>)
    }

    interface CreateChannelListener{
        fun createChannelSuccess(channel: Channel)
    }


    // MESSAGE

    interface CreateMessageListener{
        fun createMessageSuccess(message: Message)
    }

    interface GetMessagesListener{
        fun getMessagesSuccess(messages: ArrayList<Message>)
    }


    // USER


    // USER IN CHANNEL
}