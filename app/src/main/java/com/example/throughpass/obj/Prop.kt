package com.example.throughpass.obj

import com.example.throughpass.obj.Prop.serverUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/*
* 공통 변수/상수
* */
object Prop {

    var user_nfc : String? = null
    val serverUrl : String = "15.165.28.140:8000"

    // Retrofit 객체 생성
    val retrofit: Retrofit = Retrofit.Builder()
    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(serverUrl)
    .client(OkHttpClient())
    .build()
}