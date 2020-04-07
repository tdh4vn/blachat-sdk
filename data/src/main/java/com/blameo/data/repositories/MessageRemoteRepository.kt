package com.blameo.data.repositories

import com.blameo.data.models.results.GetChannelResult
import com.blameo.data.models.results.GetMessageByIDResult
import com.blameo.data.models.results.LikeResult
import io.reactivex.Single

interface MessageRemoteRepository {

    fun getMessageById(id: String): Single<GetMessageByIDResult>

}