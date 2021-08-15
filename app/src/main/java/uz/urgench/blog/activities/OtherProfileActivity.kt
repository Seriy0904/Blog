package uz.urgench.blog.activities

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import uz.urgench.blog.BlogListAdapter
import uz.urgench.blog.R

class OtherProfileActivity : AppCompatActivity() {
    private val textList: ArrayList<String> = arrayListOf()
    private val textNameList: ArrayList<String> = arrayListOf()
    private val userList: ArrayList<String> = arrayListOf()
    private val dateList: ArrayList<Timestamp> = arrayListOf()
    private lateinit var userBlogListAdapter: BlogListAdapter
    private lateinit var userBlogsList: RecyclerView
    private lateinit var swipe_layout:SwipeRefreshLayout
    private lateinit var selectedUser: String
    private lateinit var userName: TextView
    private lateinit var userEmail: TextView
    private lateinit var progress:FrameLayout
    private lateinit var userPhoto: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sp = getSharedPreferences(APP_PREFERENCE, MODE_PRIVATE)
            setTheme(when(sp.getInt(APP_PREFERENCE_THEME,0)){
                1->R.style.OldTheme
                else -> R.style.MainTheme
            })
        setContentView(R.layout.activity_other_profile)
        selectedUser = intent.getStringExtra("Email").toString()
        userName = findViewById(R.id.other_profile_username)
        progress = findViewById(R.id.other_profile_progressbar)
        userEmail = findViewById(R.id.other_profile_user_email)
        userPhoto = findViewById(R.id.other_profile_user_photo)
        swipe_layout = findViewById(R.id.refresh_layout_other_profile)
        userBlogsList = findViewById(R.id.other_profile_list)
        setSupportActionBar(findViewById(R.id.toolbar_other_profile))
        Firebase.firestore.collection("Accounts").document(selectedUser).get()
            .addOnSuccessListener {
                userName.text = it.getString("CustomName")
                Glide.with(this).load(Uri.parse(it.getString("CustomPhoto"))).into(userPhoto)
                userEmail.text = selectedUser
            }
        putToList()
        swipe_layout.setOnRefreshListener {
            putToList()
        }
    }

    private fun putToList() {
        textList.clear()
        textNameList.clear()
        userList.clear()
        dateList.clear()
        val db = Firebase.firestore
        db.collection("Blog").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    if (document.getString("UserName")==selectedUser){
                    textNameList.add(document.id)
                    textList.add(document.get("Text").toString())
                    userList.add(selectedUser)
                    dateList.add(document.get("Date") as Timestamp)
                    }
                }
                userBlogsList.layoutManager = LinearLayoutManager(this)
                userBlogListAdapter = BlogListAdapter(
                    textNameList,
                    textList,
                    userList,
                    dateList,
                    false
                )
                userBlogsList.adapter = userBlogListAdapter
                progress.visibility = View.GONE
                swipe_layout.isRefreshing = false

            }
    }
}