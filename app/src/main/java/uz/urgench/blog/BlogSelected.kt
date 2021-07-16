package uz.urgench.blog

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class BlogSelected : AppCompatActivity() {
    private lateinit var textName:TextView
    private lateinit var text:TextView
    private lateinit var image:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blog_selected)
        actionBar?.setDisplayShowTitleEnabled(false)
        textName = findViewById(R.id.textNameInBlog)
        text = findViewById(R.id.textInBlog)
        image = findViewById(R.id.imageInBlog)
        val fdb = Firebase.firestore
        val getExtra = intent.getStringExtra("BlogName")
        fdb.collection("Blog").document("$getExtra")
            .get()
            .addOnSuccessListener { doc->
                text.text = doc["Text"].toString()
                textName.text = getExtra
            }
        var uri: Uri? = null
        Firebase.storage.reference.child("chatFiles/$getExtra").downloadUrl.addOnSuccessListener {
            uri = it
            Glide.with(this).load(it).into(image)
        }.addOnFailureListener { }
        image.setOnClickListener {
            val gallery = Intent().setAction(Intent.ACTION_VIEW)
            gallery.setDataAndType(uri, "image/*")
            startActivity(gallery)
        }
    }
}