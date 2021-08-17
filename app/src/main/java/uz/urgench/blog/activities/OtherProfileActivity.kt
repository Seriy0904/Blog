package uz.urgench.blog.activities

import android.net.Uri
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
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import uz.urgench.blog.BlogListAdapter
import uz.urgench.blog.R

class OtherProfileActivity : AppCompatActivity() {
    private val textList: ArrayList<String> = arrayListOf()
    private val textNameList: ArrayList<String> = arrayListOf()
    private val userList: ArrayList<String> = arrayListOf()
    private val dateList: ArrayList<Timestamp> = arrayListOf()
    private val uriList: ArrayList<String?> = arrayListOf()
    private var userBlogListAdapter: BlogListAdapter? = null
    private lateinit var userBlogsList: RecyclerView
    private lateinit var swipe_layout: SwipeRefreshLayout
    private lateinit var selectedUser: String
    private lateinit var userName: TextView
    private lateinit var subscribersAmount: TextView
    private lateinit var progress: FrameLayout
    private lateinit var userPhoto: ImageView
    private lateinit var subscribeButton: Button
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
        selectedUser = intent.getStringExtra("Email").toString()
        userName = findViewById(R.id.other_profile_username)
        subscribeButton = findViewById(R.id.subscribe_in_other_profile)
        progress = findViewById(R.id.other_profile_progressbar)
        subscribersAmount = findViewById(R.id.other_profile_subscribers_amount)
        userPhoto = findViewById(R.id.other_profile_user_photo)
        swipe_layout = findViewById(R.id.refresh_layout_other_profile)
        userBlogsList = findViewById(R.id.other_profile_list)
        setSupportActionBar(findViewById(R.id.toolbar_other_profile))
        var subscribers = arrayListOf<String>()
        Firebase.firestore.collection("Accounts").document(selectedUser).get()
            .addOnSuccessListener {
                userName.text = it.getString("CustomName")
                Glide.with(this).load(Uri.parse(it.getString("CustomPhoto"))).into(userPhoto)
                if (it["SubscribersList"] != null) {
                    subscribers = it["SubscribersList"] as ArrayList<String>
                    pasteTrueWord(subscribers)
                }
            }
        subscribeButton.setOnClickListener {
            if (!subscribers.contains(Firebase.auth.currentUser?.email!!)) {
                subscribers.add(Firebase.auth.currentUser?.email!!)
                Firebase.firestore.collection("Accounts").document(selectedUser)
                    .update(mapOf("SubscribersList" to subscribers)).addOnSuccessListener {
                        pasteTrueWord(subscribers)
                    }
            } else {
                subscribers.remove(Firebase.auth.currentUser?.email!!)
                Firebase.firestore.collection("Accounts").document(selectedUser)
                    .update(mapOf("SubscribersList" to subscribers)).addOnSuccessListener {
                        pasteTrueWord(subscribers)
                    }
            }
        }
        putToList()
        swipe_layout.setOnRefreshListener {
            Firebase.firestore.collection("Accounts").document(selectedUser).get()
            .addOnSuccessListener {
                userName.text = it.getString("CustomName")
                Glide.with(this).load(Uri.parse(it.getString("CustomPhoto"))).into(userPhoto)
                if (it["SubscribersList"] != null) {
                    subscribers = it["SubscribersList"] as ArrayList<String>
                    pasteTrueWord(subscribers)
                }
            }
            putToList()
        }
    }

    private fun pasteTrueWord(subscribers: ArrayList<String>) {
        val lastSymbols = "0${subscribers.size}".substring(subscribers.size.toString().length - 1)
        if (lastSymbols.first() != '1' && lastSymbols[1].digitToInt() in 2 until 4)
            subscribersAmount.text =
                getString(R.string.second_subscribe_text, subscribers.size)
        else if (lastSymbols != "11" && lastSymbols.last() != '1'){
                Log.d("MyTag", "Subscribers ${lastSymbols[1].code}")
            subscribersAmount.text =
                getString(R.string.default_subscribe_text, subscribers.size)}
        else
            subscribersAmount.text =
                getString(R.string.first_subscribe_text, subscribers.size)
    }

    private fun putToList() {
        userBlogListAdapter = null
        textList.clear()
        textNameList.clear()
        userList.clear()
        dateList.clear()
        uriList.clear()
        val db = Firebase.firestore
        db.collection("Blog").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    if (document.getString("UserName") == selectedUser) {
                        textNameList.add(document.id)
                        textList.add(document.get("Text").toString())
                        userList.add(selectedUser)
                        uriList.add(document.getString("AddedPhoto"))
                        dateList.add(document.get("Date") as Timestamp)
                    }
                }
                userBlogsList.layoutManager = LinearLayoutManager(this)
                userBlogListAdapter = BlogListAdapter(
                    textNameList,
                    textList,
                    userList,
                    dateList,
                    uriList,
                    false
                )
                userBlogsList.adapter = userBlogListAdapter
                progress.visibility = View.GONE
                swipe_layout.isRefreshing = false

            }
    }
}