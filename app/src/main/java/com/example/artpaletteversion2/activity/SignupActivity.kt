package com.example.artpaletteversion2.activity

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.artpaletteversion2.R
import com.example.artpaletteversion2.model.UserModel
import com.example.artpaletteversion2.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private var inputEmail: EditText? = null
    private var inputFirstName: EditText? = null
    private var inputLastName: EditText? = null
    private var inputDob: EditText? = null
    private var inputPassword: EditText? = null
    private var inputConfirmPw: EditText? = null

    private var btnSignup: Button? = null

    private var switcherLogin: Button? = null

    private var progressBar: ProgressBar? = null

    private val TAG = "SignupActivity"

    private var picker: DatePickerDialog? = null

    private var backwardButton: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inputEmail = findViewById(R.id.emailSignUpEt)
        inputFirstName = findViewById(R.id.firstNameSignUpEt)
        inputLastName = findViewById(R.id.lastNameSignUpEt)
        inputDob = findViewById(R.id.dateSignUpEt)
        inputPassword = findViewById(R.id.passwordSignUpEt)
        inputConfirmPw = findViewById(R.id.confirmPasswordSignUpEt)

        btnSignup = findViewById(R.id.signupBtn)

        switcherLogin = findViewById(R.id.loginSwitchBtn)

        backwardButton = findViewById(R.id.backBtn)

        progressBar = findViewById(R.id.progressBar)

        // Back to previous page using button listener
        backwardButton?.setOnClickListener {
            startActivity(Intent(this@SignupActivity, RegistrationActivity::class.java))
            this@SignupActivity.overridePendingTransition(
                R.anim.animate_fade_enter,
                R.anim.animate_fade_exit
            )
        }

        // Switch to Login page
        switcherLogin?.setOnClickListener {
            startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
            this@SignupActivity.overridePendingTransition(
                R.anim.animate_fade_enter,
                R.anim.animate_fade_exit
            )
        }

        // Setting up DatePicker on Et
        inputDob?.setOnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH)
            val year = calendar.get(Calendar.YEAR)

            // Date Picker Dialog
            picker = DatePickerDialog(this@SignupActivity, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                inputDob?.setText("${dayOfMonth}/${month + 1}/${year}")
            }, year, month, day)
            picker?.show()
        }

        // Signup Listener
        btnSignup?.setOnClickListener {
            // get email
            val textEmail = inputEmail?.text.toString()
            // get full name
            val textFirstName = inputFirstName?.text.toString()
            val textLastname = inputLastName?.text.toString()
            val textFullName = "$textFirstName $textLastname"
            // get date of birth
            val textDob = inputDob?.text.toString()
            // get password
            val textPw = inputPassword?.text.toString()
            // get confirmed password
            val textConfirmPw = inputConfirmPw?.text.toString()

            if(TextUtils.isEmpty(textEmail)) {
                Toast.makeText(this@SignupActivity, "Please enter your Email", Toast.LENGTH_LONG).show()
                inputEmail?.error = "Email is required"
                inputEmail?.requestFocus()
            } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                Toast.makeText(this@SignupActivity, "Please re-enter your Email", Toast.LENGTH_LONG).show()
                inputFirstName?.error = "Valid email is required"
                inputFirstName?.requestFocus()
            } else if (TextUtils.isEmpty(textFirstName)) {
                Toast.makeText(this@SignupActivity, "Please enter your First name", Toast.LENGTH_LONG).show()
                inputFirstName?.error = "Firstname is required"
                inputFirstName?.requestFocus()
            } else if (TextUtils.isEmpty(textLastname)) {
                Toast.makeText(this@SignupActivity, "Please enter your Last name", Toast.LENGTH_LONG).show()
                inputLastName?.error = "Lastname is required"
                inputLastName?.requestFocus()
            } else if (TextUtils.isEmpty(textDob)) {
                Toast.makeText(this@SignupActivity, "Please enter your Date of Birth", Toast.LENGTH_LONG).show()
                inputDob?.error = "Date of birth is required"
                inputDob?.requestFocus()
            } else if (TextUtils.isEmpty(textPw)) {
                Toast.makeText(this@SignupActivity, "Please enter your Password", Toast.LENGTH_LONG).show()
                inputPassword?.error = "Password is required"
                inputPassword?.requestFocus()
            } else if (textPw.length < 6) {
                Toast.makeText(this@SignupActivity, "Password should be at least 6 digits", Toast.LENGTH_LONG).show()
                inputPassword?.error = "Password too weak"
                inputPassword?.requestFocus()
            } else if (TextUtils.isEmpty(textConfirmPw)) {
                Toast.makeText(this@SignupActivity, "Please confirm your Password", Toast.LENGTH_LONG).show()
                inputConfirmPw?.error = "Confirmed Password is required"
                inputConfirmPw?.requestFocus()
            } else if (!textPw.equals(textConfirmPw)) {
                Toast.makeText(this@SignupActivity, "Please same same password", Toast.LENGTH_LONG).show()
                inputConfirmPw?.error = "Password Confirmation is required"
                inputConfirmPw?.requestFocus()
                // Clear the entered password
                inputPassword?.clearComposingText()
                inputConfirmPw?.clearComposingText()
            } else {
                progressBar?.visibility = View.VISIBLE
                registerUser(textEmail, textFullName, textDob, textPw)
            }
        }
    }

    private fun registerUser(textEmail: String, textFullName: String, textDob: String, textPw: String) {
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(textEmail, textPw).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this@SignupActivity, "User registered successfully", Toast.LENGTH_LONG).show()
                val firebaseUser: FirebaseUser? = auth.currentUser

                // Enter User Data into the Firebase Realtime Database
                val user: UserModel = UserModel(textFullName, textDob)

                // Extracting User reference from Database for "Registered User"
                val referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users")

                referenceProfile.child(firebaseUser!!.uid).setValue(user).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // send verification Email
                        firebaseUser.sendEmailVerification()

                        Toast.makeText(this@SignupActivity, "User registered successfully. Please verify your email", Toast.LENGTH_LONG).show()

                        // Open user Profile after successful registration
//                        val intent = Intent(this@SignupActivity, UserProfileActivity::class.java)
//                        // To prevent User from returning back to Register Activity on pressing back button after registration
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
//                        startActivity(intent)
//                        finish() // to close Signup Activity
                    } else {
                        Toast.makeText(this@SignupActivity, "User registered failed. Please try again", Toast.LENGTH_LONG).show()
                    }
                    progressBar?.visibility = View.GONE
                }
            } else {
                try {
                    throw task.exception!!
                } catch (e: FirebaseAuthWeakPasswordException) {
                    inputPassword?.error = "Your password is too weak. Kindly use a mix of alphabets, numbers and special characters."
                    inputPassword?.requestFocus()
                } catch (e: FirebaseAuthInvalidCredentialsException) {
                    inputEmail?.error = "Your email is invalid or already in use. Kindly re-enter."
                    inputEmail?.requestFocus()
                } catch (e: FirebaseAuthUserCollisionException) {
                    inputEmail?.error = "User is already registered with this email. Use another email."
                    inputEmail?.requestFocus()
                } catch (e: Exception) {
                    Log.e(TAG, e.message!!)
                    Toast.makeText(this@SignupActivity, e.message, Toast.LENGTH_LONG).show()
                }
                progressBar?.visibility = View.GONE
            }
        }
    }
}