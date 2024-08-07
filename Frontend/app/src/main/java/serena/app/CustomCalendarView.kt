package serena.app

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
    private lateinit var apiService: ApiService

    init {
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.view_custom_calendar, this, true)
        setupUI()
        setupCalendar()
        setupNavigation()
        setupApiService()
        fetchMonthlyData()
    }

    private fun setupUI() {
        currentDateTextView = findViewById(R.id.current_date_text)
        recyclerView = findViewById(R.id.calendar_recycler_view)
        recyclerView.layoutManager = GridLayoutManager(context, 7) // 7 columns for days of the week
        buttonPreviousMonth = findViewById(R.id.buttonPreviousMonth)
        buttonNextMonth = findViewById(R.id.buttonNextMonth)
    }

    private fun setupCalendar(daysMap: Map<String, Map<String, String>> = emptyMap()) {
        updateMonthLabel()
        val daysInMonth = getDaysInMonth()
        val fragmentManager = (context as FragmentActivity).supportFragmentManager
        val monthYear = getCurrentMonthYear()
        recyclerView.adapter = CalendarAdapter(daysInMonth, fragmentManager, monthYear, daysMap) { day ->
            Toast.makeText(context, "Clicked on day: $day", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupNavigation() {
        buttonPreviousMonth.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            fetchMonthlyData()
        }

        buttonNextMonth.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            fetchMonthlyData()
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

    private fun getCurrentMonthYear(): String {
        val sdf = SimpleDateFormat("MM-yyyy", Locale.getDefault())
        return sdf.format(calendar.time)
    }

    private fun setupApiService() {
        apiService = ApiClient.getClient(context).create(ApiService::class.java)
    }

    fun fetchMonthlyData() {
        val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", null) ?: return

        val currentMonthYear = getCurrentMonthYear()

        apiService.getHealthData(userId, currentMonthYear).enqueue(object : Callback<Map<String, Map<String, String>>> {
            override fun onResponse(call: Call<Map<String, Map<String, String>>>, response: Response<Map<String, Map<String, String>>>) {
                if (response.isSuccessful) {
                    val daysMap = response.body() ?: emptyMap()
                    Log.d("CustomCalendarView", "Days Map: $daysMap")
                    setupCalendar(daysMap)
                } else {
                    Log.e("API", "Failed to fetch data: ${response.errorBody()?.string()}")
                    setupCalendar(emptyMap())
                }
            }

            override fun onFailure(call: Call<Map<String, Map<String, String>>>, t: Throwable) {
                Log.e("API", "API call failed", t)
                setupCalendar(emptyMap())
            }
        })
    }
}
