package uz.urgench.blog

import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.*


class MyItemRecyclerViewAdapter(
    private var textNameList: ArrayList<String>,
    private var textList: ArrayList<String>,
    private var userList: ArrayList<String>,
    private val photoUserList: ArrayList<String>,
    private val dateList: ArrayList<Timestamp>,
    private val cont:ListFragment,
    private val onUserClickListener: OnUserClickListener
) :
    RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.blog_recycler_item, parent, false)
        )

    interface OnUserClickListener {
        fun onClick(textName: String, clas: MyItemRecyclerViewAdapter)
        fun onLongClick(textName: String, clas: MyItemRecyclerViewAdapter)
    }

    private val db = Firebase.firestore
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            val auth = Firebase.auth.currentUser
            Firebase.storage.reference.child("chatFiles/${textNameList[position]}").downloadUrl.addOnSuccessListener {
                holder.imageItem.visibility = View.VISIBLE
                Glide.with(holder.imageItem.context).load(it).into(holder.imageItem)
            }.addOnFailureListener { }
            holder.blogInfo.setOnClickListener {
                onUserClickListener.onClick(textNameList[position], this)
                startactivity(holder, position)
            }
            holder.blogInfo.setOnLongClickListener {
                onUserClickListener.onLongClick(textNameList[position], this)
                db.collection("Blog").document(textNameList[position])
                    .get()
                    .addOnSuccessListener {
                        if (auth?.email == "sirojiddin.nuraddinov@gmail.com") {
                            removeItem(holder, position)
                        }
                    }
                true
            }
            holder.textName.text = textNameList[position]
            db.collection("Accounts")
                .document(userList[position])
                .get().addOnSuccessListener {
                    holder.user.text = it["CustomName"].toString()
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
            Glide.with(holder.photoUser.context).load(photoUserList[position]).centerCrop()
                .into(holder.photoUser)
        } catch (n: NullPointerException) {
            Log.d("MyTag", "$n")
        }
    }

    fun removeItem(holder: ViewHolder, position: Int) {
        val dialog = AlertDialog.Builder(holder.text.context)
        dialog.setTitle("Вы уверены?")
            .setMessage("Вы уверены что хотите удалить запись ${textNameList[position]}?")
            .setPositiveButton("Да, удалить") { dial, id ->
                db.collection("Blog").document(textNameList[position]).delete()
                Firebase.storage.reference.child("chatFiles/${textNameList[position]}")
                    .delete()
                notifyItemRemoved(position)
                textNameList.removeAt(textNameList.size - 1)
                userList.removeAt(userList.size - 1)
                textList.removeAt(textList.size - 1)
                photoUserList.removeAt(photoUserList.size - 1)
//                notifyDataSetChanged()
//                dial.cancel()
            }.setNeutralButton("Отмена") { dial, id ->
                notifyDataSetChanged()
//                dial.cancel()
            }.setOnCancelListener {
                notifyDataSetChanged() }.show()

    }

    fun startactivity(holder: ViewHolder, position: Int) {
        val intent = Intent(holder.textName.context, BlogSelected()::class.java)
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
        val main: RelativeLayout = itemView.findViewById(R.id.main_item)
    }
}