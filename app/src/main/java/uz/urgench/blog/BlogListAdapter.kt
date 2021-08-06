package uz.urgench.blog

import android.content.Intent
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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class BlogListAdapter(
    private val textNameList: ArrayList<String>,
    private val textList: ArrayList<String>,
    private var userList: ArrayList<String>,
    private val dateList: ArrayList<Timestamp>
) :
    RecyclerView.Adapter<BlogListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.blog_recycler_item, parent, false)
        )
    }


    private val db = Firebase.firestore
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            val email = Firebase.auth.currentUser?.email!!
            holder.blogInfo.setOnClickListener {
                try {
                    startactivity(holder, position, BlogSelected())
                } catch (e: IndexOutOfBoundsException) {
                }
            }
            holder.comments.setOnClickListener {//CommentsButton
                startactivity(holder, position, CommentActivity())
            }
            var likesList: ArrayList<String> = arrayListOf()
            val blogDir = db.collection("Blog").document(textNameList[position])
            blogDir//Download photo from firebase storage
                .get().addOnSuccessListener { doc ->
                    if (doc["LikeList"] != null) {
                        likesList = doc["LikeList"] as ArrayList<String>
                        holder.likeAmount.text =
                            if (likesList.size == 0) "" else likesList.size.toString()
                    }
                    if (doc["AddedPhoto"] != null) {
                        Glide.with(holder.imageItem.context).load(doc.getString("AddedPhoto"))
                            .into((holder.imageItem))
                        holder.imageItem.visibility = View.VISIBLE

                    }
                }
            holder.likeBut.setOnClickListener {
                val mLikeList = likesList
                if (mLikeList.contains(email)) {
                    mLikeList.remove(email)
                    blogDir.update(mapOf("LikeList" to mLikeList))
                    holder.likeAmount.text =
                        if (mLikeList.size == 0) "" else mLikeList.size.toString()
                } else {
                    mLikeList.add(email)
                    blogDir.update(mapOf("LikeList" to mLikeList))
                    holder.likeAmount.text = mLikeList.size.toString()
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
        }
    }

    private fun startactivity(holder: ViewHolder, position: Int, clas: AppCompatActivity) {
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
        val date: TextView = itemView.findViewById(R.id.itemDate)
        val subscribe: TextView = itemView.findViewById(R.id.subscribe_blog_list)
        val likeBut: ImageButton = itemView.findViewById(R.id.like_buttonI_in_list)
        val likeAmount: TextView = itemView.findViewById(R.id.like_amount_in_list)
        val comments: ImageButton = itemView.findViewById(R.id.comments_button_in_list)
    }
}