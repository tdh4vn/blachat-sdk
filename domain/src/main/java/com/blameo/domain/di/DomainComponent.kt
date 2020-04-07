package com.blameo.domain.di

import com.blameo.data.di.DataComponent
import com.blameo.domain.usecases.channels.GetChannelRemoteUseCase
import com.blameo.domain.usecases.channels.GetMessageByIdUseCase
import dagger.Component
import com.blameo.domain.usecases.session.*

@Component(
    dependencies = [
        DataComponent::class
    ]
)
@DomainScope
abstract class DomainComponent {

    // CHANNELS
    abstract fun getChannelsRemoteUseCase(): GetChannelRemoteUseCase

    abstract fun loginUseCase(): LoginUseCase

    abstract fun logoutUseCase(): LogoutUseCase

    abstract fun checkLoginUseCase(): CheckLoginUseCase

    // MESSAGES

    abstract fun getMessageByIdUseCase(): GetMessageByIdUseCase


    @Component.Builder
    interface Builder {
        fun dataComponent(dataComponent: DataComponent): Builder

        fun build(): DomainComponent
    }
}