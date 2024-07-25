package serena.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SwipePreferencesActivity : AppCompatActivity() {

    private val preferences = mutableListOf(
        "Preference 1", "Preference 2", "Preference 3", "Preference 4",
        "Preference 5", "Preference 6", "Preference 7", "Preference 8",
        "Preference 9", "Preference 10"
    )
    private val selectedPreferences = mutableListOf<String>()
    private val discardedPreferences = mutableListOf<String>()
    private lateinit var adapter: PreferenceAdapter
    private lateinit var recyclerView: RecyclerView
    private var currentPosition: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_swipe_preferences)

        recyclerView = findViewById(R.id.recyclerView)
        adapter = PreferenceAdapter(preferences)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                currentPosition = viewHolder.adapterPosition
                if (direction == ItemTouchHelper.LEFT) {
                    discardedPreferences.add(preferences[currentPosition])
                } else if (direction == ItemTouchHelper.RIGHT) {
                    selectedPreferences.add(preferences[currentPosition])
                }
                preferences.removeAt(currentPosition)
                adapter.notifyItemRemoved(currentPosition)
                if (preferences.isEmpty()) {
                    Toast.makeText(this@SwipePreferencesActivity, "Good job! You've finished the onboarding process", Toast.LENGTH_SHORT).show()
                    savePreferences()
                }
            }
        })

        itemTouchHelper.attachToRecyclerView(recyclerView)

        findViewById<Button>(R.id.btnFinish).setOnClickListener {
            savePreferences()
        }

        findViewById<ImageView>(R.id.goBackArrow).setOnClickListener {
            goBackToPrevious()
        }
    }

    private fun savePreferences() {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", null)
        if (userId == null) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show()
            return
        }

        val apiService = ApiClient.getClient(this).create(ApiService::class.java)
        val preferencesRequest = PreferencesRequest(userId, selectedPreferences, 3) // Set onboarding to 3
        apiService.savePreferences(preferencesRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("SwipePreferences", "Preferences saved successfully")
                    Toast.makeText(this@SwipePreferencesActivity, "Preferences saved successfully", Toast.LENGTH_SHORT).show()
                    navigateToHomeHub()
                } else {
                    val errorMsg = response.errorBody()?.string()
                    Log.e("SwipePreferences", "Failed to save preferences: $errorMsg")
                    Toast.makeText(this@SwipePreferencesActivity, "Failed to save preferences: $errorMsg", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("SwipePreferences", "Failed to save preferences: ${t.message}")
                Toast.makeText(this@SwipePreferencesActivity, "Failed to save preferences: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateToHomeHub() {
        val intent = Intent(this@SwipePreferencesActivity, HomePageActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun goBackToPrevious() {
        if (discardedPreferences.isNotEmpty() || selectedPreferences.isNotEmpty()) {
            val lastRemoved = if (discardedPreferences.isNotEmpty()) {
                discardedPreferences.removeAt(discardedPreferences.size - 1)
            } else {
                selectedPreferences.removeAt(selectedPreferences.size - 1)
            }
            preferences.add(currentPosition, lastRemoved)
            adapter.notifyItemInserted(currentPosition)
            recyclerView.scrollToPosition(currentPosition)
        } else {
            Toast.makeText(this, "No more items to go back to.", Toast.LENGTH_SHORT).show()
        }
    }
}
