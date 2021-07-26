package uz.urgench.blog

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
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.*


class MyItemRecyclerViewAdapter(
    private var textNameList: ArrayList<String>,
    private var textList: ArrayList<String>,
    private var userList: ArrayList<String>,
    private val dateList: ArrayList<Timestamp>,
    private val onUserClickListener: OnUserClickListener
) :
    RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.blog_recycler_item, parent, false)
        )

    interface OnUserClickListener {
        fun onClick(textName: String)
        fun onLongClick(textName: String)
    }

    private val db = Firebase.firestore
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            holder.blogInfo.setOnClickListener {
                try{
                    onUserClickListener.onClick(textNameList[position])
                    startactivity(holder, position,BlogSelected())
                }catch (e:IndexOutOfBoundsException){}
            }
            holder.comments.setOnClickListener {
                startactivity(holder, position,CommentActivity())
            }
            holder.blogInfo.setOnLongClickListener {
                onUserClickListener.onLongClick(textNameList[position])
                true
            }
            db.collection("Blog").document(textNameList[position])
                .get()
                .addOnSuccessListener { doc ->
                    if (doc["HavePhoto"] != null && doc["HavePhoto"] as Boolean) {
                        Firebase.storage.reference.child("chatFiles/${textNameList[position]}").downloadUrl
                            .addOnSuccessListener {
                                holder.imageItem.visibility = View.VISIBLE
                                Glide.with(holder.imageItem.context).load(it).into(holder.imageItem)
                            }
                    }
                }
            holder.textName.text = textNameList[position]
            db.collection("Accounts")
                .document(userList[position])
                .get().addOnSuccessListener {
                    holder.user.text = it["CustomName"].toString()
                    Glide.with(holder.photoUser.context).load(it["CustomPhoto"]).centerCrop()
                        .into(holder.photoUser)
                }
            holder.text.text = textList[position]
            val dL = dateList[position].toDate()
            holder.date.text = holder.date.context.getString(
                R.string.date,
                1900 + dL.year,
                dL.month + 1,
                dL.date,
                dL.hours,
                dL.minutes
            )
        } catch (n: NullPointerException) {
            Log.d("MyTag", "$n")
        }
    }

    fun startactivity(holder: ViewHolder, position: Int,clas:AppCompatActivity) {
        val intent = Intent(holder.textName.context, clas::class.java)
        intent.putExtra("BlogName", textNameList[position])
        holder.textName.context.startActivity(intent)
    }

    override fun getItemCount() = textNameList.size
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val blogInfo: LinearLayout = itemView.findViewById(R.id.blogInfo)
        val imageItem: ImageView = itemView.findViewById(R.id.imageItem)
        val photoUser: ImageView = itemView.findViewById(R.id.userPhoto)
        val textName: TextView = itemView.findViewById(R.id.textName)
        val text: TextView = itemView.findViewById(R.id.text)
        val user: TextView = itemView.findViewById(R.id.userText)
        val date: TextView = itemView.findViewById(R.id.date)
        val comments:ImageButton = itemView.findViewById(R.id.commentsButton)
    }
}