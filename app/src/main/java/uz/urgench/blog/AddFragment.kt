package uz.urgench.blog

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.Fragment

class AddFragment : Fragment() {
    companion object {
        lateinit var DbHelper: DataBase
        lateinit var editTextName: EditText
        lateinit var editText: EditText
        lateinit var editUser: EditText
    }

    private lateinit var clearDatabase: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editTextName = view.findViewById(R.id.editTextName)
        editText = view.findViewById(R.id.editText)
        editUser = view.findViewById(R.id.editUser)
        clearDatabase = view.findViewById(R.id.clearDatabase)
        clearDatabase.setOnClickListener {
            val dataBase = DbHelper.writableDatabase
            dataBase.delete(DataBase.TABLE_BLOGS, null, null);
            DbHelper.close()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add, container, false)
    }


}