package serena.app

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class HealthTrackingDialogFragment : DialogFragment() {

    private lateinit var apiService: ApiService
    private lateinit var userId: String
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it, R.style.CustomDialog)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_health_tracking, null)

            // Initialize ApiService
            apiService = ApiClient.getClient(requireContext()).create(ApiService::class.java)

            // Retrieve user ID from shared preferences
            val sharedPreferences = activity?.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            userId = sharedPreferences?.getString("userId", null) ?: run {
                Toast.makeText(activity, "User ID not found", Toast.LENGTH_SHORT).show()
                dismiss()
                return builder.create()
            }

            Log.d("HealthTrackingDialog", "UserID: $userId")

            // Get the day, month-year, and data from arguments
            val day = arguments?.getInt("day") ?: 1
            val monthYear = arguments?.getString("monthYear") ?: ""
            val binaryString = arguments?.getString("data") ?: "0".repeat(32)

            Log.d("HealthTrackingDialog", "Day: $day")
            Log.d("HealthTrackingDialog", "Month-Year: $monthYear")
            Log.d("HealthTrackingDialog", "BinaryString: $binaryString")

            // Find your checkboxes, seekbar, and edit text
            val spottingCheckBox = view.findViewById<CheckBox>(R.id.checkbox_spotting)
            val bleedingCheckBox = view.findViewById<CheckBox>(R.id.checkbox_bleeding)
            val constipationCheckBox = view.findViewById<CheckBox>(R.id.checkbox_constipation)
            val diarrheaCheckBox = view.findViewById<CheckBox>(R.id.checkbox_diarrhea)
            val bloatingCheckBox = view.findViewById<CheckBox>(R.id.checkbox_bloating)
            val cravingsCheckBox = view.findViewById<CheckBox>(R.id.checkbox_cravings)
            val crampsCheckBox = view.findViewById<CheckBox>(R.id.checkbox_cramps)
            val nauseaCheckBox = view.findViewById<CheckBox>(R.id.checkbox_nausea)
            val painScaleSeekBar = view.findViewById<SeekBar>(R.id.seekbar_pain_scale)
            val notesEditText = view.findViewById<EditText>(R.id.etNotes)
            val dateTextView = view.findViewById<TextView>(R.id.tvDate)

            // Mood icons and checkboxes
            val moodItems = listOf(
                Triple(R.id.icon_happy, R.id.checkbox_happy, R.id.layout_happy),
                Triple(R.id.icon_confident, R.id.checkbox_confident, R.id.layout_confident),
                Triple(R.id.icon_calm, R.id.checkbox_calm, R.id.layout_calm),
                Triple(R.id.icon_energetic, R.id.checkbox_energetic, R.id.layout_energetic),
                Triple(R.id.icon_excited, R.id.checkbox_excited, R.id.layout_excited),
                Triple(R.id.icon_pms, R.id.checkbox_pms, R.id.layout_pms),
                Triple(R.id.icon_mood_swings, R.id.checkbox_mood_swings, R.id.layout_mood_swings),
                Triple(R.id.icon_irritable, R.id.checkbox_irritable, R.id.layout_irritable),
                Triple(R.id.icon_anxious, R.id.checkbox_anxious, R.id.layout_anxious),
                Triple(R.id.icon_stressed, R.id.checkbox_stressed, R.id.layout_stressed),
                Triple(R.id.icon_tired, R.id.checkbox_tired, R.id.layout_tired),
                Triple(R.id.icon_sensitive, R.id.checkbox_sensitive, R.id.layout_sensitive),
                Triple(R.id.icon_numb, R.id.checkbox_numb, R.id.layout_numb),
                Triple(R.id.icon_sad, R.id.checkbox_sad, R.id.layout_sad),
                Triple(R.id.icon_angry, R.id.checkbox_angry, R.id.layout_angry),
                Triple(R.id.icon_unfocused, R.id.checkbox_unfocused, R.id.layout_unfocused),
                Triple(R.id.icon_self_critical, R.id.checkbox_self_critical, R.id.layout_self_critical),
                Triple(R.id.icon_guilty, R.id.checkbox_guilty, R.id.layout_guilty),
                Triple(R.id.icon_obsessive_thoughts, R.id.checkbox_obsessive_thoughts, R.id.layout_obsessive_thoughts),
                Triple(R.id.icon_confused, R.id.checkbox_confused, R.id.layout_confused)
            )

            // Format the date string correctly and set the date in the TextView
            val sdfInput = SimpleDateFormat("MM-yyyy-d", Locale.getDefault())
            val sdfOutput = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
            val date = sdfInput.parse("$monthYear-$day")
            dateTextView.text = sdfOutput.format(date)

            // Update UI based on the binary string
            if (binaryString[1] == '1') spottingCheckBox.isChecked = true
            if (binaryString[2] == '1') bleedingCheckBox.isChecked = true
            if (binaryString[3] == '1') constipationCheckBox.isChecked = true
            if (binaryString[4] == '1') diarrheaCheckBox.isChecked = true
            if (binaryString[5] == '1') bloatingCheckBox.isChecked = true
            if (binaryString[6] == '1') cravingsCheckBox.isChecked = true
            if (binaryString[7] == '1') crampsCheckBox.isChecked = true
            if (binaryString[8] == '1') nauseaCheckBox.isChecked = true

            val painScaleBinary = binaryString.substring(9, 12)
            painScaleSeekBar.progress = Integer.parseInt(painScaleBinary, 2)

            moodItems.forEachIndexed { index, (_, checkboxId, layoutId) ->
                val checkbox = view.findViewById<CheckBox>(checkboxId)
                val layout = view.findViewById<LinearLayout>(layoutId)
                if (binaryString[index + 12] == '1') {
                    checkbox.isChecked = true
                    updateBackground(layout, true)
                    Log.d("HealthTrackingDialog", "Mood $index: ${checkbox.text}")
                } else {
                    updateBackground(layout, false)
                }
            }

            // Set mutual exclusivity for spotting and bleeding checkboxes
            spottingCheckBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) bleedingCheckBox.isChecked = false
            }
            bleedingCheckBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) spottingCheckBox.isChecked = false
            }

            // Set click listeners for mood checkboxes
            moodItems.forEach { (_, checkboxId, layoutId) ->
                val layout = view.findViewById<LinearLayout>(layoutId)
                val checkbox = view.findViewById<CheckBox>(checkboxId)
                layout.setOnClickListener {
                    checkbox.isChecked = !checkbox.isChecked
                    updateBackground(layout, checkbox.isChecked)
                }
            }

            builder.setView(view)
                .setPositiveButton("Save") { dialog, id ->
                    val newBinaryString = StringBuilder("0".repeat(32))

                    // Update binary string based on checkbox states
                    if (spottingCheckBox.isChecked) newBinaryString.setCharAt(1, '1')
                    if (bleedingCheckBox.isChecked) newBinaryString.setCharAt(2, '1')
                    if (constipationCheckBox.isChecked) newBinaryString.setCharAt(3, '1')
                    if (diarrheaCheckBox.isChecked) newBinaryString.setCharAt(4, '1')
                    if (bloatingCheckBox.isChecked) newBinaryString.setCharAt(5, '1')
                    if (cravingsCheckBox.isChecked) newBinaryString.setCharAt(6, '1')
                    if (crampsCheckBox.isChecked) newBinaryString.setCharAt(7, '1')
                    if (nauseaCheckBox.isChecked) newBinaryString.setCharAt(8, '1')

                    val painScaleBinary = painScaleSeekBar.progress.toString(2).padStart(3, '0')
                    for (i in 0..2) {
                        newBinaryString.setCharAt(9 + i, painScaleBinary[i])
                    }

                    // Update binary string based on mood checkboxes
                    moodItems.forEachIndexed { index, (_, checkboxId, _) ->
                        val checkbox = view.findViewById<CheckBox>(checkboxId)
                        if (checkbox.isChecked) newBinaryString.setCharAt(index + 12, '1')
                    }

                    val notes = notesEditText.text.toString()

                    val healthDataRequest = HealthDataRequest(
                        userId = userId,
                        month = monthYear,
                        day = day,
                        data = newBinaryString.toString(),
                        notes = notes
                    )

                    Log.d("HealthTrackingDialog", "HealthDataRequest: $healthDataRequest")

                    apiService.saveHealthData(healthDataRequest).enqueue(object : Callback<HealthDataResponse> {
                        override fun onResponse(call: Call<HealthDataResponse>, response: Response<HealthDataResponse>) {
                            if (response.isSuccessful) {
                                Log.d("API", "Data saved successfully")
                            } else {
                                Log.e("API", "Failed to save data: ${response.errorBody()?.string()}")
                                Log.d("API", "Response code: ${response.code()}")
                                Log.d("API", "Response message: ${response.message()}")
                            }
                        }

                        override fun onFailure(call: Call<HealthDataResponse>, t: Throwable) {
                            Log.e("API", "API call failed", t)
                        }
                    })


                }
                .setNegativeButton("Cancel") { dialog, id ->
                    dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun updateBackground(layout: View, isChecked: Boolean) {
        layout.setBackgroundResource(if (isChecked) R.drawable.selected_background else 0)
    }
    }
