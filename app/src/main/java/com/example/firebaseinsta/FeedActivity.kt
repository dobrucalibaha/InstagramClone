package com.example.firebaseinsta

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebaseinsta.databinding.ActivityFeedBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class FeedActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore

    var userEmailFromFB : ArrayList<String> = ArrayList()
    var userCommentFromFB : ArrayList<String> = ArrayList()
    var userImageFromFB : ArrayList<String> = ArrayList()

    var adapter : FeedRecyclerAdapter? = null

    lateinit var binding: ActivityFeedBinding

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.options_menu,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.add_post){
            //Upload Activity
            val intent = Intent(this,UploadActivity::class.java)
            startActivity(intent)
            

        }else if (item.itemId == R.id.logout){
            //Logout
            auth.signOut()
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        getDataFromFirestore()

        //recyclerview

        var layoutManger = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManger

        adapter = FeedRecyclerAdapter(userEmailFromFB,userCommentFromFB,userImageFromFB)
        binding.recyclerView.adapter = adapter


    }
    fun getDataFromFirestore() {

        db.collection("Posts").orderBy("date",Query.Direction.DESCENDING).addSnapshotListener { snapshot, exception ->
            if (exception != null){
                Toast.makeText(this, exception.localizedMessage.toString(), Toast.LENGTH_LONG).show()
            }else {
                if (snapshot != null){
                    if (!snapshot.isEmpty){

                        userEmailFromFB.clear()
                        userCommentFromFB.clear()
                        userImageFromFB.clear()

                        val documents = snapshot.documents
                        for (document in documents) {

                            val comment = document.get("comment") as String
                            val userEmail = document.get("userEmail") as String
                            val downloadUrl = document.get("downloadUrl") as String
                            val timestamp = document.get("date") as Timestamp
                            val date = timestamp.toDate()

                            userEmailFromFB.add(userEmail)
                            userCommentFromFB.add(comment)
                            userImageFromFB.add(downloadUrl)

                            adapter!!.notifyDataSetChanged()
                        }

                    }
                }
            }
        }
    }
}