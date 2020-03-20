package com.example.throughpass.Main.SSLexception;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.annotation.GlideModule;

import java.io.InputStream;

import io.reactivex.annotations.NonNull;
import okhttp3.OkHttpClient;

@GlideModule
public class MyGlideModule extends AppGlideModule {
    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {

        //To Attach Self Signed Ssl Certificate
        /*OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, X509TrustManager)
                .build();*/

        //Unsafe Okhttp client
        OkHttpClient okHttpClient= UnsafeOkHttpClient.getUnsafeOkHttpClient();

        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(okHttpClient));
    }
}
