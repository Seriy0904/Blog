package uz.urgench.blog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ListFragment : Fragment() {
    private val textList: ArrayList<String> = arrayListOf()
    private val textNameList: ArrayList<String> = arrayListOf()
    private val userList: ArrayList<String> = arrayListOf()
    private val photoUserList: ArrayList<String> = arrayListOf()
    lateinit var blogsList: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val db = Firebase.firestore
        db.collection("Blog").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    textNameList.add(document.id)
                    textList.add(document.get("Text").toString())
                    userList.add(document.get("UserName").toString())
                    photoUserList.add(document.get("UserPhoto").toString())
                }
                blogsList = view.findViewById(R.id.list)
                blogsList.layoutManager = LinearLayoutManager(activity)
                blogsList.adapter = MyItemRecyclerViewAdapter(textNameList, textList,userList,photoUserList)
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list_list, container, false)
    }
}