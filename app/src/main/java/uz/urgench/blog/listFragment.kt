package uz.urgench.blog

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.lang.NullPointerException

class listFragment : Fragment() {
    private val textList: ArrayList<String> = arrayListOf()
    private val textNameList: ArrayList<String> = arrayListOf()
    private val userList: ArrayList<String> = arrayListOf()
    private lateinit var blogsList: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AddFragment.DbHelper = DataBase(context)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            val dataBase: SQLiteDatabase = AddFragment.DbHelper.writableDatabase
            val cursor =
                dataBase.query(DataBase.TABLE_BLOGS, null, null, null, null, null, null)
            if (cursor.moveToFirst()) {
                val text = cursor.getColumnIndex(DataBase.KEY_TEXT)
                val textName = cursor.getColumnIndex(DataBase.KEY_TEXT_NAME)
                val textUser = cursor.getColumnIndex(DataBase.KEY_USER)
                do {
                    textNameList.add(cursor.getString(textName))
                    textList.add(cursor.getString(text))
                    userList.add(cursor.getString(textUser))
                } while (cursor.moveToNext())
                cursor.close()
                blogsList = view.findViewById(R.id.list)
                blogsList.layoutManager = LinearLayoutManager(activity)
                blogsList.adapter = MyItemRecyclerViewAdapter(
                    textNameList,
                    textList,
                    userList
                )
            }
            AddFragment.DbHelper.close()
        } catch (e:NullPointerException) {
            Log.d("MyLog", "$e")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list_list, container, false)
    }
}