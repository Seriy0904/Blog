package uz.urgench.blog

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var btn: Button

    companion object {
        lateinit var userName: String
    }

    private lateinit var userPhoto: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
            val auth = Firebase.auth
            val currentUser = auth.currentUser
            updateUI(currentUser)
        btn = findViewById(R.id.addBlog)
        btn.setOnClickListener { addBlogBtn(btn) }
        replaceList(btn)
    }

    private fun updateUI(userInfo: FirebaseUser?) {
        if (userInfo != null) {
            userPhoto = userInfo.photoUrl.toString()
            userName = userInfo.displayName.toString()
        } else startActivity(Intent(this, LoginSucces::class.java))
    }

    fun addBlogBtn(v: Button) {
        when ((v).text.toString()) {
            getString(R.string.addBlogTextResource) -> {
                btn.text = getString(R.string.save_blog_resource)
                supportFragmentManager.beginTransaction()
                    .add(R.id.fragmentContainerView, AddFragment(), null).commit()
            }
            getString(R.string.save_blog_resource) -> {
                btn.text = getString(R.string.addBlogTextResource)
                putBD()
                supportFragmentManager.beginTransaction()
                    .remove(AddFragment())
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, ListFragment(), null).commit()
            }
        }
    }

    fun replaceList(view: View) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, ListFragment(), null).commit()
    }

    fun putBD() {
        val editText: EditText = findViewById(R.id.editText)
        val editTextName: EditText = findViewById(R.id.editTextName)
        if (editTextName.text.toString() != "" && editText.text.toString() != "") {
            val db = Firebase.firestore
            val hash = hashMapOf<String, Any>(
                "Text" to editText.text.toString(),
                "UserName" to userName,
                "UserPhoto" to userPhoto
            )
            db.collection("Blog").document(editTextName.text.toString())
                .set(hash)
                .addOnSuccessListener { Log.d("MyTag", "Successful") }
                .addOnFailureListener { Log.d("MyTag", "Failed") }
        }
    }
}