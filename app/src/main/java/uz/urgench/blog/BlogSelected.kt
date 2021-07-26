package uz.urgench.blog

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class BlogSelected : AppCompatActivity() {
    private lateinit var textName: TextView
    private lateinit var text: TextView
    private lateinit var image: ImageView
    private lateinit var userPhoto: ImageView
    private lateinit var userName: TextView
    private lateinit var email: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blog_selected)
        email = findViewById(R.id.emailInBlog)
        textName = findViewById(R.id.textNameInBlog)
        text = findViewById(R.id.textInBlog)
        image = findViewById(R.id.imageInBlog)
        userPhoto = findViewById(R.id.userPhotoInBlog)
        userName = findViewById(R.id.userNameInBlog)
        val fdb = Firebase.firestore
        val getExtra = intent.getStringExtra("BlogName")!!
        setSupportActionBar(findViewById(R.id.blog_toolbar))
        supportActionBar?.subtitle = getExtra
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        fdb.collection("Blog").document(getExtra)
            .get()
            .addOnSuccessListener { doc ->
                email.text = doc["UserName"].toString()
                fdb.collection("Accounts").document(email.text.toString())
                    .get()
                    .addOnSuccessListener {
                        Glide.with(userPhoto.context).load(it["CustomPhoto"]).into(userPhoto)
                        userName.text = it["CustomName"].toString()
                    }
                text.text = doc["Text"].toString()
                textName.text = getExtra
            }
        Firebase.storage.reference.child("chatFiles/$getExtra").downloadUrl.addOnSuccessListener {
            Glide.with(this).load(it).into(image)
            image.setOnClickListener { v ->
                val gallery = Intent().setAction(Intent.ACTION_VIEW)
                gallery.setDataAndType(it, "image/*")
                startActivity(gallery)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> super.onBackPressed()
        }
        return true
    }

}