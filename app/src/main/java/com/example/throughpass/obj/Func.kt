package com.example.throughpass.obj

import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST
import java.security.MessageDigest

/*
* 공통 함수
* */
object Func {

    fun getUserInfo() : Boolean {
        //retrofit get
        // Prop.user_nfc = "asdfasdasdf"
        return true
    }

//    fun md5(msg : String) : String {
//        var md : MessageDigest = MessageDigest.getInstance("MD5")
//        md.update(msg.toByte())
//        return
//    }
//    public static String md5(String msg) throws NoSuchAlgorithmException {
//
//        MessageDigest md = MessageDigest.getInstance("MD5");
//
//        md.update(msg.getBytes());
//
//        return CryptoUtil.byteToHexString(md.digest());
//
//    }
}

// TEST REST API
// RETROFIT
interface TestService {
    @POST("/ticket/addTicket")
    fun resultRepos(@Body insertTicketData: Prop.AddTicketData) : Single<Prop.TestData>
}

// TEST REST API
// RETROFIT
interface RegistTicketService {
    @POST("/ticket/registTicket")
    fun resultRepos(@Body registTicketData: Prop.RegistTicketData) : Single<Prop.ResultData>
}