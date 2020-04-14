package com.blameo.chatsdk.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.blameo.chatsdk.models.bodies.UsersBody
import com.blameo.chatsdk.models.pojos.User
import com.blameo.chatsdk.models.results.GetUsersByIdsResult
import com.blameo.chatsdk.net.APIProvider
import com.blameo.chatsdk.repositories.UserRemoteRepository
import com.blameo.chatsdk.repositories.UserRemoteRepositoryImpl
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable

private var shareInstance: UserViewModel? = null

class UserViewModel {

    companion object {
        fun getInstance(): UserViewModel {
            if (shareInstance == null)
                shareInstance = UserViewModel()
            return shareInstance!!
        }
    }

    private val TAG = "USER_VM"

    var userRepository: UserRemoteRepository =
        UserRemoteRepositoryImpl(APIProvider.userAPI)

    var usersByIds = MutableLiveData<ArrayList<User>>()

    var errorStream = MutableLiveData<String>()

    private var getUsersByIdsRemoteListener = object : SingleObserver<GetUsersByIdsResult> {
        override fun onSuccess(t: GetUsersByIdsResult) {
            usersByIds.value = t.data
            Log.e(TAG, "s: ${t.data.size}")
        }

        override fun onSubscribe(d: Disposable) {

        }

        override fun onError(e: Throwable) {
            errorStream.value = e.message
            Log.e(TAG, "" + e.message + e.cause + e.stackTrace.toString())
        }
    }

    fun getUsersByIdsRemote(ids: ArrayList<String>) {
        Log.e(TAG, "users by ids: ${ids.size}")
        userRepository.getUsersByIds(UsersBody(ids)).subscribe(getUsersByIdsRemoteListener)
    }
}