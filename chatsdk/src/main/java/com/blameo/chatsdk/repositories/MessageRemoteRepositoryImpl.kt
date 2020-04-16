package com.blameo.chatsdk.repositories

import android.util.Log
import com.blameo.chatsdk.models.bodies.CreateMessageBody
import com.blameo.chatsdk.models.results.BaseResult
import com.blameo.chatsdk.models.results.GetMessageByIDResult
import com.blameo.chatsdk.models.results.GetMessagesResult
import com.blameo.chatsdk.net.MessageAPI
import com.blameo.chatsdk.sources.MessageResultListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MessageRemoteRepositoryImpl(
    private val messageAPI: MessageAPI,
    private val messageListener: MessageResultListener
) :
    MessageRemoteRepository {

    private val TAG = "MESS_MODEL"

    override fun getMessageById(id: String) {

        messageAPI.getMessageById(id)
            .enqueue(object : Callback<GetMessageByIDResult> {
                override fun onFailure(call: Call<GetMessageByIDResult>, t: Throwable) {
                }

                override fun onResponse(
                    call: Call<GetMessageByIDResult>,
                    response: Response<GetMessageByIDResult>
                ) {
                    Log.e(TAG, "${response.isSuccessful}")
                    if (response.isSuccessful)
                        messageListener.onGetMessageByIdSuccess(response.body()?.message!!)
                }
            })

    }

    override fun createMessage(body: CreateMessageBody) {
        messageAPI.createMessage(body)
            .enqueue(object : Callback<GetMessageByIDResult> {
                override fun onFailure(call: Call<GetMessageByIDResult>, t: Throwable) {
                }

                override fun onResponse(
                    call: Call<GetMessageByIDResult>,
                    response: Response<GetMessageByIDResult>
                ) {
                    Log.e(TAG, "${response.isSuccessful}")
                    if (response.isSuccessful)
                        messageListener.onCreateMessageSuccess(response.body()?.message!!)
                }
            })
    }

    override fun getMessages(channelId: String, lastId: String) {

        messageAPI.getMessagesInChannel(channelId, lastId)
            .enqueue(object : Callback<GetMessagesResult> {
                override fun onFailure(call: Call<GetMessagesResult>, t: Throwable) {
                }

                override fun onResponse(
                    call: Call<GetMessagesResult>,
                    response: Response<GetMessagesResult>
                ) {
                    if (response.isSuccessful)
                        if(response.body()?.data != null)
                             messageListener.onGetRemoteMessagesSuccess(response.body()?.data!!)
                }
            })
    }

    override fun sendSeenMessageEvent(channelId: String, messageId: String, authorId: String) {

        messageAPI.markSeenMessage(MarkStatusMessage(messageId, channelId, authorId))
            .enqueue(object: Callback<BaseResult>{
                override fun onFailure(call: Call<BaseResult>, t: Throwable) {
                    messageListener.onMarkSeenMessageFail(t.message!!)
                }

                override fun onResponse(call: Call<BaseResult>, response: Response<BaseResult>) {
                    if(response.isSuccessful)
                        if(response.body()!!.success())
                            messageListener.onMarkSeenMessageSuccess(messageId)
                        else
                            messageListener.onMarkSeenMessageFail(response.body()!!.resultMessage)
                    else
                        messageListener.onMarkSeenMessageFail(response.errorBody().toString())
                }
            })
    }

    override fun sendReceivedMessageEvent(channelId: String, messageId: String, authorId: String) {

        messageAPI.markReceiveMessage(MarkStatusMessage(messageId, channelId, authorId))
            .enqueue(object: Callback<BaseResult>{
                override fun onFailure(call: Call<BaseResult>, t: Throwable) {
                    messageListener.onMarkReceiveMessageFail(t.message!!)
                }

                override fun onResponse(call: Call<BaseResult>, response: Response<BaseResult>) {
                    if(response.isSuccessful)
                        if(response.body()!!.success())
                            messageListener.onMarkReceiveMessageSuccess(messageId)
                        else
                            messageListener.onMarkReceiveMessageFail(response.body()!!.resultMessage)
                    else
                        messageListener.onMarkReceiveMessageFail(response.errorBody().toString())

                }
            })
    }

    data class MarkStatusMessage(
        val message_id: String,
        val channel_id: String,
        val receive_id: String
    )

}