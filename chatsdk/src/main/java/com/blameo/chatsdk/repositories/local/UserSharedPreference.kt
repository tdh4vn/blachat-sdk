package com.blameo.chatsdk.repositories.local


import android.content.SharedPreferences
import android.text.TextUtils
import com.blameo.chatsdk.models.pojos.Me
import com.blameo.chatsdk.models.pojos.User
import java.text.SimpleDateFormat
import java.util.*

class UserSharedPreference constructor(private val sharedPreferences: SharedPreferences) {
    
    fun isLogin(): Boolean {
        return sharedPreferences.getBoolean("IS_LOGIN", false)
    }

    fun saveCurrentUser(me: Me): Boolean {

        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        
        val saveUserSession = sharedPreferences.edit()
        saveUserSession.putBoolean("IS_LOGIN", true)
        saveUserSession.putString("ID", me.user.id)
        saveUserSession.putString("NAME", me.user.name)
        saveUserSession.putString("AVATAR", me.user.avatar)
        saveUserSession.putString("EMAIL", me.user.email)
        saveUserSession.putString("GENDER", me.user.gender)

        if (!TextUtils.isEmpty(me.user.dobString))
            saveUserSession.putString("DOB", me.user.dobString)
        var role = 1
        if (me.user.role == User.Role.User) role = 0
        saveUserSession.putInt("ROLE", role)

        if (!TextUtils.isEmpty(me.token))
            saveUserSession.putString("TOKEN", me.token)
        return saveUserSession.commit()
    }

    fun changeAvatar(avatar: String): Boolean {
        val saveUserSession = sharedPreferences.edit()
        saveUserSession.putString("AVATAR", avatar)
        return saveUserSession.commit()
    }

    fun changeName(name: String): Boolean {
        val saveUserSession = sharedPreferences.edit()
        saveUserSession.putString("NAME", name)
        return saveUserSession.commit()
    }

    fun clearSessionData(): Boolean {
        return sharedPreferences.edit().clear().commit()
    }

    fun getCurrentUser(): Me? {
        if (sharedPreferences.contains("IS_LOGIN")) {

//            val me = Me(
//                sharedPreferences.getString("TOKEN", ""),
//                User(
//                    sharedPreferences.getString("ID", ""),
//                    sharedPreferences.getString("NAME", ""),
//                    sharedPreferences.getString("AVATAR", ""),
//                    if (sharedPreferences.getInt("ROLE", 0) == 0) User.Role.User else User.Role.Sale
//                )
//            )
//
//            me.user.email = sharedPreferences.getString("EMAIL", "")
//            me.user.gender = sharedPreferences.getString("GENDER", "")
//            me.user.dobString = sharedPreferences.getString("DOB", "")

            return Me()
        }
        return null
    }
}