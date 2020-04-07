package com.blameo.view.di

import com.blameo.view.viewmodels.ViewModelStore
import dagger.Module
import dagger.Provides

@Module
class ViewModelModule {

    var viewModelStore: ViewModelStore? = null

    @Provides
    @AppScope
    fun provideViewModelStore(): ViewModelStore {
        if (viewModelStore == null) {
            viewModelStore = ViewModelStore()
        }
        return viewModelStore!!
    }

}