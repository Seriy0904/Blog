package uz.urgench.blog

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MyItemRecyclerViewAdapter(
    private var textNameList: ArrayList<String>,
    private var textList: ArrayList<String>,
    private var userList: ArrayList<String>,
    private val photoUserList: ArrayList<String>
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
            if(userList[position]!=MainActivity.userName)
                holder.delete.visibility = View.GONE
            holder.delete.setOnClickListener{
                if(userList[position]==MainActivity.userName) {
                    db.collection("Blog").document(textNameList[position]).delete()
                    textNameList.removeAt(textNameList.size-1)
                    userList.removeAt(userList.size-1)
                    textList.removeAt(textList.size-1)
                    photoUserList.removeAt(photoUserList.size-1)
                    notifyItemRemoved(position)}
            }
            holder.textName.text = textNameList[position]
            holder.user.text = userList[position]
            holder.text.text = textList[position]
            Glide.with(holder.photoUser).load(photoUserList[position]).into(holder.photoUser)
        } catch (n: NullPointerException) {
            Log.d("MyTag","$n")
        }
    }
    override fun getItemCount() = textNameList.size
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val delete:ImageButton = itemView.findViewById(R.id.delete)
        val photoUser:ImageView = itemView.findViewById(R.id.userPhoto)
        val textName: TextView = itemView.findViewById(R.id.textName)
        val text: TextView = itemView.findViewById(R.id.text)
        val user: TextView = itemView.findViewById(R.id.userText)
    }
}