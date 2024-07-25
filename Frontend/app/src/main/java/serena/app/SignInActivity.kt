package serena.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log // Import Log

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val emailEditText: EditText = findViewById(R.id.emailEditText)
        val passwordEditText: EditText = findViewById(R.id.passwordEditText)
        val btnSignIn: Button = findViewById(R.id.btnSignIn)

        val email = intent.getStringExtra("email")
        val password = intent.getStringExtra("password")

        if (email != null && password != null) {
            loginUser(email, password)
        }

        btnSignIn.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        checkIfLoggedIn()
    }

    private fun loginUser(email: String, password: String) {
        val apiService = ApiClient.getClient(this).create(ApiService::class.java)
        val user = User(email, password)
        apiService.login(user).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val token = response.body()?.token ?: ""
                    val userId = response.body()?.userId ?: ""
                    Log.d("SignInActivity", "User ID: $userId")  // Log userId during login
                    saveTokenAndUserId(token, userId)
                    navigateToHomeHub()
                } else {
                    Toast.makeText(this@SignInActivity, "Login Failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@SignInActivity, "Login Failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun saveTokenAndUserId(token: String, userId: String) {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("token", token)
        editor.putString("userId", userId)
        editor.apply()
    }

    private fun checkIfLoggedIn() {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        if (token != null) {
            navigateToHomeHub()
        }
    }

    private fun navigateToHomeHub() {
        val intent = Intent(this, OnboardingNavigationActivity::class.java)
        startActivity(intent)
        finish()
    }
}
