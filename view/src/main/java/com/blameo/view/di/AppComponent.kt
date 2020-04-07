package com.blameo.view.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.core.app.NotificationManagerCompat
import com.blameo.view.viewmodels.ChannelViewModel
import com.blameo.view.viewmodels.SessionViewModel
import com.blameo.domain.di.DomainComponent
import com.blameo.view.viewmodels.MessageViewModel
import dagger.BindsInstance
import dagger.Component
import org.ocpsoft.prettytime.PrettyTime

@Component(
    modules = [
        AndroidModule::class,
        ViewModelModule::class
    ],
    dependencies = [
        DomainComponent::class
    ]
)
@AppScope
interface AppComponent {
    fun provideNotificationManager(): NotificationManagerCompat
    fun provideSharePreference(): SharedPreferences
    fun provideApplicationContext(): Context
    fun providePrettyTime(): PrettyTime

    @Component.Builder
    abstract class Builder {
        @BindsInstance
        abstract fun appInstance(context: Application): Builder

        abstract fun domainComponent(domainComponent: DomainComponent): Builder

        abstract fun build(): AppComponent
    }

    fun inject(channelViewModel: ChannelViewModel)

    fun inject(messageViewModel: MessageViewModel)

    fun inject(instance: SessionViewModel)

    companion object {
        fun builder(): Builder = DaggerAppComponent.builder()
    }
}