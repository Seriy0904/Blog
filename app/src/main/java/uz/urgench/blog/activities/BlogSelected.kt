package uz.urgench.blog.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import uz.urgench.blog.R

class BlogSelected : AppCompatActivity() {
    private lateinit var textName: TextView
    private lateinit var text: TextView
    private lateinit var image: ImageView
    private lateinit var userPhoto: ImageView
    private lateinit var userName: TextView
    private lateinit var add: AdView
    private lateinit var userInfo: LinearLayout
    private lateinit var commentBut: ImageButton
    private lateinit var commentAmount: TextView
    private lateinit var likeButton: ImageButton
    private lateinit var likeAmount: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sp = getSharedPreferences(APP_PREFERENCE, MODE_PRIVATE)
            setTheme(when(sp.getInt(APP_PREFERENCE_THEME,0)){
                1->R.style.OldTheme
                else -> R.style.MainTheme
            })
        setContentView(R.layout.activity_blog_selected)
        var likesList: ArrayList<String> = arrayListOf()
        commentBut = findViewById(R.id.comments_button_in_selected)
        userInfo = findViewById(R.id.user_info_blog_selected)
        textName = findViewById(R.id.text_name_in_selected)
        text = findViewById(R.id.text_in_selected)
        image = findViewById(R.id.image_in_selected)
        userPhoto = findViewById(R.id.user_photo_in_selected)
        userName = findViewById(R.id.username_in_selected)
        commentAmount = findViewById(R.id.comments_amount_in_selected)
        likeAmount = findViewById(R.id.like_amount_in_selected)
        likeButton = findViewById(R.id.like_button_in_selected)
        setSupportActionBar(findViewById(R.id.blog_selected_toolbar))
        supportActionBar?.subtitle = intent.getStringExtra("BlogName")!!
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val email = Firebase.auth.currentUser?.email
        val act = Intent(this, CommentActivity::class.java)
        act.putExtra("BlogName", intent.getStringExtra("BlogName")).putExtra("Where", false)
        commentBut.setOnClickListener {
            startActivity(act)
        }
        val blogDir =
            Firebase.firestore.collection("Blog").document(intent.getStringExtra("BlogName")!!)
        val getExtra = intent.getStringExtra("BlogName")!!
        blogDir.get()
            .addOnSuccessListener { doc ->
                userInfo.setOnClickListener {
                    if (intent.getBooleanExtra("recurse", false)) {
                        val userProfileIntent = Intent(it.context, OtherProfileActivity::class.java)
                        userProfileIntent.putExtra("Email", doc["UserName"].toString())
                        startActivity(userProfileIntent)
                    } else {
                        finish()
                    }
                }
                if (doc["LikeList"] != null) {
                    likesList = doc["LikeList"] as ArrayList<String>
                    likeAmount.text =
                        if (likesList.size == 0) "" else likesList.size.toString()
                    if (likesList.contains(email))
                        likeButton.background = ContextCompat.getDrawable(
                            this,
                            R.drawable.like_icon_pressed
                        )
                }
                blogDir.collection("Comments").get().addOnSuccessListener {
                    commentAmount.text = if (it.size() > 0) it.size().toString() else ""
                }
                Firebase.firestore.collection("Accounts")
                    .document(doc["UserName"].toString().toString())
                    .get()
                    .addOnSuccessListener {
                        Glide.with(userPhoto.context).load(it["CustomPhoto"]).into(userPhoto)
                        userName.text = it["CustomName"].toString()
                    }
                text.text = doc["Text"].toString()
                textName.text = getExtra
                val url: String = doc["AddedPhoto"].toString()
                Glide.with(this).load(doc["AddedPhoto"]).into(image)
                findViewById<FrameLayout>(R.id.blog_selected_progressbar).visibility = View.GONE
                image.setOnClickListener { _ ->
                    val gallery = Intent().setAction(Intent.ACTION_VIEW)
                    gallery.setDataAndType(Uri.parse(url), "image/*")
                    startActivity(gallery)
                }
            }

        likeButton.setOnClickListener { it
            val mLikeList = likesList
            if (mLikeList.contains(email)) {
                mLikeList.remove(email)
                blogDir.update(mapOf("LikeList" to mLikeList))
                likeAmount.text =
                    if (mLikeList.size == 0) "" else mLikeList.size.toString()
                it.background = ContextCompat.getDrawable(
                    this,
                    R.drawable.like_icon
                )
            } else {
                mLikeList.add(email!!)
                blogDir.update(mapOf("LikeList" to mLikeList))
                likeAmount.text = mLikeList.size.toString()
                it.background = ContextCompat.getDrawable(
                    this,
                    R.drawable.like_icon_pressed
                )
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