package com.example.firebaseinsta

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.firebaseinsta.databinding.ActivityUploadBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class UploadActivity : AppCompatActivity() {
    lateinit var binding: ActivityUploadBinding
    var selectedPicture : Uri? = null
    private lateinit var db : FirebaseFirestore
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    fun imageViewClicked (view: View){

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        } else{
            val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent,2)
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if (requestCode == 1){
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent,2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            selectedPicture = data.data

            try {
            if (selectedPicture != null) {

                if (Build.VERSION.SDK_INT >= 28) {
                    val source = ImageDecoder.createSource(contentResolver, selectedPicture!!)
                    val bitmap = ImageDecoder.decodeBitmap(source)
                    binding.uploadImageView.setImageBitmap(bitmap)
                } else {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedPicture)
                    binding.uploadImageView.setImageBitmap(bitmap)
                }
            }
        }catch(e: Exception){
            e.printStackTrace()
        }

        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    fun uploadBtnClicked(view: View){

        //UUID image name  (random number)
        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpg"

        val storge = FirebaseStorage.getInstance()
        val reference = storge.reference
        val imagesReferance = reference.child("images").child(imageName)

        if (selectedPicture != null){
            imagesReferance.putFile(selectedPicture!!).addOnSuccessListener { taskSnapshot ->

                //database - Firebase
                val uploadedPictureReferance = FirebaseStorage.getInstance().reference.child("images").child(imageName)
                uploadedPictureReferance.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()

                    val postMap = hashMapOf<String,Any>()
                    postMap.put("downloadUrl",downloadUrl)
                    postMap.put("userEmail",auth.currentUser!!.email.toString())
                    postMap.put("comment",binding.uploadCommentText.text.toString())
                    postMap.put("date", Timestamp.now())

                    db.collection("Posts").add(postMap).addOnCompleteListener { task ->
                        if (task.isComplete && task.isSuccessful){
                            // back
                            finish()

                        }
                    }.addOnFailureListener { exception ->
                        Toast.makeText(this, exception.localizedMessage.toString(), Toast.LENGTH_LONG).show()
                    }
                }

            }
        }

    }
}