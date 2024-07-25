package serena.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log

class OnboardingNavigationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", null)

        if (userId != null) {
            Log.d("OnboardingNavigation", "User ID: $userId found in SharedPreferences")
            navigateBasedOnOnboarding(userId)
        } else {
            Log.d("OnboardingNavigation", "User ID not found, redirecting to SignInActivity")
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun navigateBasedOnOnboarding(userId: String) {
        Log.d("OnboardingNavigation", "Fetching onboarding status for User ID: $userId")
        val apiService = ApiClient.getClient(this).create(ApiService::class.java)
        apiService.getOnboardingStatus(userId).enqueue(object : Callback<OnboardingStatusResponse> {
            override fun onResponse(call: Call<OnboardingStatusResponse>, response: Response<OnboardingStatusResponse>) {
                if (response.isSuccessful) {
                    val onboarding = response.body()?.onboarding ?: 0
                    Log.d("OnboardingNavigation", "Onboarding status retrieved: $onboarding")
                    val intent = when (onboarding) {
                        0 -> Intent(this@OnboardingNavigationActivity, PersonalDetailsActivity::class.java)
                        1 -> Intent(this@OnboardingNavigationActivity, SymptomSelectionActivity::class.java)
                        2 -> Intent(this@OnboardingNavigationActivity, SwipePreferencesActivity::class.java)
                        3 -> Intent(this@OnboardingNavigationActivity, HomePageActivity::class.java)
                        else -> Intent(this@OnboardingNavigationActivity, PersonalDetailsActivity::class.java)
                    }
                    startActivity(intent)
                    finish()
                } else {
                    val errorMsg = response.errorBody()?.string()
                    Log.e("OnboardingNavigation", "Failed to fetch onboarding status: $errorMsg")
                    Toast.makeText(this@OnboardingNavigationActivity, "Failed to fetch onboarding status", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<OnboardingStatusResponse>, t: Throwable) {
                Log.e("OnboardingNavigation", "Failed to fetch onboarding status: ${t.message}", t)
                Toast.makeText(this@OnboardingNavigationActivity, "Failed to fetch onboarding status: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
