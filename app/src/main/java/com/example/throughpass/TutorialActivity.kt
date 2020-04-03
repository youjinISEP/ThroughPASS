package com.example.throughpass

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_tutorial.*


class TutorialActivity : AppCompatActivity() {
    private val pageCount = 5
    var dots = arrayOfNulls<TextView>(pageCount)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)

        ViewPagerTutorial.adapter = MyPagerAdapter(applicationContext, pageCount)
        addBottomDots(0)

        // 건너뛰기 버튼 클릭 리스너
        btnSkip.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }

        // 다음 버튼 클릭 리스너
        btnNext.setOnClickListener {
            if(ViewPagerTutorial.currentItem + 1 < pageCount) {
                ViewPagerTutorial.currentItem = ViewPagerTutorial.currentItem + 1;
            }
            else {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }

        // 뷰페이지 페이지 변경 이벤트 리스너
        ViewPagerTutorial.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                addBottomDots(position)

                if(position == pageCount - 1) {
                    btnNext.text = getString(R.string.start)
                    btnSkip.visibility = View.GONE
                }
                else {
                    btnNext.text = getString(R.string.next)
                    btnSkip.visibility = View.VISIBLE
                }
            }
        })
    }

    fun addBottomDots(position : Int) {
        layoutDots.removeAllViews()
        for(i in 0 until pageCount) {
            dots[i] = (TextView(this))
            dots[i]!!.text = Html.fromHtml("&#8226;")
            dots[i]!!.textSize = 35F
            dots[i]!!.setTextColor(resources.getColor(R.color.colorDarkGray))
            layoutDots.addView(dots[i])
        }
        dots[position]!!.setTextColor(resources.getColor(R.color.colorLotteLogo))
    }


    class MyPagerAdapter() : PagerAdapter() {
        // 늦은 초기화. 이 덕분에 메모리 낭비(놀고있기) 방지 가능
        private lateinit var layoutInflater: LayoutInflater
        private lateinit var context: Context
        private var myCount: Int = 0

        constructor(context: Context, count : Int) : this() {
            this.context = context
            this.myCount = count
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var view = layoutInflater.inflate(R.layout.tutorial, container, false)
            var imageViewTutorial : ImageView = view.findViewById(R.id.imageViewTutorial)

            setTutorialImage(imageViewTutorial, position)
            container.addView(view)

            return view
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

        // 인덱스(순서)에 맞게 튜토리얼 이미지 삽입
        private fun setTutorialImage(imageView: ImageView, index : Int) {
            when(index) {
                0 -> { imageView.setImageResource(R.drawable.tutorial1) }
                1 -> { imageView.setImageResource(R.drawable.tutorial2) }
                2 -> { imageView.setImageResource(R.drawable.tutorial3) }
                3 -> { imageView.setImageResource(R.drawable.tutorial4) }
                4 -> { imageView.setImageResource(R.drawable.tutorial5) }
            }
        }
    }

    //안드로이드 백버튼 막기
    override fun onBackPressed() {
        return
    }
}
