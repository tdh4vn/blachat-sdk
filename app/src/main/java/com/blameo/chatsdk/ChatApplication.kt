package com.blameo.chatsdk

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.ImageScaleType
import com.nostra13.universalimageloader.core.assist.QueueProcessingType

class ChatApplication : Application() {

    private val TAG = "application"


    private var config: ImageLoaderConfiguration.Builder? = null
    private lateinit var context: Context

    private var options: DisplayImageOptions = DisplayImageOptions.Builder()
        .cacheInMemory(true)
        .considerExifParams(true)
        .cacheOnDisk(true)
        .resetViewBeforeLoading(false)
        .showImageOnLoading(R.drawable.place_holder_no_image)
        .showImageOnFail(R.drawable.place_holder_no_image)
        .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
        .cacheOnDisc(true)
        .build()

    companion object {

        @SuppressLint("StaticFieldLeak")
        private lateinit var shareInstance: ChatApplication

        fun getInstance(): ChatApplication {
            return shareInstance
        }
    }

    fun getDisplayImageOption(): DisplayImageOptions {
        return options
    }

    fun getContext(): Context {
        return context
    }

    override fun onCreate() {
        super.onCreate()


        if (config == null) {
            config = ImageLoaderConfiguration.Builder(applicationContext)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .diskCacheFileNameGenerator(Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs()

            ImageLoader.getInstance().init(config!!.build())
        }
    }

}