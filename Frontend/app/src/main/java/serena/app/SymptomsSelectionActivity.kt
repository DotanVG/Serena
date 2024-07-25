package serena.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.setMargins
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SymptomSelectionActivity : AppCompatActivity() {
    private val symptoms = arrayOf(
        "Acne", "Pelvic pain", "Pain during or after sex", "Constipation/diarrhoea",
        "Pain during bowel movements or urination", "Blood clots during period", "Hair growth",
        "Hair loss", "Muscle weakness", "Spotting", "Excessive bleeding", "Irregular cycles",
        "Fatigue", "Weight gain", "Mood changes", "Anxiety"
    )

    private val selectedSymptoms = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_symptom_selection)

        val symptomsContainer: GridLayout = findViewById(R.id.symptomsContainer)
        val btnNext: Button = findViewById(R.id.btnNext)

        symptoms.forEach { symptom ->
            val button = Button(this).apply {
                text = symptom
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = ViewGroup.LayoutParams.WRAP_CONTENT
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    setMargins(8)
                }
                setBackgroundResource(R.drawable.button_selector)
                setTextColor(ContextCompat.getColor(context, android.R.color.white))
                setOnClickListener {
                    if (!isSelected && selectedSymptoms.size >= 5) {
                        Toast.makeText(this@SymptomSelectionActivity, "You can select up to 5 symptoms only", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    isSelected = !isSelected
                    if (isSelected) {
                        selectedSymptoms.add(symptom)
                    } else {
                        selectedSymptoms.remove(symptom)
                    }
                }
            }
            symptomsContainer.addView(button)
        }

        btnNext.setOnClickListener {
            saveSymptoms()
        }
    }

    private fun saveSymptoms() {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", null)

        if (userId == null) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedSymptoms.isEmpty()) {
            Toast.makeText(this, "Please select at least one symptom", Toast.LENGTH_SHORT).show()
            return
        }

        val apiService = ApiClient.getClient(this).create(ApiService::class.java)
        val symptomsRequest = SymptomsRequest(userId, selectedSymptoms, 2) // Set onboarding to 2
        apiService.saveSymptoms(symptomsRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("SymptomSelection", "Symptoms saved successfully")
                    navigateToNext()
                } else {
                    val errorMsg = response.errorBody()?.string()
                    Toast.makeText(this@SymptomSelectionActivity, "Failed to save symptoms: $errorMsg", Toast.LENGTH_SHORT).show()
                    Log.e("SymptomSelection", "Failed to save symptoms: $errorMsg")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@SymptomSelectionActivity, "Failed to save symptoms: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("SymptomSelection", "Failed to save symptoms: ${t.message}")
            }
        })
    }

    private fun navigateToNext() {
        val intent = Intent(this@SymptomSelectionActivity, SwipePreferencesActivity::class.java)
        startActivity(intent)
        finish()
    }
}
