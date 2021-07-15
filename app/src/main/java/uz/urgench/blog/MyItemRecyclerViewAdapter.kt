package uz.urgench.blog

import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.util.*

class MyItemRecyclerViewAdapter(
    private var textNameList: ArrayList<String>,
    private var textList: ArrayList<String>,
    private var userList: ArrayList<String>,
    private val photoUserList: ArrayList<String>,
    private val dateList: ArrayList<Timestamp>
) :
    RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.blog_recycler_item, parent, false)
        )

    private val db = Firebase.firestore

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            if (userList[position] != MainActivity.userName && MainActivity.userEmail != "sirojiddin.nuraddinov@gmail.com")
                holder.delete.visibility = View.GONE
            holder.delete.setOnClickListener {
                val dialog = AlertDialog.Builder(holder.delete.context)
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
                        dial.cancel()
                    }.setNeutralButton("Отмена") { dial, id -> }.show()
            }
            holder.blogInfo.setOnClickListener { startactivity(holder, position) }
            holder.textName.text = textNameList[position]
            holder.user.text = userList[position]
            holder.text.text = textList[position]
            val dL = dateList[position].toDate()
            holder.date.text = holder.date.context.getString(R.string.date,1900+dL.year,dL.month,dL.day,dL.hours,dL.minutes)
            Firebase.storage.reference.child("chatFiles/${textNameList[position]}").downloadUrl.addOnSuccessListener {
                holder.imageItem.visibility = View.VISIBLE
                Picasso.get().load(it).into(holder.imageItem)
            }.addOnFailureListener { }
            Picasso.get().load(photoUserList[position]).into(holder.photoUser)
        } catch (n: NullPointerException) {
            Log.d("MyTag", "$n")
        }
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
        val delete: ImageButton = itemView.findViewById(R.id.delete)
        val photoUser: ImageView = itemView.findViewById(R.id.userPhoto)
        val textName: TextView = itemView.findViewById(R.id.textName)
        val text: TextView = itemView.findViewById(R.id.text)
        val user: TextView = itemView.findViewById(R.id.userText)
        val date: TextView = itemView.findViewById(R.id.date)
    }
}