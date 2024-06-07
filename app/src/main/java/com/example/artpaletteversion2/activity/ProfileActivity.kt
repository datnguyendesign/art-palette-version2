package com.example.artpaletteversion2.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.artpaletteversion2.R
import com.example.artpaletteversion2.databinding.ActivityProfileBinding
import com.example.artpaletteversion2.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ismaeldivita.chipnavigation.ChipNavigationBar

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    private var textViewFullName: TextView? = null
    private var textViewEmail: TextView? = null
    private var textViewDob: TextView? = null

    private var progressBar: ProgressBar? = null

    private var imageView: ImageView? = null

    private var authProfile: FirebaseAuth? = null

    private var fullname: String? = null
    private var email: String? = null
    private var dob: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        textViewFullName = findViewById(R.id.textView_show_fullname)
        textViewEmail = findViewById(R.id.textView_show_email)
        textViewDob = findViewById(R.id.textView_show_dob)
        progressBar = findViewById(R.id.progressBar)

        authProfile = FirebaseAuth.getInstance()
        val firebaseUser = authProfile?.currentUser

        if (firebaseUser == null) {
            Toast.makeText(this@ProfileActivity, "Something went wrong! User's details are not available at the moment", Toast.LENGTH_LONG).show()
        } else {
            progressBar?.visibility = View.VISIBLE
            showUserProfile(firebaseUser)
        }

        val btnEditProfile = findViewById<Button>(R.id.btnEditProfile)
        btnEditProfile.setOnClickListener {
            startActivity(Intent(this@ProfileActivity, UpdateProfileActivity::class.java))
            this@ProfileActivity.overridePendingTransition(
                R.anim.animate_fade_enter,
                R.anim.animate_fade_exit
            )
        }


        val menu = findViewById<ChipNavigationBar>(R.id.menu)
        menu.setItemSelected(R.id.account)
        menu.setOnItemSelectedListener {
            if (it == R.id.home) {
                startActivity(Intent(this@ProfileActivity, MainActivity::class.java))
                this@ProfileActivity.overridePendingTransition(
                    R.anim.animate_fade_enter,
                    R.anim.animate_fade_exit
                )
            }
            if (it == R.id.explore) {
                startActivity(Intent(this@ProfileActivity, ExploreActivity::class.java))
                this@ProfileActivity.overridePendingTransition(
                    R.anim.animate_fade_enter,
                    R.anim.animate_fade_exit
                )
            }
            if (it == R.id.add) {
                startActivity(Intent(this@ProfileActivity, AddImageActivity::class.java))
                this@ProfileActivity.overridePendingTransition(
                    R.anim.animate_fade_enter,
                    R.anim.animate_fade_exit
                )
            }
            if (it == R.id.saved) {
                startActivity(Intent(this@ProfileActivity, SavedActivity::class.java))
                this@ProfileActivity.overridePendingTransition(
                    R.anim.animate_fade_enter,
                    R.anim.animate_fade_exit
                )
            }
        }
    }

    private fun showUserProfile(firebaseUser: FirebaseUser) {
        val userID = firebaseUser.uid

        // Extracting User Reference from Database for "Registered Users"
        val referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users")
        referenceProfile.child(userID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(UserModel::class.java)
                if (user != null) {
                    fullname = user.username
                    email = firebaseUser.email
                    dob = user.dob

                    textViewFullName?.text = fullname
                    textViewEmail?.text = email
                    textViewDob?.text = dob
                }
                progressBar?.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProfileActivity, "Something went wrong!", Toast.LENGTH_LONG).show()
                progressBar?.visibility = View.GONE
            }

        })
    }
}