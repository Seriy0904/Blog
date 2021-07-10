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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.lang.NullPointerException

class listFragment : Fragment() {
    private val textList: ArrayList<String> = arrayListOf()
    private val textNameList: ArrayList<String> = arrayListOf()
    private lateinit var blogsList: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            val db = Firebase.firestore
            db.collection("Blog").get()
                .addOnSuccessListener { documents ->
                    for (document in documents){
                        Log.d("INFOOOOOOOOOOO", document.id)
                        textNameList.add(document.id)
                    }
                }
                .addOnFailureListener {
                    Log.d("FAILED!!!!!!!!!","FAILED!!!!!!")
                }
            blogsList = view.findViewById(R.id.list)
            blogsList.layoutManager = LinearLayoutManager(activity)
            blogsList.adapter = MyItemRecyclerViewAdapter(
                textNameList,
                textList
            )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list_list, container, false)
    }
}