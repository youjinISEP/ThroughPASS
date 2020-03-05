package com.example.throughpass.obj

import android.text.TextUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.throughpass.obj.Prop.registDateStr
import com.example.throughpass.obj.Prop.ticketCode
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.*

/*
* 공통 함수
* */
object Func {

    fun getUserInfo() : Boolean {
        //retrofit get
        // Prop.user_nfc = "asdfasdasdf"
        return true
    }

    // Fragment 새로고침
    fun refreshFragment(fragment: Fragment, fragmentManager: FragmentManager) {
        var ft: FragmentTransaction = fragmentManager.beginTransaction()
        ft.detach(fragment).attach(fragment).commit()
    }

    // 티켓 등록 상태 체크
    // @return : true(티켓 등록), false(티켓 미등록)
    fun checkRegistTicket() : Boolean {
        return !(TextUtils.isEmpty(ticketCode) || TextUtils.isEmpty(registDateStr));
    }

    // 날짜 변환
    fun formatDateKST(date: Date) : String {
        Prop.dateFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        return Prop.dateFormat.format(date)
    }
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