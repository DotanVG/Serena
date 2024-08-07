package serena.app

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView

class CalendarAdapter(
    private val days: List<String>,
    private val fragmentManager: FragmentManager,
    private val monthYear: String, // Add month-year parameter
    private val daysMap: Map<String, Map<String, String>>, // Add daysMap parameter
    private val onDayClickListener: (String) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_day, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val day = days[position]
        holder.dayButton.text = day

        // Color the day button frame based on whether the day exists in the daysMap
        val drawable = holder.dayButton.background as GradientDrawable
        if (daysMap.containsKey(day)) {
            drawable.setStroke(5, ContextCompat.getColor(holder.dayButton.context, R.color.green))
        } else {
            drawable.setStroke(5, ContextCompat.getColor(holder.dayButton.context, R.color.orange))
        }

        holder.dayButton.setOnClickListener {
            // Show the dialog when a day button is clicked
            val dialog = HealthTrackingDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt("day", day.toInt())
                    putString("monthYear", monthYear)
                    putString("data", daysMap[day]?.get("data") ?: "0".repeat(32))
                }
            }
            dialog.show(fragmentManager, "HealthTrackingDialog")
            onDayClickListener(day)
        }
    }

    override fun getItemCount(): Int {
        return days.size
    }

    class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayButton: Button = itemView.findViewById(R.id.calendar_day_button)
    }
}
