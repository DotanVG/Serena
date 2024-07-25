package serena.app

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.slider.Slider

class DailyFormActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dailyform)

//        val backButton: ImageButton = findViewById(R.id.back_button)
        val painScaleSlider: Slider = findViewById(R.id.pain_scale_slider)

//        backButton.setOnClickListener {
//            finish()
//        }

        painScaleSlider.addOnChangeListener { slider, value, fromUser ->
            // Handle slider value change
        }
    }
}
