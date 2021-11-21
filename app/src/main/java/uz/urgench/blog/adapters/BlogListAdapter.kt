package uz.urgench.blog.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import uz.urgench.blog.R
import uz.urgench.blog.activities.BlogSelected
import uz.urgench.blog.activities.CommentActivity
import uz.urgench.blog.activities.OtherProfileActivity
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList


class BlogListAdapter(
    private val recurse: Boolean
) :
    RecyclerView.Adapter<BlogListAdapter.ViewHolder>() {
    private val blogModelList: ArrayList<BlogModel> = arrayListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.blog_recycler_item, parent, false)
        )
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun setList(newList: ArrayList<BlogModel>) {
        blogModelList.clear()
        blogModelList.addAll(newList)
        notifyDataSetChanged()
    }

    private val db = Firebase.firestore
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val nmodel = blogModelList[position]
        val email = Firebase.auth.currentUser?.email!!
        holder.blogInfo.setOnClickListener {
                startactivity(holder, position, BlogSelected::class.java)
        }
        holder.comments.setOnClickListener {//CommentsButton
            startactivity(holder, position, CommentActivity::class.java)
        }
        holder.userInfo.setOnClickListener {
            if (recurse) {
                val userProfileIntent =
                    Intent(holder.userInfo.context, OtherProfileActivity::class.java)
                userProfileIntent.putExtra("Email", nmodel.user)
                userProfileIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                it.context.startActivity(userProfileIntent)
            }
        }
        holder.imageItem.visibility = View.GONE
        if (nmodel.uri != null) {
            Glide.with(holder.imageItem.context).load(nmodel.uri)
                .into((holder.imageItem))
            holder.imageItem.visibility = View.VISIBLE
        }
        var likesList: ArrayList<String> = arrayListOf()
        val blogDir = db.collection("Blog").document(nmodel.textName)
        blogDir //Download photo from firebase storage
            .get().addOnSuccessListener { doc ->
                if (doc["LikeList"] != null) {
                    likesList = doc["LikeList"] as ArrayList<String>
                    holder.likeAmount.text =
                        if (likesList.size == 0) "" else likesList.size.toString()
                    if (likesList.contains(email))
                        holder.likeBut.background = ContextCompat.getDrawable(
                            holder.likeAmount.context,
                            R.drawable.like_icon_pressed
                        )
                    doc.reference.collection("Comments").get().addOnSuccessListener {
                        holder.commentsAmount.text =
                            if (it.size() > 0) it.size().toString() else ""
                    }
                }
            }
        holder.likeBut.setOnClickListener {
            val mLikeList = likesList
            if (mLikeList.contains(email)) {
                mLikeList.remove(email)
                blogDir.update(mapOf("LikeList" to mLikeList))
                holder.likeAmount.text =
                    if (mLikeList.size == 0) "" else mLikeList.size.toString()
                it.background = ContextCompat.getDrawable(
                    it.context,
                    R.drawable.like_icon
                )
            } else {
                mLikeList.add(email)
                blogDir.update(mapOf("LikeList" to mLikeList))
                holder.likeAmount.text = mLikeList.size.toString()
                it.background = ContextCompat.getDrawable(
                    it.context,
                    R.drawable.like_icon_pressed
                )
            }
        }
        holder.textName.text = nmodel.textName
        db.collection("Accounts")
            .document(nmodel.user)
            .get().addOnSuccessListener {
                holder.user.text = it["CustomName"].toString()
                Glide.with(holder.photoUser.context).load(it["CustomPhoto"]).centerCrop()
                    .into(holder.photoUser)
            }
        holder.text.text = nmodel.text
        val dL = nmodel.date.toDate()
        holder.date.text = holder.date.context.getString(
            R.string.date,
            1900 + dL.year,
            dL.month + 1,
            dL.date,
            dL.hours,
            dL.minutes
        )
    }

    private fun startactivity(holder: ViewHolder, position: Int, runActivity: Class<*>) {
        val nmodel = blogModelList[position]
        val intent = Intent(holder.textName.context, runActivity)
        intent.putExtra("BlogName", nmodel.textName)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        intent.putExtra("recurse", true)
        holder.textName.context.startActivity(intent)
    }

    override fun getItemCount() = blogModelList.size
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val blogInfo: LinearLayout = itemView.findViewById(R.id.blogInfo)
        val userInfo: LinearLayout = itemView.findViewById(R.id.userInfo)
        val imageItem: ImageView = itemView.findViewById(R.id.imageItem)
        val photoUser: ImageView = itemView.findViewById(R.id.userPhoto)
        val textName: TextView = itemView.findViewById(R.id.textName)
        val text: TextView = itemView.findViewById(R.id.text)
        val user: TextView = itemView.findViewById(R.id.userText)
        val date: TextView = itemView.findViewById(R.id.itemDate)
        val likeBut: ImageButton = itemView.findViewById(R.id.like_button_in_list)
        val likeAmount: TextView = itemView.findViewById(R.id.like_amount_in_list)
        val commentsAmount: TextView = itemView.findViewById(R.id.comments_amount_in_list)
        val comments: ImageButton = itemView.findViewById(R.id.comments_button_in_list)
    }
}