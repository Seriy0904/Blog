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
import uz.urgench.blog.adapters.BlogListAdapter
import uz.urgench.blog.R
import uz.urgench.blog.adapters.BlogModel

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
        getSubscribers()
        subscribeButton.setOnClickListener {
            if (!subscribers.contains(Firebase.auth.currentUser?.email!!)) {
                subscribers.add(Firebase.auth.currentUser?.email!!)
                subscribe()
            } else {
                subscribers.remove(Firebase.auth.currentUser?.email!!)
                subscribe(true)
            }
        }
        swipeLayout.setOnRefreshListener {
            getSubscribers()
        }
    }

    private fun updateSubscribers() {
        Firebase.firestore.collection("Accounts").document(selectedUser)
            .update(mapOf("SubscribersList" to subscribers)).addOnSuccessListener {
                pasteTrueWord(subscribers)
            }
    }

    private fun getSubscribers() {
        Firebase.firestore.collection("Accounts").document(selectedUser).get()
            .addOnSuccessListener {
                userName.text = it.getString("CustomName")
                Glide.with(this).load(Uri.parse(it.getString("CustomPhoto"))).into(userPhoto)
                if (it["SubscribersList"] != null) {
                    subscribers.clear()
                    subscribers.addAll(it["SubscribersList"] as ArrayList<String>)
                    pasteTrueWord(subscribers)
                    subscribe(true)
                }
            }
        putToList()
    }

    override fun onStop() {
        updateSubscribers()
        super.onStop()
    }

    private fun pasteTrueWord(subscribers: ArrayList<String>) {
//        val lastSymbols = "0${subscribers.size}".substring(subscribers.size.toString().length - 1)
        val lastSymbols = "0${subscribers.size}"
        if (lastSymbols[lastSymbols.length - 1] != '1' && lastSymbols.last().digitToInt() in 2 until 4)
            subscribersAmount.text =
                getString(R.string.second_subscribe_text, subscribers.size)
        else if (lastSymbols.substring(lastSymbols.length-2) != "11" && lastSymbols.last() != '1') {
            subscribersAmount.text =
                getString(R.string.default_subscribe_text, subscribers.size)
        } else
            subscribersAmount.text =
                getString(R.string.first_subscribe_text, subscribers.size)
    }

    private fun subscribe(unsubscribe: Boolean = false) {
        if (unsubscribe) {
            subscribeButton.setTextColor(resources.getColor(R.color.red))
            subscribeButton.text = "ПОДПИСАТЬСЯ"
        } else {
            subscribeButton.setTextColor(resources.getColor(R.color.grey))
            subscribeButton.text = "ВЫ ПОДПИСАНЫ"
        }
        pasteTrueWord(subscribers)
    }

    private fun putToList() {
        val db = Firebase.firestore
        blogList.clear()
        db.collection("Blog").whereEqualTo("UserName", selectedUser).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    blogList.add(
                        BlogModel(
                            textName = document.id,
                            text = document.get("Text").toString(),
                            user = selectedUser,
                            date = document.get("Date") as Timestamp,
                            uri = document.getString("AddedPhoto")
                        )
                    )
                }
                progress.visibility = View.GONE
                swipeLayout.isRefreshing = false
                userBlogListAdapter.setList(blogList)
            }
    }
}