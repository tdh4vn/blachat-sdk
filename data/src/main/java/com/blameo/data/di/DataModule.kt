package com.blameo.data.di

import android.content.Context
import com.blameo.data.net.APIProvider
import com.blameo.data.net.UserAPI
import dagger.Module
import dagger.Provides
import com.blameo.data.local.UserSharedPreference
import com.blameo.data.net.MessageAPI
import com.blameo.data.net.SessionAPI
import com.blameo.data.repositories.*

@Module
class DataModule {

    @Provides
    @DataScope
    fun providePostAPI(): UserAPI {
        return APIProvider.userAPI
    }

    @Provides
    @DataScope
    fun provideChannelRepository(userAPI: UserAPI): ChannelRemoteRepository {
        return ChannelRemoteRepositoryImpl(userAPI)
    }

    @Provides
    @DataScope
    fun provideMessageAPI(): MessageAPI {
        return APIProvider.messageAPI
    }

    @Provides
    @DataScope
    fun provideMessageRepository(messageAPI: MessageAPI): MessageRemoteRepository {
        return MessageRemoteRepositoryImpl(messageAPI)
    }

    @Provides
    @DataScope
    fun provideSessionAPI(): SessionAPI {
        return APIProvider.sessionAPI
    }

    @Provides
    @DataScope
    fun provideSessionRepository(sessionAPI: SessionAPI, sessionPreference: UserSharedPreference): SessionRepository {
        return SessionRepositoryImpl(sessionAPI, sessionPreference)
    }

    @Provides
    @DataScope
    fun provideSessionPreference(context: Context): UserSharedPreference {
        return UserSharedPreference(context.getSharedPreferences("blameo_chat_sdk", Context.MODE_PRIVATE))
    }
}