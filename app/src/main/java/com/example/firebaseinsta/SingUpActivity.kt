package com.example.firebaseinsta

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.firebaseinsta.databinding.ActivitySingUpBinding
import com.google.firebase.auth.FirebaseAuth

class SingUpActivity : AppCompatActivity() {

    lateinit var binding: ActivitySingUpBinding
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

    }

    fun saveClicked(view: View){

        val email = binding.userEmailText.text.toString()
        val password = binding.passwordText.text.toString()
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {task ->
            if (task.isSuccessful){
              /*  val intent = Intent(this,MainActivity::class.java)
                startActivity(intent) */
                finish()
            }
        }.addOnFailureListener { exception ->
            if (exception != null){
                Toast.makeText(this, exception.localizedMessage.toString(), Toast.LENGTH_LONG).show()
            }
        }

    }
}