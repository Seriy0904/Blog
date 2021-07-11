package uz.urgench.blog

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class MyItemRecyclerViewAdapter(
    private val textNameList: ArrayList<String>,
    private val textList: ArrayList<String>,
    private val userList: ArrayList<String>,
    private val photoUserList: ArrayList<String>
) :
    RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.blog_recycler_item, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            holder.textName.text = textNameList[position]
            holder.text.text = textList[position]
            holder.user.text = userList[position]
            Glide.with(holder.photoUser).load(photoUserList[position]).into(holder.photoUser)
        } catch (n: NullPointerException) {
            Log.d("MyTag","$n")
        }
    }
    override fun getItemCount() = textNameList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photoUser:ImageView = itemView.findViewById(R.id.userPhoto)
        val textName: TextView = itemView.findViewById(R.id.textName)
        val text: TextView = itemView.findViewById(R.id.text)
        val user: TextView = itemView.findViewById(R.id.userText)
    }

}