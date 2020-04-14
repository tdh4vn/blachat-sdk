package com.blameo.chatsdk.repositories

import com.blameo.chatsdk.models.bodies.CreateChannelBody
import com.blameo.chatsdk.models.results.CreateChannelResult
import com.blameo.chatsdk.models.results.GetChannelResult
import com.blameo.chatsdk.models.results.GetUsersInChannelResult
import com.blameo.chatsdk.net.UserAPI
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ChannelRemoteRepositoryImpl constructor(private val userAPI: UserAPI) : ChannelRemoteRepository {

    override fun getChannels(): Single<GetChannelResult> {
        return userAPI.getChannel(10)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap {
                Single.just(it)
            }
    }

    override fun getUsersInChannel(id: String): Single<GetUsersInChannelResult> {
        return userAPI.getUsersInChannels(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap {
                Single.just(it)
            }
    }

    override fun createChannel(body: CreateChannelBody): Single<CreateChannelResult> {
        return userAPI.createChannel(body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap {
                Single.just(it)
            }
    }

}