package uz.urgench.blog

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream


class MainActivity : AppCompatActivity() {
    private lateinit var btn: Button
    private lateinit var updateBtn: ImageButton

    companion object {
        lateinit var userName: String
        lateinit var userEmail: String
    }

    private lateinit var userPhoto: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val auth = Firebase.auth
        val currentUser = auth.currentUser
        updateUI(currentUser)
        updateBtn = findViewById(R.id.replaceList)
        btn = findViewById(R.id.addBlog)
        updateBtn.setOnClickListener { replaceList() }
        btn.setOnClickListener { addBlogBtn(btn) }
        replaceList()
    }

    override fun onBackPressed() {
        when (findViewById<Button>(R.id.addBlog).text.toString()) {
            getString(R.string.addBlogTextResource) -> {
                finish()
            }
            getString(R.string.next_add_resource) -> {
                btn.text = getString(R.string.addBlogTextResource)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, ListFragment(), null).commit()
            }
            getString(R.string.save_blog_resource) -> {
                btn.text = getString(R.string.next_add_resource)
                supportFragmentManager.beginTransaction()
                    .remove(AddTextDetalis())
                supportFragmentManager.beginTransaction()
                    .add(R.id.fragmentContainerView, AddFragment(), null).commit()
            }
        }
    }

    fun replaceList() {
        btn.text = getString(R.string.addBlogTextResource)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, ListFragment(), null).commit()
    }

    private fun updateUI(userInfo: FirebaseUser?) {
        if (userInfo != null) {
            userPhoto = userInfo.photoUrl.toString()
            userName = userInfo.displayName.toString()
            userEmail = userInfo.email.toString()
        } else startActivity(Intent(this, LoginSucces::class.java))
    }

    private fun addBlogBtn(v: Button) {
        when ((v).text.toString()) {
            getString(R.string.addBlogTextResource) -> {
                btn.text = getString(R.string.next_add_resource)
                supportFragmentManager.beginTransaction()
                    .add(R.id.fragmentContainerView, AddFragment(), null).commit()
            }
            getString(R.string.next_add_resource) -> {
                if (findViewById<EditText>(
                        R.id.editTextName
                    ).text.toString() != ""
                ) {
                    btn.text = getString(R.string.save_blog_resource)
                    supportFragmentManager.beginTransaction()
                        .add(R.id.fragmentContainerView, AddTextDetalis(), null).commit()
                    supportFragmentManager.beginTransaction()
                        .remove(AddFragment()).commit()
                } else {
                    btn.text = getString(R.string.next_add_resource)
                    supportFragmentManager.beginTransaction()
                        .add(R.id.fragmentContainerView, AddFragment(), null).commit()
                    Toast.makeText(this, "Сначала введите название", Toast.LENGTH_LONG).show()
                }
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

    fun putBD() {
        val editText: EditText = findViewById(R.id.editText)
        val editTextName: EditText = findViewById(R.id.editTextName)

        if (findViewById<ImageView>(R.id.uploadImage).visibility == View.VISIBLE) {
            val baos = ByteArrayOutputStream()
            findViewById<ImageView>(R.id.uploadImage).drawable.toBitmap()
                .compress(Bitmap.CompressFormat.JPEG, 90, baos)
            val byteArray = baos.toByteArray()
            val storage = Firebase.storage.reference
            storage.child("chatFiles/" + findViewById<EditText>(R.id.editTextName).text.toString())
                .putBytes(byteArray)
        }
        if (editTextName.text.toString() != "" && editText.text.toString() != "") {
            val db = Firebase.firestore
            db.collection("Blog").document(editTextName.text.toString())
                .get()
                .addOnSuccessListener {
                    if (it == null) {
                        val hash = hashMapOf<String, Any>(
                            "Text" to editText.text.toString(),
                            "UserName" to userName,
                            "UserPhoto" to userPhoto
                        )
                        db.collection("Blog").document(editTextName.text.toString())
                            .set(hash)
                            .addOnSuccessListener { Log.d("MyTag", "Successful") }
                            .addOnFailureListener { Log.d("MyTag", "Failed") }
                    } else Toast.makeText(
                        this,
                        "Запись с таким именем уже существет",
                        Toast.LENGTH_LONG
                    ).show()
                }

        }
    }
}