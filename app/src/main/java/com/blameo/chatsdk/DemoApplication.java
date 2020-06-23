package com.blameo.chatsdk;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.decoder.SimpleProgressiveJpegConfig;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class DemoApplication extends Application {


    private static DemoApplication instance;
    private ImageLoaderConfiguration.Builder config;

    public static DemoApplication getInstance() {
        return instance;
    }

    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .considerExifParams(true)
            .cacheOnDisk(true)
            .resetViewBeforeLoading(false)
            .showImageOnLoading(R.drawable.place_holder_no_image)
            .showImageOnFail(R.drawable.place_holder_no_image)
            .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
            .cacheOnDisc(true)
            .build();

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        if (config == null) {
            config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                    .threadPriority(Thread.NORM_PRIORITY - 2)
                    .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                    .tasksProcessingOrder(QueueProcessingType.LIFO)
                    .writeDebugLogs();

            ImageLoader.getInstance().init(config.build());
        }

        ImagePipelineConfig cf = ImagePipelineConfig.newBuilder(this)
                .setProgressiveJpegConfig(new SimpleProgressiveJpegConfig())
                .setResizeAndRotateEnabledForNetwork(true)
                .setDownsampleEnabled(true)
                .build();

        Fresco.initialize(this, cf);

    }

    public DisplayImageOptions getImageOptions(){
        return options;
    }
}
