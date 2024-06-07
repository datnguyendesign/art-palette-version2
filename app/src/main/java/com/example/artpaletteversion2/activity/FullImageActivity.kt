package com.example.artpaletteversion2.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.artpaletteversion2.R

class FullImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_image)

        val imageView = findViewById<ImageView>(R.id.myZoomageView)
        Glide.with(this)
            .load(intent.getStringExtra("image"))
            .into(imageView)
    }
}