package serena.app

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

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val emailEditText: EditText = findViewById(R.id.emailEditText)
        val passwordEditText: EditText = findViewById(R.id.passwordEditText)
        val btnSignUp: Button = findViewById(R.id.btnSignUp)

        btnSignUp.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                signUpUser(email, password)
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signUpUser(email: String, password: String) {
        val apiService = ApiClient.getClient(this).create(ApiService::class.java)
        val user = User(email, password)
        apiService.signUp(user).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@SignUpActivity, "Sign Up Successful", Toast.LENGTH_SHORT).show()
                    navigateToSignIn(email, password)
                } else {
                    val errorMsg = response.errorBody()?.string()
                    Toast.makeText(this@SignUpActivity, "Sign Up Failed: $errorMsg", Toast.LENGTH_SHORT).show()
                    Log.e("SignUpActivity", "Sign Up Failed: $errorMsg")
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Toast.makeText(this@SignUpActivity, "Sign Up Failed: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("SignUpActivity", "Sign Up Failed: ${t.message}")
            }
        })
    }

    private fun navigateToSignIn(email: String, password: String) {
        val intent = Intent(this, SignInActivity::class.java)
        intent.putExtra("email", email)
        intent.putExtra("password", password)
        startActivity(intent)
        finish()
    }
}
