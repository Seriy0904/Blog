package uz.urgench.blog.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import uz.urgench.blog.R
import uz.urgench.blog.adapters.BlogListAdapter
import uz.urgench.blog.adapters.BlogModel
import java.util.*
import kotlin.collections.ArrayList

class OtherProfileActivity : AppCompatActivity() {
    private val blogList = arrayListOf<BlogModel>()
    private val userBlogListAdapter = BlogListAdapter(false)
    private val userBlogsList: RecyclerView by lazy { findViewById(R.id.other_profile_list) }
    private val swipeLayout: SwipeRefreshLayout by lazy { findViewById(R.id.refresh_layout_other_profile) }
    private val selectedUser: String by lazy { intent.getStringExtra("Email").toString() }
    private val userName: TextView by lazy { findViewById(R.id.other_profile_username) }
    private val subscribersAmount: TextView by lazy { findViewById(R.id.other_profile_subscribers_amount) }
    private val progress: FrameLayout by lazy { findViewById(R.id.other_profile_progressbar) }
    private val userPhoto: ImageView by lazy { findViewById(R.id.other_profile_user_photo) }
    private val subscribeButton: Button by lazy { findViewById(R.id.subscribe_in_other_profile) }
    private val subscribers = arrayListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sp = getSharedPreferences(APP_PREFERENCE, MODE_PRIVATE)
        setTheme(
            when (sp.getInt(APP_PREFERENCE_THEME, 0)) {
                1 -> R.style.OldTheme
                else -> R.style.MainTheme
            }
        )
        setContentView(R.layout.activity_other_profile)
        setSupportActionBar(findViewById(R.id.toolbar_other_profile))
        userBlogsList.layoutManager = LinearLayoutManager(this)
        userBlogsList.adapter = userBlogListAdapter
        updateList()
        swipeLayout.setOnRefreshListener { updateList() }
        subscribeButton.setOnClickListener {
            if (subscribers.contains(Firebase.auth.currentUser?.email)){
                subscribers.remove(Firebase.auth.currentUser?.email)
                subscribeButton.setTextColor(getColor(R.color.red))
                subscribeButton.text = "ПОДПИСАТЬСЯ"}
            else{
                subscribers.add(Firebase.auth.currentUser?.email?:"")
                subscribeButton.setTextColor(getColor(R.color.grey))
                subscribeButton.text = "ВЫ ПОДПИСАНЫ"
            }
            Firebase.firestore.collection("Accounts").document(selectedUser).update(mapOf("SubscribersList" to subscribers))
            updateList()
        }
    }

    private fun updateList() {
        Firebase.firestore.collection("Blog").whereEqualTo("UserName", selectedUser).get()
            .addOnSuccessListener {
                val blogList = arrayListOf<BlogModel>()
                for (data in it) {
                    blogList.add(
                        BlogModel(
                            text = data.getString("Text") ?: "",
                            textName = data.id,
                            date = data.getTimestamp("Date")
                                ?: com.google.firebase.Timestamp(Date()),
                            user = selectedUser,
                            uri = data.getString("AddedPhoto")
                        )
                    )
                }
                userBlogListAdapter.setList(blogList)
            }
        Firebase.firestore.collection("Accounts").get().addOnSuccessListener {
            for (userInfo in it) {
                if (userInfo.id == selectedUser) {
                    subscribers.clear()
                    subscribers.addAll(userInfo["SubscribersList"] as ArrayList<String>)
                    userName.text = userInfo.getString("CustomName")
                    Glide.with(userPhoto).load(userInfo.getString("CustomPhoto")).into(userPhoto)
                }
            }
            progress.visibility = View.INVISIBLE
            swipeLayout.isRefreshing = false
            Log.d("MyTag", subscribers.toString())
            Log.d("MyTag","${Firebase.auth.currentUser?.email}")
            if (subscribers.contains(Firebase.auth.currentUser?.email)) {
                subscribeButton.setTextColor(getColor(R.color.grey))
                subscribeButton.text = "ВЫ ПОДПИСАНЫ"
            } else {
                subscribeButton.setTextColor(getColor(R.color.red))
                subscribeButton.text = "ПОДПИСАТЬСЯ"
            }
        }
    }
}