package serena.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PersonalDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_details)

        val etName: EditText = findViewById(R.id.etName)
        val etAge: EditText = findViewById(R.id.etAge)
        val btnNext: Button = findViewById(R.id.btnNext)

        btnNext.setOnClickListener {
            val name = etName.text.toString()
            val age = etAge.text.toString()
            if (name.isNotEmpty() && age.isNotEmpty()) {
                savePersonalDetails(name, age.toInt())
            } else {
                Toast.makeText(this, "Please enter name and age", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun savePersonalDetails(name: String, age: Int) {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", null)
        if (userId == null) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show()
            return
        }
        val apiService = ApiClient.getClient(this).create(ApiService::class.java)
        val personalDetails = PersonalDetails(userId, name, age, 1) // Set onboarding to 1

        apiService.savePersonalDetails(personalDetails).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("PersonalDetails", "Details saved successfully")
                    navigateToNext()
                } else {
                    val errorMsg = response.errorBody()?.string()
                    Toast.makeText(this@PersonalDetailsActivity, "Failed to save details: $errorMsg", Toast.LENGTH_SHORT).show()
                    Log.e("PersonalDetails", "Failed to save details: $errorMsg")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@PersonalDetailsActivity, "Failed to save details: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("PersonalDetails", "Failed to save details: ${t.message}")
            }
        })
    }

    private fun navigateToNext() {
        val intent = Intent(this@PersonalDetailsActivity, SymptomSelectionActivity::class.java)
        startActivity(intent)
        finish()
    }
}
