package uz.urgench.blog

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var btn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn = findViewById(R.id.addBlog)
        btn.setOnClickListener { addBlogBtn(btn) }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, ListFragment(), null).commit()
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
                    .replace(R.id.fragmentContainerView, ListFragment(), null).commit()
                supportFragmentManager.beginTransaction()
                    .remove(AddFragment()).commit()
            }
        }
    }

    fun putBD() {
        if (AddFragment.editTextName.text.toString() != "" && AddFragment.editText.text.toString() != "") {
            val db = Firebase.firestore
            db.collection("Blog").document(AddFragment.editTextName.text.toString())
                .get()
                .addOnSuccessListener { doc ->
                    if (doc.data == null) {
                        db.collection("Blog").document(AddFragment.editTextName.text.toString())
                            .set("Text" to AddFragment.editText.text.toString())
                            .addOnSuccessListener { Log.d("MyTag", "Successful") }
                            .addOnFailureListener { Log.d("MyTag", "Failed") }
                    } else {
                        AddFragment.clone = true
                        Toast.makeText(
                            this,
                            "Блог с таким названием уже существует",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                .addOnFailureListener { Log.d("MyTag", "FAIL!!!!") }
        }

    }
}
// contextual.put(DataBase.KEY_TEXT, inputText)
// dataBase.insert(DataBase.TABLE_BLOGS, null, contextual)

// val cursor =
//     dataBase.query(DataBase.TABLE_BLOGS, null, null, null, null, null, null)


// if (cursor.moveToFirst()) {
//     val idIndex = cursor.getColumnIndex(DataBase.KEY_ID)
//     val textIndex = cursor.getColumnIndex(DataBase.KEY_TEXT)
//     do {
//         Log.d(
//             "MyLog",
//             "Id: ${cursor.getString(idIndex)}, Text:${cursor.getString(textIndex)}"
//         )
//     } while (cursor.moveToNext())
//     cursor.close()
