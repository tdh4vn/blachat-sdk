package com.blameo.chatsdk

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
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

    private object Holder {val INSTANCE = ChatApplication()}


    companion object {

        @JvmStatic
        fun getInstance(): ChatApplication {
            return Holder.INSTANCE
        }
    }

    fun getDisplayImageOption(): DisplayImageOptions {
        return options
    }

    fun getContext(): String {
        return TAG
    }

    fun getSP(): SharedPreferences{
        return getSharedPreferences("user_sp", Context.MODE_PRIVATE)
    }

    override fun onCreate() {
        super.onCreate()

        context = applicationContext


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