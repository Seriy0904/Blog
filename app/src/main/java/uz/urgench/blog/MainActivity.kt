package uz.urgench.blog

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var btn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn = findViewById(R.id.addBlog)
        btn.setOnClickListener { addBlogBtn(btn) }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, listFragment(), null).commit()
    }

    fun addBlogBtn(v: Button) {
        when ((v).text.toString()) {
            getString(R.string.addBlogTextResource) -> {
                btn.text = getString(R.string.save_blog_resource)
                supportFragmentManager.beginTransaction()
                    .add(R.id.fragmentContainerView, AddFragment(), null).commit()
            }
            getString(R.string.save_blog_resource) -> {
                putDB()
                btn.text = getString(R.string.addBlogTextResource)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, listFragment(), null).commit()
                supportFragmentManager.beginTransaction()
                    .remove(AddFragment()).commit()
            }
        }
    }

    fun putDB() {
        if (AddFragment.editTextName.text.toString() != "" &&
            AddFragment.editText.text.toString() != "" &&
            AddFragment.editUser.text.toString() != ""
        ) {
            val cont = ContentValues()
            val database = AddFragment.DbHelper.writableDatabase
            cont.put(DataBase.KEY_TEXT_NAME, AddFragment.editTextName.text.toString())
            cont.put(DataBase.KEY_TEXT, AddFragment.editText.text.toString())
            cont.put(DataBase.KEY_USER, AddFragment.editUser.text.toString())
            database.insert(DataBase.TABLE_BLOGS, null, cont)
            AddFragment.DbHelper.close()
            Log.d("MyTag", "Was Writed")
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
