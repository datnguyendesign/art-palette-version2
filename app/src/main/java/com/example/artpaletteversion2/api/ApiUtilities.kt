package com.example.artpaletteversion2.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiUtilities {
    companion object {
        private const val BASE_URL = "https://api.unsplash.com"
        const val API_KEY = "FG2Z1lj9az27jG0nAcJh1kaSdT624uwI55jEi4Z9q_Y"

        private var retrofit: Retrofit? = null
        fun getApiInterface(): ApiInterface? {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit?.create(ApiInterface::class.java)
        }
    }

}