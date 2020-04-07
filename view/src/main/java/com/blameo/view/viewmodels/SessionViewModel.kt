package com.blameo.view.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.blameo.data.models.pojos.Me
import com.blameo.domain.usecases.session.CheckLoginUseCase
import com.blameo.domain.usecases.session.LoginUseCase
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class SessionViewModel(application: Application) : AndroidViewModel(application) {
    @Inject
    lateinit var checkLoginUseCase: CheckLoginUseCase

    @Inject
    lateinit var loginUseCase: LoginUseCase

    lateinit var me: Me

    var errorStream = MutableLiveData<String>()

    var loginState = MutableLiveData<StateSession>()

    var user = MutableLiveData<Boolean>()


    private var updateUserListener = object : SingleObserver<Boolean> {
        override fun onSuccess(t: Boolean) {
            user.value = t
        }

        override fun onSubscribe(d: Disposable) {

        }

        override fun onError(e: Throwable) {
            Log.e("SESS", " update err: " + e.message)
            errorStream.value = e.message
        }
    }

    private val getMeObserver = object : SingleObserver<Me> {
        override fun onSuccess(t: Me) {
            me = t
            loginState.value = StateSession.LoginSuccess
        }

        override fun onSubscribe(d: Disposable) {}

        override fun onError(e: Throwable) {
            e.printStackTrace()
            loginState.value = StateSession.Error
            errorStream.value = e.message
        }

    }

    fun checkLogin() {
        loginState.value = StateSession.LoginChecking
        checkLoginUseCase.execute().subscribe(object : SingleObserver<Boolean> {
            override fun onSuccess(t: Boolean) {

                if (t) {
                } else {
                    loginState.value = StateSession.NeedLogin
                }
            }

            override fun onSubscribe(d: Disposable) {

            }

            override fun onError(e: Throwable) {
                errorStream.value = e.message
                loginState.value = StateSession.Error
            }

        })
    }

    fun login(idToken: String) {
        Log.e("TOEES", idToken)
        loginState.value = StateSession.StartLogin
        loginUseCase.execute(idToken).subscribe(getMeObserver)
    }

    enum class StateSession {
        LoginChecking,
        LoginChecked,
        NeedLogin,
        NavigateToMain,
        StartLogin,
        LoginSuccess,
        Logging,
        Error
    }

}