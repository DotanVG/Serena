package serena.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignIn_UpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin_up)

        val btnSignIn: Button = findViewById(R.id.btnSignIn)
        val btnSignUp: Button = findViewById(R.id.btnSignUp)

        btnSignIn.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        btnSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signUpUser(email: String) {
        val apiService = ApiClient.getClient(this).create(ApiService::class.java)
        val user = User(email, "default_password")
        apiService.signUp(user).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@SignIn_UpActivity, "Sign Up Successful", Toast.LENGTH_SHORT).show()
                    Log.d("SignIn_UpActivity", "Sign Up Successful: ${response.body()}")
                } else {
                    val errorMsg = response.errorBody()?.string()
                    Toast.makeText(this@SignIn_UpActivity, "Sign Up Failed: $errorMsg", Toast.LENGTH_SHORT).show()
                    Log.e("SignIn_UpActivity", "Sign Up Failed: $errorMsg")
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Toast.makeText(this@SignIn_UpActivity, "Sign Up Failed: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("SignIn_UpActivity", "Sign Up Failed: ${t.message}")
            }
        })
    }
}
