package com.blameo.data.di

import android.content.Context
import com.blameo.data.repositories.ChannelRemoteRepository
import com.blameo.data.repositories.MessageRemoteRepository
import com.blameo.data.repositories.SessionRepository
import dagger.BindsInstance
import dagger.Component

@Component(
    modules = [
        DataModule::class
    ]
)
@DataScope
abstract class DataComponent {
    abstract fun providePostRepository(): ChannelRemoteRepository

    abstract fun provideMessageRepository(): MessageRemoteRepository

    abstract fun provideSessionRepository(): SessionRepository

    abstract fun provideContext(): Context

    @Component.Builder
    abstract class Builder {
        @BindsInstance
        abstract fun context(context: Context): Builder

        abstract fun build(): DataComponent
    }
}