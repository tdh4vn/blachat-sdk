package com.blameo.view.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.core.app.NotificationManagerCompat
import com.blameo.view.ChatSdkApplication
import dagger.Module
import dagger.Provides
import org.ocpsoft.prettytime.PrettyTime
import java.util.*

@Module
class AndroidModule {
    @Provides
    @AppScope
    fun provideApplicationContext(application: Application): Context {
        return application
    }

    @Provides
    @AppScope
    fun provideNotificationManager(context: Context): NotificationManagerCompat {
        return NotificationManagerCompat.from(context)
    }


    @Provides
    @AppScope
    fun provideSharePreference(context: Context): SharedPreferences {
        return context.getSharedPreferences("blameo_chat_sdk", Context.MODE_PRIVATE)
    }

    @Provides
    @AppScope
    fun providePrettyTime(): PrettyTime {
        return PrettyTime(Locale("vi"))
    }

}