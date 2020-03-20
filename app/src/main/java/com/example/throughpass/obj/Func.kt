package com.example.throughpass.obj

import android.app.Application
import android.content.Context
import android.system.Os.open
import android.text.TextUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.throughpass.obj.Prop.registDateStr
import com.example.throughpass.obj.Prop.ticketCode
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.channels.AsynchronousFileChannel.open
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

    // assets 폴더 내 파일 읽어오는 함수
    fun readFromAssets(filename: String, context: Context): String? {
        val reader = BufferedReader(InputStreamReader(context.assets.open(filename)))
        val sb = StringBuilder()
        var line: String? = reader.readLine()

        while (line != null) {
            sb.append(line)
            sb.append("\n")
            line = reader.readLine()
        }
        reader.close()
        return sb.toString()
    }
}


// TEST REST API
// RETROFIT
interface TestService {
    @POST("/ticket/addTicket")
    fun resultRepos(@Body insertTicketData: Prop.AddTicketData) : Single<Prop.TestData>
}
// 티켓 등록 API
interface RegistTicketService {
    @POST("/ticket/registTicket")
    fun resultRepos(@Body registTicketData: Prop.RegistTicketData) : Single<Prop.ResultData>
}

// NFC 태그
interface NfcTaggingService {
    @POST("/user/nfcTagging")
    fun resultRepos(@Body nfcTaggingData: Prop.NfcTaggingData) : Single<Prop.TagResultData>
}

// 티켓 사전 등록 조회
interface RegisteredTodayTicketService {
    @POST("/ticket/getTodayRegisteredTicket")
    fun resultRepos(@Body registeredTodayTicketData: Prop.RegisteredTodayTicketData) : Single<Prop.RegisteredResultData>
}

// 공지사항 데이터 가져오기
interface getAllNoticeService {
    @GET("/notice/getAllNotice")
    fun resultRepos(): Single<ArrayList<Prop.NoticeData>>
}

// 분실물 데이터 가져오기
interface getAllLostsService {
    @GET("/losts/getAllLosts")
    fun resultRepos() : Single<ArrayList<Prop.LostsData>>
}