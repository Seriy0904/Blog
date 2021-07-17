package uz.urgench.blog

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var btn: Button
    private lateinit var editText:EditText
    private lateinit var editTextName:EditText
    private val auth = Firebase.auth
    companion object {
        lateinit var userName: String
        lateinit var userEmail: String
    }

    private lateinit var userPhoto: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val currentUser = auth.currentUser
        updateUI(currentUser)
        btn = findViewById(R.id.addBlog)
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
                    .replace(R.id.fragmentContainerView, ListFragment(), null).commit()
                supportFragmentManager.beginTransaction()
                    .add(R.id.fragmentContainerView, AddFragment(), null).commit()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.exit_account -> {
                FirebaseAuth.getInstance().signOut()
                GoogleSignIn.getClient(this, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()).signOut()
                startActivity(Intent(this, LoginSucces::class.java))
            }
            R.id.replaceList -> replaceList()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun replaceList() {
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
                supportFragmentManager.beginTransaction()
                    .add(R.id.fragmentContainerView, AddFragment(), null).commit()
                btn.text = getString(R.string.next_add_resource)
            }
            getString(R.string.next_add_resource) -> {
                if (findViewById<EditText>(
                        R.id.editTextName
                    ).text.toString() != ""
                ) {
                    btn.text = getString(R.string.save_blog_resource)
                    supportFragmentManager.beginTransaction()
                        .remove(AddFragment()).commit()
                    supportFragmentManager.beginTransaction()
                        .add(R.id.fragmentContainerView, AddTextDetalis(), null).commit()
                } else {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, ListFragment(), null).commit()
                    btn.text = getString(R.string.addBlogTextResource)
                    Toast.makeText(this, "Сначала введите название", Toast.LENGTH_LONG).show()
                }
            }
            getString(R.string.save_blog_resource) -> {
                btn.text = getString(R.string.addBlogTextResource)
                putBD()
                supportFragmentManager.beginTransaction()
                    .remove(AddFragment())
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, AddTextDetalis(), null).commit()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, ListFragment(), null).commit()
            }
        }
    }

    private fun putBD() {
        editText= findViewById(R.id.editText)
        editTextName= findViewById(R.id.editTextName)
        if (findViewById<ImageView>(
                R.id.uploadImage
            ).visibility == View.VISIBLE
        ) {
            val baos = ByteArrayOutputStream()
            findViewById<ImageView>(R.id.uploadImage).drawable.toBitmap()
                .compress(Bitmap.CompressFormat.JPEG, 85, baos)
            val byteArray = baos.toByteArray()
            val storage = Firebase.storage.reference
            storage.child("chatFiles/" + findViewById<EditText>(R.id.editTextName).text.toString())
                .putBytes(byteArray)
        }
        if (editTextName.text.toString() != "" && editText.text.toString() != "") {
            val db = Firebase.firestore
            val ymdhm = GregorianCalendar(TimeZone.getTimeZone("gmt"))
            db.collection("Blog").document(editTextName.text.toString())
                .get()
                .addOnSuccessListener {data->
                    if (data["Text"] == null) {
                        val hash = hashMapOf<String, Any>(
                            "Text" to editText.text.toString(),
                            "UserName" to userName,
                            "UserPhoto" to userPhoto,
                            "Date" to ymdhm.time,
                        )
                        db.collection("Blog").document(editTextName.text.toString())
                            .set(hash)
                            .addOnSuccessListener { Log.d("MyTag", "Successful Upload ${data.data}") }
                            .addOnFailureListener { Log.d("MyTag", "Failed") }
                    } else {
                        Toast.makeText(
                            this,
                            "Запись с таким именем уже существет",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.d("MyTag", "$data")
                    }
                }

        }
    }
}