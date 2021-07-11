package uz.urgench.blog

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class LoginSucces : AppCompatActivity() {
    private lateinit var btn: Button
    lateinit var userName:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_succes)
        userName = intent.getStringExtra("userName").toString()
        btn = findViewById(R.id.addBlog)
        btn.setOnClickListener { addBlogBtn(btn) }
        replaceList(btn)
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
        val editText:EditText = findViewById(R.id.editText)
        val editTextName:EditText = findViewById(R.id.editTextName)
        if (editTextName.text.toString() != "" && editText.text.toString() != "") {
            val db = Firebase.firestore
            val hash = hashMapOf<String, Any>(
                "Text" to editText.text.toString(),
                "UserName" to userName
            )
            db.collection("Blog").document(editTextName.text.toString())
                .set(hash)
                .addOnSuccessListener { Log.d("MyTag", "Successful") }
                .addOnFailureListener { Log.d("MyTag", "Failed") }
        }
    }
//    fun putBD() {
//        if (AddFragment.editTextName.text.toString() != "" && AddFragment.editText.text.toString() != "") {
//            val db = Firebase.firestore
//            db.collection("Blog").document(AddFragment.editTextName.text.toString())
//                .get()
//                .addOnSuccessListener { doc ->
//                    if (doc.data == null) {
//                        val hash = hashMapOf<String,Any>(
//                            "Text" to AddFragment.editText.text.toString(),
//                            "UserName" to userName
//                        )
//                        db.collection("Blog").document(AddFragment.editTextName.text.toString())
//                            .set(hash)
//                            .addOnSuccessListener { Log.d("MyTag", "Successful") }
//                            .addOnFailureListener { Log.d("MyTag", "Failed") }
//                    } else {
//                        AddFragment.clone = true
//                        Toast.makeText(
//                            this,
//                            "Блог с таким названием уже существует",
//                            Toast.LENGTH_LONG
//                        ).show()
//                    }
//                }
//                .addOnFailureListener { Log.d("MyTag", "FAIL!!!!") }
//        }
//    }
}