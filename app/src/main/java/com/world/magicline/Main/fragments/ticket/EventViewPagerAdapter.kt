package com.world.magicline.Main.fragments.ticket

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.world.magicline.R
import com.world.magicline.obj.Prop

class EventViewPagerAdapter() : PagerAdapter() {
    // 늦은 초기화. 이 덕분에 메모리 낭비(놀고있기) 방지 가능
    private lateinit var layoutInflater: LayoutInflater
    private lateinit var context: Context
    private lateinit var eventBanners: ArrayList<Prop.NoticeData>
    private var myCount: Int = 0

    constructor(context: Context, count : Int, array: ArrayList<Prop.NoticeData>) : this() {
        this.context = context
        this.myCount = count
        this.eventBanners = array
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var view = layoutInflater.inflate(R.layout.event_item, container, false)
        var eventImgView : ImageView = view.findViewById(R.id.eventBannerImg)
        var titleTextView : TextView = view.findViewById(R.id.eventTitleText)

        // String a = "http://adventure.lotteworld.com/image/2018/7/201807251058185011_1350.jpg";
        Glide.with(view)
                .load(getItem(position).notice_img_url)
                .dontTransform()
                .apply(RequestOptions().override(1300, 500)) //                .centerCrop()
                .into(eventImgView)
        container.addView(view)
        titleTextView.text = getItem(position).title

        return view
    }

    fun getItem(position : Int) : Prop.NoticeData {
        return this.eventBanners[position]
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return myCount
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}