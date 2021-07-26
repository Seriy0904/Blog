package uz.urgench.blog

import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CommentActivity : AppCompatActivity() {
    private val commentUserList = arrayListOf<String>()
    private val commentTextList = arrayListOf<String>()
    private lateinit var editComment: EditText
    private lateinit var saveComments: ImageButton
    private lateinit var commentList: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment_activiy)
        setSupportActionBar(findViewById(R.id.toolbar_comments))
        supportActionBar?.title = intent.getStringExtra("BlogName")!!
        supportActionBar?.subtitle = "Комментарии"
        editComment = findViewById(R.id.editComment)
        saveComments = findViewById(R.id.save_comment)
        saveComments.setOnClickListener { saveComment() }
        commentList = findViewById(R.id.comments_list)
        putToList()
    }
    fun putToList(){
        commentTextList.clear()
        commentUserList.clear()
        commentList.layoutManager = LinearLayoutManager(this)
        Firebase.firestore.collection("Blog").document(intent.getStringExtra("BlogName")!!)
            .collection("Comments")
            .get()
            .addOnSuccessListener { documents ->
                for (doc in documents){
                    Log.d("MyTag",doc.getString("CommentText")!!)
                    commentTextList.add(doc.getString("CommentText").toString())
                    commentUserList.add(doc.getString("CommentUser").toString())
                    commentList.adapter = CommentsAdapter(commentUserList,commentTextList)
                }
            }
    }
    fun saveComment() {
        if (editComment.text.toString() != "") {
            val map = hashMapOf<String, Any>(
                "CommentText" to editComment.text.toString(),
                "CommentUser" to Firebase.auth.currentUser?.email!!
            )
            Firebase.firestore.collection("Blog").document(intent.getStringExtra("BlogName")!!)
                .collection("Comments").add(map)
            editComment.setText("")
            editComment.clearFocus()
            val keyBoard = this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            keyBoard.hideSoftInputFromWindow(
                editComment.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS)
            putToList()
        }
    }
}