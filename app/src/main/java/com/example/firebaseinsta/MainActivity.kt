package com.example.firebaseinsta

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.firebaseinsta.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser

        if (currentUser != null){
            val intent = Intent(this,FeedActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    fun signInClicked (view: View){
        auth.signInWithEmailAndPassword(binding.userEmailText.text.toString(),binding.passwordText.text.toString()).addOnCompleteListener { task ->
            if (task.isSuccessful){
                Toast.makeText(this, "Welcome ${auth.currentUser?.email.toString()}", Toast.LENGTH_LONG).show()
                val intent = Intent(this,FeedActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, exception.localizedMessage.toString(), Toast.LENGTH_LONG).show()
        }
    }
    fun signUpClicked (view: View){

        val intent = Intent(this,SingUpActivity::class.java)
        startActivity(intent)

    }
}