package com.world.magicline.obj

import com.world.magicline.Main.fragments.ride.swipeRecyclerview.ViewItem
import com.world.magicline.Main.fragments.selection.selectRecyclerview.SelectItem
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.math.BigInteger
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/*
* 공통 변수/상수
* */
object Prop {
    val serverUrl: String = "http://15.165.28.140:8000"
    val FCM_MSG_CODE = 112
    const val ADD_WAIT_CODE = 1110
    const val ADD_RESERVATION_CODE = 1111
    const val TUTORIAL_CODE = 11
    const val NFC_TAGGING = 1112
    const val TICKET_POPUP_CODE = 111
    const val RECOMMEND_CODE = 110
    const val TAG = "GHOST"
    var fcmTokenId: String? = null
    val SECOND: Int = 1000

    //TICKET 화면
    var user_nfc: String? = null
    var ticketCode: String? = null
    var registDate: BigInteger? = null
    var registDateStr: String? = null

    //ATTRACTION 화면
    var waitAttractionSize: Int? = null
    var resvAttractionSize: Int? = null
    var totalSize: Int? = null

    /*var ride_Code : Int? = null
    var ride_Name : String? = null
    var ride_Personnel : Int? = null
    var runTime : Int? = null
    var startTime : String? = null
    var endTime : String? = null
    var ride_Location : String? = null
    var ride_Coordinate : String? = null
    var ride_ImgUrl : String? = null
    var ride_Info : String? = null
    var waitMinute : Int? = null*/

    //Wait Attraction CODE
    var wait_attr_code: Int? = null
    var attraction: List<ViewItem>? = null
    var reservationList: List<Int>? = null
    var reservationInfoList: List<SelectItem>? = null

    val client = OkHttpClient.Builder().apply {
        readTimeout(5, TimeUnit.SECONDS)
        writeTimeout(5, TimeUnit.SECONDS)
        connectTimeout(5, TimeUnit.SECONDS)

    }
    // Retrofit 객체 생성
    val retrofit: Retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(serverUrl)
            .client(client.build())
            .build()

    // 날짜 출력 Format
    var dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREAN)

    /* 1. 티켓 등록 화면 : 티켓 번호 - > 서버 */
    data class RegistTicketData(val ticketCode: String, val nfcUid: String, var tokenId: String)   //REQ DATA

    data class TicketResultData(var result: String, var registDate: BigInteger)     //RES DATA


    /* 놀이기구 리스트 화면*/
    //* 2. 모든 놀이기구의 data (대기 시간 포함)
    data class AllRideData(val attr_code: Int,
                           val name: String,
                           val personnel: Int,
                           val run_time: Int,
                           val start_time: String,
                           val end_time: String,
                           val location: String,
                           val coordinate: String,
                           val img_url: String,
                           val info: String,
                           val wait_minute: Int)                                //RES DATA

    //* 3. 대기 신청중인 놀이기구
    data class WaitRideCodeData(val nfcUid: String)                             //REQ DATA

    data class WaitRideResultData(val attr_code: Int)                          //RES DATA

    //* 3-1. 대기 신청중인 놀이기구 정보
    data class WaitRideInfoCodeData(val nfcUid: String)

    data class WaitRideInfoResultData(val attr_code: Int,
                                      val name: String,
                                      val location: String,
                                      val img_url: String)

    /* 3-2 대기 신청중인 놀이기구의 잔여 대기 시간 정보 */
    data class WaitMinuteOfWaitAttrData(val nfcUid: String, val attrCode: Int)
    data class WaitMinuteInfoData(val count: Int, val wait_minute: Int)

    //* 4. 예약 신청중인 놀이기구
    data class ResvRideCodeData(val nfcUid: String)

    data class ResvRideResultData(val attr_code: Int,
                                  val reservation_order: Int)

    //* 4-1.  예약 신청중인 놀이기구 정보
    data class ResvRideInfoCodeData(val nfcUid: String)                        //REQ DATA

    data class ResvRideInfoResultData(val attr_code: Int,
                                      val reservation_order: Int,
                                      val name: String,
                                      val location: String,
                                      val coordinate: String,
                                      val img_url: String,
                                      val wait_minute: Int)                   //RES DATA

    //* 대기 신청 및 예약 신청 (버튼 클릭시 데이터 전송)
    // 5. 대기 신청 요청
    data class AddWaitData(val nfcUid: String, val attrCode: Int)             //REQ DATA

    data class AddWaitResultData(var result: String)                           //RES DATA

    // 6. 예약 신청 요청
    data class AddResvData(val nfcUid: String, val attrCode: Int)             //REQ DATA

    data class ResultData(var result: String)

    /* 현재상황 화면 */
    //* 7. 대기 신청된 놀이기구 삭제
    data class RemWaitData(val nfcUid: String)                                //REQ DATA

    //* 8. 예약 신청된 놀이기구 삭제
    data class RemResvData(val nfcUid: String, val reservationOrder: Int)      //REQ DATA

    //* 9. 예약 신청된 놀이기구 변경
    data class ChangeResvData(val attrCodes: ArrayList<Integer>, val nfcUid: String)

    //* 10. 예약 신청된 놀이기구 추천
    data class RecomResvData(val attrCodes: ArrayList<ResvRideResultData>)

    data class RecomResvResultData(val attr_code: Int,
                                   val name : String,
                                   val reservation_order: Int,
                                   val expect_wait_minute: Int)

    /* 11. NFC 태깅 진행 */
    data class NfcTaggingData(val nfcUid: String, val attrCode: Int)

    data class TagResultData(var result: String)

    // 티켓 사전 등록 조회
    data class RegisteredTodayTicketData(val nfcUid: String)

    data class RegisteredResultData(val result : String, var ticket_code: String, var reg_date: BigInteger)

    // 공지사항 가져오기
    data class NoticeData(val title: String, val context: String, var reg_date: BigInteger, val notice_img_url : String)    // 대소문자 구분 되나?
    data class EventBannerData(val title: String, val bannerImgUrl: String)

    // 분실물 데이터 가져오기
    data class LostsData(val classification: String, val name: String, val location: String, var get_date: BigInteger)
}