package com.example.throughpass.obj

import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

/*
* 공통 함수
* */
object Func {

    fun getUserInfo() : Boolean {
        //retrofit get
        // Prop.user_nfc = "asdfasdasdf"
        return true
    }

    // TEST REST API
    // RETROFIT

}

interface TestService {
    @POST("/ticket/insertTicket")
    fun resultRepos(@Body params : HashMap<String, String>) : Single<TestData>
}
data class TestData(var result : String)