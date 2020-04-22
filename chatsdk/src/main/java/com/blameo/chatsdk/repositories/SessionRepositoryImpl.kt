package com.blameo.chatsdk.repositories

import com.blameo.chatsdk.repositories.local.UserSharedPreference
import com.blameo.chatsdk.models.pojos.Me
import com.blameo.chatsdk.repositories.remote.net.APIProvider
import com.blameo.chatsdk.repositories.remote.net.SessionAPI
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class SessionRepositoryImpl constructor(
    private val sessionAPI: SessionAPI,
    private val UserSharedPreference: UserSharedPreference
) : SessionRepository {
    
    override fun currentSession(): Me {

        val me = UserSharedPreference.getCurrentUser()

        if (me != null) {
            APIProvider.setSession("", me.token)
            // update firebase here
        }
        return me!!
    }

    override fun checkLogin(): Boolean {
        return UserSharedPreference.isLogin()
    }

    override fun saveSession(me: Me) {
        UserSharedPreference.saveCurrentUser(me)
    }

    override fun logout() {
        UserSharedPreference.clearSessionData()
    }

    override fun login(idToken: String): Single<Me> {
        
        return sessionAPI.login(idToken)
            .subscribeOn(Schedulers.io())
            .flatMap {
                saveSession(it)
                APIProvider.setSession("", it.token)
                Single.just(it)
            }
    }
}