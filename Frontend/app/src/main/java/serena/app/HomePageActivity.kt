package serena.app

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView.State
import androidx.recyclerview.widget.RecyclerView.ItemAnimator

class HomePageActivity : AppCompatActivity() {
    private lateinit var customCalendarView: CustomCalendarView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        // Setup custom toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Setup carousel
        val carouselRecyclerView: RecyclerView = findViewById(R.id.carousel_recyclerview)
        val items = listOf(
            CarouselItem(R.mipmap.ic_heartbeat, "72"),
            CarouselItem(R.mipmap.ic_breath, "21"),
            CarouselItem(R.mipmap.ic_sleep, "Good"),
            CarouselItem(R.mipmap.ic_workout, "Very Good")
        )
        val adapter = CarouselAdapter(items)
        carouselRecyclerView.adapter = adapter
        carouselRecyclerView.layoutManager = LinearLayoutManager(this, HORIZONTAL, false)

        // Add item decoration to center items
        val pageMarginPx = resources.getDimensionPixelOffset(R.dimen.pageMargin)
        val offsetPx = resources.getDimensionPixelOffset(R.dimen.offset)
        carouselRecyclerView.addItemDecoration(object : ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: State) {
                val position = parent.getChildAdapterPosition(view)
                outRect.left = if (position == 0) offsetPx else pageMarginPx / 2
                outRect.right = if (position == state.itemCount - 1) offsetPx else pageMarginPx / 2
            }
        })

        // Add item animator to create a scaling effect
        carouselRecyclerView.itemAnimator = object : ItemAnimator() {
            override fun animateDisappearance(holder: RecyclerView.ViewHolder, preLayoutInfo: ItemHolderInfo, postLayoutInfo: ItemHolderInfo?): Boolean = false
            override fun animateAppearance(holder: RecyclerView.ViewHolder, preLayoutInfo: ItemHolderInfo?, postLayoutInfo: ItemHolderInfo): Boolean = false
            override fun animatePersistence(holder: RecyclerView.ViewHolder, preLayoutInfo: ItemHolderInfo, postLayoutInfo: ItemHolderInfo): Boolean = false
            override fun animateChange(oldHolder: RecyclerView.ViewHolder, newHolder: RecyclerView.ViewHolder, preInfo: ItemHolderInfo, postInfo: ItemHolderInfo): Boolean = false
            override fun runPendingAnimations() {}
            override fun endAnimation(item: RecyclerView.ViewHolder) {}
            override fun endAnimations() {}
            override fun isRunning(): Boolean = false

            fun onAddFinished(item: RecyclerView.ViewHolder) {
                item.itemView.apply {
                    scaleY = 0.85f
                    scaleX = 0.85f
                    animate().scaleY(1f).scaleX(1f).setDuration(300).start()
                }
            }


        }
    }

    fun refreshCalendarView() {
        customCalendarView.fetchMonthlyData()
    }
}
