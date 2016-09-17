package com.orcchg.data.source.remote.injection;

import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.Component;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

@Singleton
@Component(modules = {CloudModule.class})
public interface CloudComponent {

    Cache okHttpCache();
    Gson gson();
    HttpLoggingInterceptor httpLoggingInterceptor();
    OkHttpClient okHttpClient();
    Retrofit retrofit();
}
