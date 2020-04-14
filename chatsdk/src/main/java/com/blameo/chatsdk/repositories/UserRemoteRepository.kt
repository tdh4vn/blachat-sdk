package com.blameo.chatsdk.repositories

import com.blameo.chatsdk.models.bodies.UsersBody
import com.blameo.chatsdk.models.results.GetUsersByIdsResult
import io.reactivex.Single

interface UserRemoteRepository {

    fun getUsersByIds(body: UsersBody): Single<GetUsersByIdsResult>

}