package com.example.artpaletteversion2.activity

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.artpaletteversion2.R
import com.example.artpaletteversion2.databinding.ActivityLoginBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private var backwardButton: TextView? = null
    private var inputEmail: TextInputEditText? = null
    private var inputPassword: TextInputEditText? = null

    private var btnLogin: Button? = null

    private var switcherSignup: Button? = null

    private var progressBar: ProgressBar? = null

    private var authProfile: FirebaseAuth? = null

    private val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inputEmail = findViewById(R.id.emailEt)
        inputPassword = findViewById(R.id.passwordEt)

        backwardButton = findViewById(R.id.backBtn)
        btnLogin = findViewById(R.id.loginBtn)
        switcherSignup = findViewById(R.id.signupSwitchBtn)

        progressBar = findViewById(R.id.progressBar)

        authProfile = FirebaseAuth.getInstance()

        // Back to previous page using button listener
        binding.backBtn.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegistrationActivity::class.java))
            this@LoginActivity.overridePendingTransition(
                R.anim.animate_fade_enter,
                R.anim.animate_fade_exit
            )
        }

        // Switch to Sign up page
        switcherSignup?.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignupActivity::class.java))
            this@LoginActivity.overridePendingTransition(
                R.anim.animate_fade_enter,
                R.anim.animate_fade_exit
            )
        }

        // Login listener
        btnLogin?.setOnClickListener {
            val textEmail = inputEmail?.text.toString()
            val textPassword = inputPassword?.text.toString()

            if (TextUtils.isEmpty(textEmail)) {
                Toast.makeText(this@LoginActivity, "Please Enter your Email", Toast.LENGTH_LONG).show()
                inputEmail?.error = "Email is required"
                inputEmail?.requestFocus()
            } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                Toast.makeText(this@LoginActivity, "Please Re-Enter your Email", Toast.LENGTH_LONG).show()
                inputEmail?.error = "Valid Email is required"
                inputEmail?.requestFocus()
            } else if (TextUtils.isEmpty(textPassword)) {
                Toast.makeText(this@LoginActivity, "Please Enter your Password", Toast.LENGTH_LONG).show()
                inputEmail?.error = "Password is required"
                inputEmail?.requestFocus()
            } else {
                progressBar?.visibility = View.VISIBLE
                loginUser(textEmail, textPassword)
            }
        }
    }

    private fun loginUser(textEmail: String, textPassword: String) {
        authProfile?.signInWithEmailAndPassword(textEmail, textPassword)?.addOnCompleteListener {task ->
            if (task.isSuccessful) {

                // Get instance of the current User
                val firebaseUser = authProfile?.currentUser

                // Check if email is verified before user can access their profile
                if (firebaseUser!!.isEmailVerified) {
                    Toast.makeText(this@LoginActivity, "You are logged in now", Toast.LENGTH_LONG).show()
                } else {
                    firebaseUser.sendEmailVerification()
                    authProfile?.signOut()
                    showAlertDialog()
                }

            } else {
                try {
                    throw task.exception!!
                } catch (e: FirebaseAuthInvalidUserException) {
                    inputEmail?.error = "User does not exists or is no longer valid. Please register again."
                    inputEmail?.requestFocus()
                } catch (e: FirebaseAuthInvalidCredentialsException) {
                    inputEmail?.error = "Invalid Credentials. Kindly check and Re-enter."
                    inputEmail?.requestFocus()
                } catch (e: Exception) {
                    Log.e(TAG, e.message!!)
                    Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_LONG).show()
                }
                Toast.makeText(this@LoginActivity, "Something went wrong", Toast.LENGTH_LONG).show()
            }
            progressBar?.visibility = View.GONE
        }
    }

    private fun showAlertDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@LoginActivity)
        builder.setTitle("Email Not Verified")
        builder.setMessage("Please verify your email now. you can not login without email verification.")

        // Open Email Apps if User clicks/taps Continue button
        builder.setPositiveButton("Continue", DialogInterface.OnClickListener { dialog, which ->
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_APP_EMAIL)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        })

        // Create the Alert
        val alertDialog = builder.create()

        // Show the Alert Dialog
        alertDialog.show()
    }

    override fun onStart() {
        super.onStart()
        if (authProfile?.currentUser != null) {
            Toast.makeText(this@LoginActivity, "Already Logged In", Toast.LENGTH_LONG).show()

            // Start the Main Activity
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        } else {
            Toast.makeText(this@LoginActivity, "You can login now", Toast.LENGTH_LONG).show()
        }
    }
}