package serena.app

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private var retrofit: Retrofit? = null

    fun getClient(context: Context): Retrofit {
        if (retrofit == null) {
            val httpClient = OkHttpClient.Builder()
            httpClient.addInterceptor { chain ->
                val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                val token = sharedPreferences.getString("token", null)
                val request: Request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                chain.proceed(request)
            }

            retrofit = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:5000/")  // Replace with your backend server address
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }
}
