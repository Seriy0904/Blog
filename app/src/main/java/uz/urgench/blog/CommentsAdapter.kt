package uz.urgench.blog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CommentsAdapter(
    private val commentUserList: ArrayList<String>,
    private val commentList: ArrayList<String>,
    private val commentDateList: ArrayList<Timestamp>?,
    private val userClick:OnUserClickListener
) : RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.coment_items, parent, false)
        )

    override fun getItemCount() = commentUserList.size

    interface OnUserClickListener {
        fun delete(position:Int)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnLongClickListener {
            userClick.delete(position)
            true
        }
        val db = Firebase.firestore
        db.collection("Accounts").document(commentUserList[position])
            .get()
            .addOnSuccessListener {
                holder.user.text = it["CustomName"].toString()
                Glide.with(holder.userPhoto.context).load(it["CustomPhoto"]).into(holder.userPhoto)
            }
            if(commentDateList!=null){
                val dL = commentDateList[position].toDate()
                holder.date.text = holder.date.context.getString(
                    R.string.date,
                    1900 + dL.year,
                    dL.month + 1,
                    dL.date,
                    dL.hours,
                    dL.minutes
                )
            }else holder.date.visibility = View.GONE
        holder.comment.text = commentList[position]
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val comment: TextView = itemView.findViewById(R.id.textInComments)
        val user: TextView = itemView.findViewById(R.id.userNameInComments)
        val userPhoto: ImageView = itemView.findViewById(R.id.userPhotoInComments)
        val date: TextView = itemView.findViewById(R.id.commentDate)
    }
}