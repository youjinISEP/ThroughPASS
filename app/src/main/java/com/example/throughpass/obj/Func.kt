package com.example.throughpass.obj

import android.content.Context
import android.text.TextUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.throughpass.obj.Prop.registDateStr
import com.example.throughpass.obj.Prop.ticketCode
import io.reactivex.Single
import io.reactivex.annotations.Nullable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import kotlin.collections.ArrayList

/*
* 공통 함수
* */
object Func {
    // Fragment 새로고침
    fun refreshFragment(fragment: Fragment, fragmentManager: FragmentManager) {
        var ft: FragmentTransaction = fragmentManager.beginTransaction()
        ft.detach(fragment).attach(fragment).commit()
    }

    // 티켓 등록 상태 체크
    // @return : true(티켓 등록), false(티켓 미등록)
    fun checkRegistTicket() : Boolean {
        return !(TextUtils.isEmpty(ticketCode) || TextUtils.isEmpty(registDateStr))
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

// 1. RETROFIT(티켓 등록화면 : 티켓 번호 -> 서버)
interface RegistTicketService {
    @POST("/ticket/registTicket")
    fun resultRepos(@Body registTicketData: Prop.RegistTicketData) : Single<Prop.TicketResultData>
}

/**RETROFIT : 놀이기구 리스트 화면*/

// 2. (서버 -> 모든 놀이기구 정보)
interface GetAllRideData {
    @GET("/attr/getAllAttractionsWithWaitTime")
    fun resultRepos() : Single<ArrayList<Prop.AllRideData>>
}

// 3. (nfcUid -> 서버 -> 놀이기구 고유코드)
interface WaitAttractionService{
    @POST("/attr/getWaitAttrCode")
    fun resultRepos(@Body waitRideCodeData: Prop.WaitRideCodeData) : Single<Prop.WaitRideResultData>
}

// 3-1. (nfcUid -> 서버 -> 놀이기구 정보)
interface WaitAttractionInfoService{
    @POST("/attr/getWaitStatusInfo")
    fun resultRepos(@Body waitRideInfoCodeData: Prop.WaitRideInfoCodeData ) : Single<Prop.WaitRideInfoResultData>
}

// 3-2. (nfcUid -> 서버 -> 놀이기구 대기 잔여시간)
interface WaitAttractionWaitMinuteService{
    @POST("/attr/getWaitMinuteOfWaitAttr")
    fun resultRepos(@Body waitMinuteOfWaitAttrData: Prop.WaitMinuteOfWaitAttrData ) : Single<Prop.WaitMinuteInfoData>
}

// 4. (nfcUid -> 서버 -> 놀이기구 고유코드 LIST)
interface ResvAttractionService{
    @POST("/reservation/getReservationAttrCode")
    fun resultRepos(@Body resvRideCodeData: Prop.ResvRideCodeData) : Single<ArrayList<Prop.ResvRideResultData>>
}

// 4-1. (nfcUid -> 서버 -> 놀이기구 정보 LIST)
interface ResvAttractionInfoService{
    @POST("/reservation/getReservation")
    fun resultRepos(@Body resvRideInfoCodeData: Prop.ResvRideInfoCodeData) : Single<ArrayList<Prop.ResvRideInfoResultData>>
}

// 5. (대기신청 버튼: nfcUid, 놀이기구 코드 -> 서버)
interface AddWaitCodeService{
    @POST("/user/insertWaitAttr")
    fun resultRepos(@Body addWaitData: Prop.AddWaitData) : Single<Prop.AddWaitResultData>
}

// 6. (예약신청 버튼: nfcUid, 놀이기구 코드 -> 서버)
interface AddResvCodeService{
    @POST("/reservation/addReservation")
    fun resultRepos(@Body addResvData: Prop.AddResvData) : Single<Prop.ResultData>
}

// 7. (대기삭제 버튼: nfcUid -> 서버)
interface RemoveWaitCodeService{
    @POST("/user/removeWaitAttr")
    fun resultRepos(@Body remWaitData: Prop.RemWaitData) : Single<Prop.ResultData>
}


/**RETROFIT : 현재 상황 화면**/
// 8. (예약취소 버튼: nfcUid, 예약순서 -> 서버)
interface RemvResvCodeService{
    @POST("/reservation/removeReservation")
    fun resultRepos(@Body remResvData: Prop.RemResvData) : Single<Prop.ResultData>
}

// 9. (예약 신청된 놀이기구 drag & dop: nfcUid, 예약놀이기구 리스트 -> 서버)
interface  ChangeResvCodeService{
    @POST("/reservation/changeReservation")
    fun resultRepos(@Body changeResvData: Prop.ChangeResvData) : Single<Prop.ResultData>
}

// 10. (예약 신청된 놀이기구 추천 버튼: nfcUid ->서버 ->놀이기구 정보)
interface RecomResvCodeService{
    @POST("/reservation/getDataForRecommend")
    fun resultRepos(@Body recomResvData: Prop.RecomResvData) : Single<ArrayList<Prop.RecomResvResultData>>
}

// 11. (NFC 태깅 진행: nfcUid, attrCode -> 서버)
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

// 추천
interface GetRecomAttrCodeService {
    @GET("/reservation/recommendReservation")
    fun resultRepos(@Body recomResvData: Prop.RecomResvData) : Single<ArrayList<Prop.RecomResvResultData>>
}