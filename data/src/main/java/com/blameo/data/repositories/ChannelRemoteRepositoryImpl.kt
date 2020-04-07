package com.blameo.data.repositories

import android.util.Log
import com.blameo.data.di.DataScope
import com.blameo.data.models.results.GetChannelResult
import com.blameo.data.models.results.LikeResult
import com.blameo.data.net.UserAPI
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@DataScope
class ChannelRemoteRepositoryImpl @Inject constructor(private val userAPI: UserAPI) : ChannelRemoteRepository {

    override fun getChannels(): Single<GetChannelResult> {
        return userAPI.getChannel(10)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap {
                Single.just(it)
            }
    }

}