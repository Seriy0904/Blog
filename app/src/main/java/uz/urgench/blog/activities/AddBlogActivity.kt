package uz.urgench.blog.activities

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import uz.urgench.blog.R
import java.io.ByteArrayOutputStream
import java.util.*

class AddBlogActivity : AppCompatActivity() {
    private lateinit var textName: EditText
    private lateinit var text: EditText
    private lateinit var textDetalis: ImageButton
    private lateinit var saveBlog: ImageButton
    private lateinit var uploadImage: ImageView
    private val IMAGE_REQUEST = 65
    private val currentUser = Firebase.auth.currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        val sp = getSharedPreferences(APP_PREFERENCE, MODE_PRIVATE)
            setTheme(when(sp.getInt(APP_PREFERENCE_THEME,0)){
                1->R.style.OldTheme
                else -> R.style.MainTheme
            })
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_blog)
        setSupportActionBar(findViewById(R.id.addBlogToolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        textName = findViewById(R.id.editTextName)
        text = findViewById(R.id.editText)
        textDetalis = findViewById(R.id.textDetalis)
        saveBlog = findViewById(R.id.saveBlog)
        uploadImage = findViewById(R.id.uploadImage)
        textDetalis.setOnClickListener {
            val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(i, IMAGE_REQUEST)
        }
        saveBlog.setOnClickListener { putDB() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == IMAGE_REQUEST) {
            if (data?.data != null) {
                uploadImage.visibility = View.VISIBLE
                uploadImage.setImageURI(data.data)
            }
        }
    }

    private fun putDB() {
        val db = Firebase.firestore

        if (textName.text.toString() != "" && text.text.toString() != "") {
            val ymdhm = GregorianCalendar(TimeZone.getTimeZone("gmt"))
            db.collection("Blog").document(textName.text.toString())
                .get()
                .addOnSuccessListener { data ->
                    if (data["Text"] == null) {
                        val hash = hashMapOf<String, Any>(
                            "Text" to text.text.toString(),
                            "UserName" to currentUser?.email.toString(),
                            "UserPhoto" to currentUser?.photoUrl.toString(),
                            "Date" to ymdhm.time,
                            "HavePhoto" to (uploadImage.visibility == View.VISIBLE)
                        )
                        db.collection("Blog").document(textName.text.toString())
                            .set(hash)
                        if (uploadImage.visibility == View.VISIBLE) {
                            val baos = ByteArrayOutputStream()
                            uploadImage.drawable.toBitmap()
                                .compress(Bitmap.CompressFormat.JPEG, 70, baos)
                            val byteArray = baos.toByteArray()
                            val storage =
                                Firebase.storage.reference.child("chatFiles/" + textName.text.toString())
                            storage.putBytes(byteArray).addOnSuccessListener {
                                storage.downloadUrl.addOnSuccessListener { uri ->
                                    db.collection("Blog").document("${textName.text}")
                                        .update(mapOf("AddedPhoto" to uri.toString())).addOnSuccessListener {
                                            startActivity(Intent(this, MainActivity::class.java))
                                        }
                                }
                            }
                        }else startActivity(Intent(this, MainActivity::class.java))
                    } else {
                        Toast.makeText(this, "Запись ${data["Text"]} уже существет", Toast.LENGTH_LONG).show()
                    }
                }

        } else Toast.makeText(this, "Сначала введите имя и текст", Toast.LENGTH_LONG).show()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> super.onBackPressed()
        }
        return true
    }
}