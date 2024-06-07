package com.example.artpaletteversion2.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.artpaletteversion2.R
import com.ismaeldivita.chipnavigation.ChipNavigationBar

class SavedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved)

        val menu = findViewById<ChipNavigationBar>(R.id.menu)
        menu.setItemSelected(R.id.saved)
        menu.setOnItemSelectedListener {
            if (it == R.id.home) {
                startActivity(Intent(this@SavedActivity, MainActivity::class.java))
                this@SavedActivity.overridePendingTransition(
                    R.anim.animate_fade_enter,
                    R.anim.animate_fade_exit
                )
            }
            if (it == R.id.explore) {
                startActivity(Intent(this@SavedActivity, ExploreActivity::class.java))
                this@SavedActivity.overridePendingTransition(
                    R.anim.animate_fade_enter,
                    R.anim.animate_fade_exit
                )
            }
            if (it == R.id.add) {
                startActivity(Intent(this@SavedActivity, AddImageActivity::class.java))
                this@SavedActivity.overridePendingTransition(
                    R.anim.animate_fade_enter,
                    R.anim.animate_fade_exit
                )
            }
            if (it == R.id.account) {
                startActivity(Intent(this@SavedActivity, ProfileActivity::class.java))
                this@SavedActivity.overridePendingTransition(
                    R.anim.animate_fade_enter,
                    R.anim.animate_fade_exit
                )
            }
        }
    }
}