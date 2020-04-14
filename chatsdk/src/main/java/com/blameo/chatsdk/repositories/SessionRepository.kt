package com.blameo.chatsdk.repositories

import com.blameo.chatsdk.models.pojos.Me
import io.reactivex.Single

interface SessionRepository {

    fun login(idToken: String): Single<Me>

    fun saveSession(me: Me)

    fun currentSession(): Me

    fun checkLogin(): Boolean

    fun logout()

}