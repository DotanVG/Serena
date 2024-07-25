package serena.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2

class HomePageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        // Setup custom toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Setup carousel
        val carouselViewPager: ViewPager2 = findViewById(R.id.carousel_viewpager)
        val items = listOf(
            CarouselItem(R.mipmap.ic_heartbeat, "72"),
            CarouselItem(R.mipmap.ic_breath, "21"),
            CarouselItem(R.mipmap.ic_sleep, "Good"),
            CarouselItem(R.mipmap.ic_workout, "Very Good")
        )
        val adapter = CarouselAdapter(items)
        carouselViewPager.adapter = adapter
        carouselViewPager.clipToPadding = false
        carouselViewPager.clipChildren = false
        carouselViewPager.offscreenPageLimit = 3
        carouselViewPager.getChildAt(0).overScrollMode = ViewPager2.OVER_SCROLL_NEVER

        // Apply transformation to make items appear smaller
        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - Math.abs(position)
            page.scaleY = 0.85f + r * 0.15f
            page.alpha = 0.7f + r * 0.3f
        }

        carouselViewPager.setPageTransformer(compositePageTransformer)
    }
}

