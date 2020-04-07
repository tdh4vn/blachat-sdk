package com.blameo.chatsdk

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.QueueProcessingType


lateinit var shareInstance: ChatSdkApplication

class ChatSdkApplication : Application(), LifecycleObserver {

    private val TAG = "application"

//    lateinit var appComponent: AppComponent
//
//    private lateinit var domainComponent: DomainComponent
//
//    private lateinit var dataComponent: DataComponent

    private var config: ImageLoaderConfiguration.Builder? = null
    private lateinit var context: Context

//    private var options: DisplayImageOptions = DisplayImageOptions.Builder()
//        .cacheInMemory(true)
//        .considerExifParams(true)
//        .cacheOnDisk(true)
//        .resetViewBeforeLoading(false)
//        .showImageOnLoading(R.drawable.place_holder_no_image)
//        .showImageOnFail(R.drawable.place_holder_no_image)
//        .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
//        .cacheOnDisc(true)
//        .build()

    companion object {

        fun getInstance(): ChatSdkApplication {
            return shareInstance
        }
    }

//    fun getDisplayImageOption(): DisplayImageOptions {
//        return options
//    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun onAppForegrounded() {
    }


    override fun onCreate() {
        super.onCreate()

//        FacebookSdk.sdkInitialize(applicationContext)
//        AppEventsLogger.activateApp(this)

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        context = this

//        dataComponent = DaggerDataComponent.builder()
//            .context(this)
//            .build()
//
//        domainComponent = DaggerDomainComponent.builder()
//            .dataComponent(dataComponent)
//            .build()
//
//        appComponent = DaggerAppComponent.builder()
//            .appInstance(this)
//            .domainComponent(domainComponent)
//            .build()

        shareInstance = this



        val mi = ActivityManager.MemoryInfo()

        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        manager.getMemoryInfo(mi)
        var avaiMem = (mi.availMem / (1048576L * 4)).toInt()

        if (avaiMem > 100)
            avaiMem = 100

        if (config == null) {
            config = ImageLoaderConfiguration.Builder(applicationContext)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .diskCacheFileNameGenerator(Md5FileNameGenerator())
                .diskCacheSize(avaiMem * 1024 * 1024)
                .memoryCacheSize(avaiMem * 1024 * 1024)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs()

            ImageLoader.getInstance().init(config!!.build())
        }
    }

}