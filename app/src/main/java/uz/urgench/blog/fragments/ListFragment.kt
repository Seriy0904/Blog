package uz.urgench.blog.fragments

import android.app.AlertDialog
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import uz.urgench.blog.R
import uz.urgench.blog.adapters.BlogListAdapter
import uz.urgench.blog.adapters.BlogModel


class ListFragment : Fragment() {
    private val blogList = arrayListOf<BlogModel>()
    private val blogListAdapter = BlogListAdapter(true)
    private lateinit var swipeLayout: SwipeRefreshLayout
    private lateinit var blogsView: RecyclerView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        blogsView = view.findViewById(R.id.list)
        swipeLayout = view.findViewById(R.id.swipe_layout)
        blogsView.layoutManager = LinearLayoutManager(activity)
        blogListAdapter.setHasStableIds(true)
        blogsView.adapter = blogListAdapter
        ItemTouchHelper(gestures).attachToRecyclerView(blogsView)
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

    private val gestures = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ) = false

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float,
            dY: Float, actionState: Int, isCurrentlyActive: Boolean
        ) {
            RecyclerViewSwipeDecorator.Builder(
                activity!!,
                c, recyclerView, viewHolder, dX, dY,
                actionState, isCurrentlyActive
            )
                .addSwipeLeftBackgroundColor(ContextCompat.getColor(activity!!, R.color.red))
                .addSwipeLeftActionIcon(R.drawable.delete_icon_60)
                .create()
                .decorate()
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.absoluteAdapterPosition
            AlertDialog.Builder(activity)
                .setTitle("Вы уверены?")
                .setMessage("Вы уверены что хотите удалить запись ${blogList[position].textName}?")
                .setPositiveButton("Да, удалить") { _, _ ->
                    Firebase.firestore.collection("Blog").document(blogList[position].textName)
                        .delete()
                    Firebase.storage.reference.child("chatFiles/${blogList[position].textName}")
                        .delete()
                    putToList()
                }.setNeutralButton("Отмена") { _, _ ->
                    blogListAdapter.notifyDataSetChanged()
                }.setOnCancelListener {
                    blogListAdapter.notifyDataSetChanged()
                }.show()
//            blogListAdapter.removeItem(viewHolder as BlogListAdapter.ViewHolder, viewHolder.absoluteAdapterPosition)
        }

        override fun getSwipeDirs(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            if (blogList[viewHolder.absoluteAdapterPosition].user != Firebase.auth.currentUser?.email) return 0
            return super.getSwipeDirs(recyclerView, viewHolder)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list_list, container, false)
    }
}