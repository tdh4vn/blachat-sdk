package com.blameo.data.repositories

import com.blameo.data.models.results.GetChannelResult
import com.blameo.data.models.results.LikeResult
import io.reactivex.Single

interface ChannelRemoteRepository {

    fun getChannels(): Single<GetChannelResult>

}