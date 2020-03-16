package com.example.throughpass.obj

import com.example.throughpass.obj.Prop.serverUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.math.BigInteger
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/*
* 공통 변수/상수
* */
object Prop {
    var user_nfc : String? = null
    var ticketCode : String? = null
    var registDate : BigInteger? = null
    var registDateStr : String? = null
    val serverUrl : String = "http://15.165.28.140:8000"
    val FCM_MSG_CODE : Int = 111
    val TAG : String = "GHOST"
    var fcmTokenId : String? = null

    // Retrofit 객체 생성
    val retrofit: Retrofit = Retrofit.Builder()
    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(serverUrl)
    .client(OkHttpClient())
    .build()

    // 날짜 출력 Format
    var dateFormat : DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREAN)

    // TEST API
    data class AddTicketData(val ticketCode : String, var nfcUid : String?)
    data class TestData(var result : String)

    // 티켓 등록
    data class RegistTicketData(val ticketCode : String, val nfcUid : String, val tokenId : String)
    data class ResultData(var result : String, var registDate : BigInteger)
}