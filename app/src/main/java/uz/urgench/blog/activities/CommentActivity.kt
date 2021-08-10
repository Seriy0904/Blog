package uz.urgench.blog.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import uz.urgench.blog.CommentsAdapter
import uz.urgench.blog.R
import java.util.*

class CommentActivity : AppCompatActivity() {
    companion object {
        var toMain = false
    }

    private lateinit var db: CollectionReference
    private lateinit var adapter: CommentsAdapter
    private val commentUserList = arrayListOf<String>()
    private val commentTextList = arrayListOf<String>()
    val commentsId = arrayListOf<String>()
    private val commentDateList = arrayListOf<Timestamp>()
    private lateinit var editComment: EditText
    private lateinit var saveComments: ImageButton
    private lateinit var commentList: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment_activiy)
        editComment = findViewById(R.id.editComment)
        saveComments = findViewById(R.id.save_comment)
        saveComments.setOnClickListener { saveComment() }
        commentList = findViewById(R.id.comments_list)
        db = Firebase.firestore.collection("Blog")
            .document(intent.getStringExtra("BlogName")!!).collection("Comments")
        setSupportActionBar(findViewById(R.id.toolbar_comments))
        supportActionBar?.title = intent.getStringExtra("BlogName")!!
        supportActionBar?.subtitle = "Комментарии"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        putToList()
    }

    override fun onResume() {
        if (toMain) {
            toMain = false
            finish()
        }
        super.onResume()
    }

    fun putToList() {
        commentTextList.clear()
        commentUserList.clear()
        commentDateList.clear()
        commentsId.clear()
        commentList.layoutManager = LinearLayoutManager(this)
        db.get()
            .addOnSuccessListener { documents ->
                for (doc in documents) {
                    commentsId.add(doc.getString("CommentId").toString())
                    commentDateList.add(doc.get("CommentDate") as Timestamp)
                    commentTextList.add(doc.getString("CommentText").toString())
                    commentUserList.add(doc.getString("CommentUser").toString())
                    adapter = CommentsAdapter(
                        commentUserList,
                        commentTextList,
                        commentDateList,
                        this
                    )
                    commentList.adapter = adapter
                }
            }
    }

    private fun saveComment() {
        if (editComment.text.toString() != "") {
            val map = hashMapOf<String, Any>(
                "CommentText" to editComment.text.toString(),
                "CommentUser" to Firebase.auth.currentUser?.email!!,
                "CommentDate" to GregorianCalendar(TimeZone.getTimeZone("gmt")).time
            )
            db.add(map).addOnSuccessListener {
                it.set(map + hashMapOf("CommentId" to it.id))
                putToList()
            }
            editComment.setText("")
            editComment.clearFocus()
            val keyBoard = this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            keyBoard.hideSoftInputFromWindow(
                editComment.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        menu?.findItem(R.id.backToBlog)?.isVisible = true
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> super.onBackPressed()
            R.id.backToBlog -> {
                if (intent.getBooleanExtra("Where",true)){
                    val blogIntent = Intent(this, BlogSelected::class.java)
                    blogIntent.putExtra("BlogName", intent.getStringExtra("BlogName"))
                    startActivity(blogIntent)
                }else super.onBackPressed()
            }
            R.id.replaceList -> putToList()
        }
        return true
    }
}