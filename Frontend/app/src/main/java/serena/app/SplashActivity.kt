package serena.app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Simulate a loading time
        Handler(Looper.getMainLooper()).postDelayed({
            checkUserLoggedIn()
        }, 3000) // Delay for 3 seconds
    }

    private fun checkUserLoggedIn() {
        val token = getTokenFromPreferences()
        if (token.isNullOrEmpty()) {
            navigateToSignInSignUp()
            return
        }

//        val apiService = ApiClient.instance.create(ApiService::class.java)
//        apiService.checkLoggedIn("Bearer $token").enqueue(object : Callback<AuthResponse> {
//            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
//                if (response.isSuccessful && response.body()?.loggedIn == true) {
//                    Log.d("SplashActivity", "User is logged in.")
//                    navigateToHomeHub()
//                } else {
//                    Log.d("SplashActivity", "User is not logged in.")
//                    navigateToSignInSignUp()
//                }
//            }
//
//            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
//                Log.e("SplashActivity", "Failed to check login status: ${t.message}")
//                Toast.makeText(this@SplashActivity, "Failed to check login status", Toast.LENGTH_SHORT).show()
//                navigateToSignInSignUp()
//            }
//        })
    }

    private fun getTokenFromPreferences(): String? {
        val sharedPreferences = getSharedPreferences("PreggoAppPrefs", MODE_PRIVATE)
        return sharedPreferences.getString("auth_token", null)
    }

    private fun navigateToHomeHub() {
        val intent = Intent(this, HomePageActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToSignInSignUp() {
        val intent = Intent(this, SignIn_UpActivity::class.java)
        startActivity(intent)
        finish()
    }
}
