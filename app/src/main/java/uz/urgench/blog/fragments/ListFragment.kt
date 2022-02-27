package uz.urgench.blog.fragments

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import uz.urgench.blog.R
import uz.urgench.blog.adapters.BlogListAdapter
import uz.urgench.blog.adapters.BlogModel


class ListFragment : Fragment() {
    private val blogList = arrayListOf<BlogModel>()
    private val blogListAdapter = BlogListAdapter(true)
    private lateinit var swipeLayout: SwipeRefreshLayout
    private lateinit var blogsView: RecyclerView
    private var listSavedState:Parcelable? = null
    override fun onHiddenChanged(hidden: Boolean) {
        Log.d("MyTag","Adapter: ${blogsView.adapter}")
        Log.d("MyTag","AdapterRecycler: $blogListAdapter")
        blogsView.adapter = blogListAdapter
        listSavedState = blogsView.layoutManager?.onSaveInstanceState()
        if(!hidden)
            blogsView.layoutManager?.onRestoreInstanceState(listSavedState)
        super.onHiddenChanged(hidden)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        putToList()
        swipeLayout.setColorSchemeColors(
            ContextCompat.getColor(
                requireContext(),
                R.color.blue_and_purple
            )
        )
        swipeLayout.setOnRefreshListener {
            putToList()
        }
    }

    fun putToList() {
        blogList.clear()
        val db = Firebase.firestore
        db.collection("Blog").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    blogList.add(
                        BlogModel(
                            textName = document.id,
                            text = document.get("Text").toString(),
                            user = document.get("UserName").toString(),
                            date = document.get("Date") as Timestamp,
                            uri = document.getString("AddedPhoto")
                        )
                    )
                }
                blogListAdapter.setList(blogList)
                swipeLayout.isRefreshing = false
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list_list, container, false)
        blogsView = view.findViewById(R.id.list)
        swipeLayout = view.findViewById(R.id.swipe_layout)
        blogsView.layoutManager = LinearLayoutManager(activity)
        blogListAdapter.setHasStableIds(true)
        blogsView.adapter = blogListAdapter
        return view
    }
}