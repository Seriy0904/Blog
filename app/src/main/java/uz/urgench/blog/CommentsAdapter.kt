package uz.urgench.blog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CommentsAdapter(
    val userName: ArrayList<String>,
    val commentList: ArrayList<String>
) : RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.coment_items, parent, false)
        )

    override fun getItemCount() = userName.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val db = Firebase.firestore
        db.collection("Accounts").document(userName[position])
            .get()
            .addOnSuccessListener {
                holder.user.text = it["CustomName"].toString()
                Glide.with(holder.userPhoto.context).load(it["CustomPhoto"]).into(holder.userPhoto)
            }
        holder.comment.text=commentList[position]
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val comment: TextView = itemView.findViewById(R.id.textInComments)
        val user: TextView = itemView.findViewById(R.id.userNameInComments)
        val userPhoto: ImageView = itemView.findViewById(R.id.userPhotoInComments)
    }
}