package com.blameo.chatsdk.repositories

import com.blameo.chatsdk.models.bodies.UsersBody
import com.blameo.chatsdk.models.results.GetUsersByIdsResult
import com.blameo.chatsdk.net.UserAPI
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class UserRemoteRepositoryImpl constructor(private val userAPI: UserAPI) : UserRemoteRepository {

    override fun getUsersByIds(body: UsersBody): Single<GetUsersByIdsResult> {
        return userAPI.getUsersByIds(body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap {
                Single.just(it)
            }
    }

}