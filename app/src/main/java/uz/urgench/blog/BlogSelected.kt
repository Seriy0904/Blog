package uz.urgench.blog

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class BlogSelected : AppCompatActivity() {
    private lateinit var textName: TextView
    private lateinit var text: TextView
    private lateinit var image: ImageView
    private lateinit var userPhoto: ImageView
    private lateinit var userName: TextView
    private lateinit var add: AdView
    private lateinit var email: TextView
    private lateinit var commentBut: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blog_selected)
        email = findViewById(R.id.emailInBlog)
        commentBut = findViewById(R.id.commentsButtonInBlog)
        textName = findViewById(R.id.textNameInBlog)
        text = findViewById(R.id.textInBlog)
        image = findViewById(R.id.imageInBlog)
        userPhoto = findViewById(R.id.userPhotoInBlog)
        userName = findViewById(R.id.userNameInBlog)
        val act = Intent(this, CommentActivity::class.java)
        act.putExtra("BlogName", intent.getStringExtra("BlogName")).putExtra("Where", false)
        commentBut.setOnClickListener {
            startActivity(act)
        }
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
                val url: String = doc["AddedPhoto"].toString()
                if(url!=null){
                    Glide.with(this).load(doc["AddedPhoto"]).into(image)
                    image.setOnClickListener { _ ->
                        val gallery = Intent().setAction(Intent.ACTION_VIEW)
                        gallery.setDataAndType(Uri.parse(url), "image/*")
                        startActivity(gallery)
                    }
                }
            }
        add = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        add.loadAd(adRequest)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                CommentActivity.toMain = true
                super.onBackPressed()
            }
        }
        return true
    }

}