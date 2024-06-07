package com.example.artpaletteversion2.activity

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.example.artpaletteversion2.R
import com.example.artpaletteversion2.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import java.util.Calendar

class UpdateProfileActivity : AppCompatActivity() {

    private var editTextUpdateName: EditText? = null
    private var editTextUpdateEmail: EditText? = null
    private var editTextUpdateDob: EditText? = null

    private var textFullName: String? = null
    private var textEmail: String? = null
    private var textDob: String? = null

    private var authProfile: FirebaseAuth? = null
    private var progressBar: ProgressBar? = null

    private var picker: DatePickerDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)

        progressBar = findViewById(R.id.progressBar)
        editTextUpdateName = findViewById(R.id.editText_update_profile_fullname)
        editTextUpdateEmail = findViewById(R.id.editText_update_profile_email)
        editTextUpdateDob = findViewById(R.id.editText_update_profile_dob)

        authProfile = FirebaseAuth.getInstance()
        val firebaseUser = authProfile?.currentUser

        // Show Profile Data
        showProfile(firebaseUser)

        editTextUpdateDob?.setOnClickListener {
            val textSADob: List<String>? = textDob?.split("/")

            val day = textSADob?.get(0)?.toInt()
            val month = textSADob?.get(1)?.toInt()?.minus(1)
            val year = textSADob?.get(2)?.toInt()

            // Date Picker Dialog
            picker = DatePickerDialog(this@UpdateProfileActivity, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                editTextUpdateDob?.setText("${dayOfMonth}/${month + 1}/${year}")
            }, year!!, month!!, day!!)
            picker?.show()
        }

        // Update Profile
        val btnUpdateProfile = findViewById<Button>(R.id.btnUpdateProfile)
        btnUpdateProfile.setOnClickListener {
            updateProfile(firebaseUser)
        }




        val menu = findViewById<ChipNavigationBar>(R.id.menu)
        menu.setItemSelected(R.id.account)
        menu.setOnItemSelectedListener {
            if (it == R.id.home) {
                startActivity(Intent(this@UpdateProfileActivity, MainActivity::class.java))
                this@UpdateProfileActivity.overridePendingTransition(
                    R.anim.animate_fade_enter,
                    R.anim.animate_fade_exit
                )
            }

            if (it == R.id.explore) {
                startActivity(Intent(this@UpdateProfileActivity, ExploreActivity::class.java))
                this@UpdateProfileActivity.overridePendingTransition(
                    R.anim.animate_fade_enter,
                    R.anim.animate_fade_exit
                )
            }

            if (it == R.id.add) {
                startActivity(Intent(this@UpdateProfileActivity, AddImageActivity::class.java))
                this@UpdateProfileActivity.overridePendingTransition(
                    R.anim.animate_fade_enter,
                    R.anim.animate_fade_exit
                )
            }

            if (it == R.id.saved) {
                startActivity(Intent(this@UpdateProfileActivity, SavedActivity::class.java))
                this@UpdateProfileActivity.overridePendingTransition(
                    R.anim.animate_fade_enter,
                    R.anim.animate_fade_exit
                )
            }
            if (it == R.id.account) {
                startActivity(Intent(this@UpdateProfileActivity, ProfileActivity::class.java))
                this@UpdateProfileActivity.overridePendingTransition(
                    R.anim.animate_fade_enter,
                    R.anim.animate_fade_exit
                )
            }
        }
    }

    private fun updateProfile(firebaseUser: FirebaseUser?) {
        if(TextUtils.isEmpty(textEmail)) {
            Toast.makeText(this@UpdateProfileActivity, "Please enter your Email", Toast.LENGTH_LONG).show()
            editTextUpdateEmail?.error = "Email is required"
            editTextUpdateEmail?.requestFocus()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail!!).matches()) {
            Toast.makeText(this@UpdateProfileActivity, "Please re-enter your Email", Toast.LENGTH_LONG).show()
            editTextUpdateEmail?.error = "Valid email is required"
            editTextUpdateEmail?.requestFocus()
        } else if (TextUtils.isEmpty(textFullName)) {
            Toast.makeText(this@UpdateProfileActivity, "Please enter your First name", Toast.LENGTH_LONG).show()
            editTextUpdateName?.error = "Firstname is required"
            editTextUpdateName?.requestFocus()
        } else if (TextUtils.isEmpty(textDob)) {
            Toast.makeText(this@UpdateProfileActivity, "Please enter your Date of Birth", Toast.LENGTH_LONG).show()
            editTextUpdateDob?.error = "Date of birth is required"
            editTextUpdateDob?.requestFocus()
        }  else {
            textFullName = editTextUpdateName?.text.toString()
            textEmail = editTextUpdateEmail?.text.toString()
            textDob = editTextUpdateDob?.text.toString()

            // Enter User Data into the Firebase Realtime Database
            val user = UserModel(textFullName, textDob)

            // Extract USer reference
            val referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users")

            val userID = firebaseUser?.uid

            referenceProfile.child(userID!!).setValue(user).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@UpdateProfileActivity, "Update Successful", Toast.LENGTH_LONG).show()

                    val intent = Intent(this@UpdateProfileActivity, ProfileActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                } else {
                    try {
                        throw task.exception!!
                    } catch (e: Exception) {
                        Toast.makeText(this@UpdateProfileActivity, e.message, Toast.LENGTH_LONG).show()
                    }
                }
                progressBar?.visibility = View.GONE
            }
        }
    }

    private fun showProfile(firebaseUser: FirebaseUser?) {
        val userIdOfRegistered = firebaseUser?.uid

        // Extracting User Reference from Database for "Registered Users"
        val referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users")

        progressBar?.visibility = View.VISIBLE

        referenceProfile.child(userIdOfRegistered!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(UserModel::class.java)
                if (user != null) {
                    textFullName = user.username
                    textEmail = firebaseUser.email
                    textDob = user.dob

                    editTextUpdateName?.setText(textFullName)
                    editTextUpdateEmail?.setText(textEmail)
                    editTextUpdateDob?.setText(textDob)
                } else {
                    Toast.makeText(this@UpdateProfileActivity, "Something went wrong!", Toast.LENGTH_LONG).show()
                }
                progressBar?.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@UpdateProfileActivity, "Something went wrong!", Toast.LENGTH_LONG).show()
                progressBar?.visibility = View.GONE
            }

        })
    }
}