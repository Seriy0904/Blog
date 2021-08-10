package uz.urgench.blog

import android.app.AlertDialog
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import uz.urgench.blog.activities.CommentActivity

class CommentsAdapter(
    private val commentUserList: ArrayList<String>,
    private val commentList: ArrayList<String>,
    private val commentDateList: ArrayList<Timestamp>?,
    private val context: CommentActivity
) : RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.coment_items, parent, false)
        )

    override fun getItemCount() = commentUserList.size
    private val db = Firebase.firestore
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (commentDateList != null) {
            val dL = commentDateList[position].toDate()
            holder.date.text = holder.date.context.getString(
                R.string.date,
                1900 + dL.year,
                dL.month + 1,
                dL.date,
                dL.hours,
                dL.minutes
            )
        } else holder.date.visibility = View.GONE
        holder.comment.text = commentList[position]
        db.collection("Accounts").document(commentUserList[position])
            .get()
            .addOnSuccessListener {
                holder.user.text = it["CustomName"].toString()
                Glide.with(holder.userPhoto.context).load(it["CustomPhoto"]).into(holder.userPhoto)
            }
        db.collection("Blog").document(context.intent.getStringExtra("BlogName")!!)
            .collection("Comments").document(context.commentsId[position]).get()
            .addOnSuccessListener {
                if (it["CommentUser"] == Firebase.auth.currentUser?.email)
                    holder.more.visibility = View.VISIBLE
            }
        holder.more.setOnClickListener {
            val popupMenu = PopupMenu(context, it)
            popupMenu.inflate(R.menu.comment_popup)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.delete_comment -> {
                        delete(position)
                        true
                    }
                    R.id.refactor_comment -> edit(commentList[position], position)
                    else -> false
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                popupMenu.setForceShowIcon(true)
            }
            popupMenu.show()
        }
    }

    private fun edit(oldText: String, position: Int): Boolean {
        val builder = AlertDialog.Builder(context)
        val view = context.layoutInflater.inflate(R.layout.edit_comment_dialog, null)
        val dial = builder.setView(view).show()
        val ediText = view.findViewById<EditText>(R.id.refactor_comment_edittext)
        ediText.setText(oldText)
        view.findViewById<ImageButton>(R.id.save_refactor_comment).setOnClickListener {
            db.collection("Blog").document(context.intent.getStringExtra("BlogName")!!)
                .collection("Comments").document(context.commentsId[position])
                .update(mapOf("CommentText" to ediText.text.toString()))
            context.putToList()
            dial.dismiss()
        }
        return true
    }

    private fun delete(position: Int) {
        AlertDialog.Builder(context)
            .setTitle("Вы уверены?")
            .setMessage("Вы уверены что хотите удалить комментарий ${commentList[position]}?")
            .setPositiveButton("Да, удалить") { dial, id ->
                db.collection("Blog").document(context.intent.getStringExtra("BlogName")!!)
                    .collection("Comments").document(context.commentsId[position]).delete()
                    .addOnSuccessListener { context.putToList() }
            }.setNeutralButton("Отмена") { _, _ ->
            }.setOnCancelListener {
            }.show()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val more: ImageButton = itemView.findViewById(R.id.more_button_comments)
        val comment: TextView = itemView.findViewById(R.id.textInComments)
        val user: TextView = itemView.findViewById(R.id.userNameInComments)
        val userPhoto: ImageView = itemView.findViewById(R.id.userPhotoInComments)
        val date: TextView = itemView.findViewById(R.id.commentDate)
    }
}

