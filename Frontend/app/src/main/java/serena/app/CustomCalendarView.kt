package serena.app

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import serena.app.R
import java.text.SimpleDateFormat
import java.util.*

class CustomCalendarView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val calendar: Calendar = Calendar.getInstance()
    private lateinit var currentDateTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var buttonPreviousMonth: ImageButton
    private lateinit var buttonNextMonth: ImageButton

    init {
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.view_custom_calendar, this, true)
        setupUI()
        setupCalendar()
        setupNavigation()
    }

    private fun setupUI() {
        currentDateTextView = findViewById(R.id.current_date_text)
        recyclerView = findViewById(R.id.calendar_recycler_view)
        recyclerView.layoutManager = GridLayoutManager(context, 7) // 7 columns for days of the week
        buttonPreviousMonth = findViewById(R.id.buttonPreviousMonth)
        buttonNextMonth = findViewById(R.id.buttonNextMonth)
    }

    private fun setupCalendar() {
        updateMonthLabel()
        val daysInMonth = getDaysInMonth()
        recyclerView.adapter = CalendarAdapter(daysInMonth) { day ->
            Toast.makeText(context, "Clicked on day: $day", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupNavigation() {
        buttonPreviousMonth.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            setupCalendar()
        }

        buttonNextMonth.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            setupCalendar()
        }
    }

    private fun updateMonthLabel() {
        val sdf = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        currentDateTextView.text = sdf.format(calendar.time)
    }

    private fun getDaysInMonth(): List<String> {
        val daysInMonth = mutableListOf<String>()
        val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (i in 1..maxDay) {
            daysInMonth.add(i.toString())
        }
        return daysInMonth
    }
}
