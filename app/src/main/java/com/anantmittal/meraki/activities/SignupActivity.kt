package com.anantmittal.meraki.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.anantmittal.meraki.data_modals.AppUserInfo
import com.anantmittal.meraki.R
import com.anantmittal.meraki.databinding.ActivitySignupBinding
import com.anantmittal.meraki.data_modals.refUrl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignupActivity : AppCompatActivity() {

    private val binding: ActivitySignupBinding by lazy {
        ActivitySignupBinding.inflate(layoutInflater)
    }
    private lateinit var firebaseAuth: FirebaseAuth

    //    private lateinit var database: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance(refUrl)
        databaseReference = database.getReference()

        binding.continueButton.setOnClickListener {
            val firstName = binding.firstNameInput.text.toString()
            val lastName = binding.lastNameInput.text.toString()
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()
            val cnfmPassword = binding.cnfmPasswordInput.text.toString()
            if (firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && cnfmPassword.isNotEmpty()) {
                if (password == cnfmPassword) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { signUp ->
                            if (signUp.isSuccessful) {
                                Log.d(TAG, "onCreate: signup success")
                                firebaseAuth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { signIn ->
                                        if (signIn.isSuccessful) {
                                            Log.d(TAG, "onCreate: signin success")
                                            val currentUser = firebaseAuth.currentUser
                                            currentUser?.let { user ->
                                                val appUserInfo =
                                                    AppUserInfo(email, firstName, lastName)
                                                Log.d(TAG, "onCreate: loggedin user info- ${user.uid}")
                                                databaseReference.child("users").child(user.uid)
                                                    .child("user_creds").setValue(appUserInfo)
                                            }

                                            val sharedPreferences =
                                                getSharedPreferences("LoginPrefs", MODE_PRIVATE)
                                            val editor = sharedPreferences.edit()
                                            editor.putBoolean("rememberMe", true)
                                            editor.apply()
                                            Log.d(TAG, "onCreate: shared preferences done")
                                            val intent = Intent(this, MainActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                            Log.d(TAG, "onCreate: gone to main activity")
                                        } else {
                                            Toast.makeText(
                                                this,
                                                signIn.exception.toString(),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            } else {
                                Toast.makeText(
                                    this, signUp.exception.toString(), Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Password not matching", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Don't leave fields empty", Toast.LENGTH_SHORT).show()
            }
        }
    }
}