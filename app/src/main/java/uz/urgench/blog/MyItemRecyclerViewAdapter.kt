package uz.urgench.blog

import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

class MyItemRecyclerViewAdapter(
    private var textNameList: ArrayList<String>,
    private var textList: ArrayList<String>,
    private var userList: ArrayList<String>,
    private val photoUserList: ArrayList<String>,
    private val context:FragmentActivity
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
            if(userList[position]!= MainActivity.userName&&MainActivity.userEmail !="sirojiddin.nuraddinov@gmail.com")
                holder.delete.visibility = View.GONE
            holder.delete.setOnClickListener{
                val dialog = AlertDialog.Builder(context)
                dialog.setTitle("Вы уверены?")
                    .setMessage("Вы уверены что хотите удалить запись ${textNameList[position]}?")
                    .setPositiveButton("Да, удалить") {dial,id ->
                            db.collection("Blog").document(textNameList[position]).delete()
                            Firebase.storage.reference.child("chatFiles/${textNameList[position]}").delete()
                            notifyItemRemoved(position)
                            textNameList.removeAt(textNameList.size-1)
                            userList.removeAt(userList.size-1)
                            textList.removeAt(textList.size-1)
                            photoUserList.removeAt(photoUserList.size-1)
                            dial.cancel()
                    }.setNeutralButton("Отмена"){dial,id->}.show()
            }
            holder.textName.text = textNameList[position]
            holder.user.text = userList[position]
            holder.text.text = textList[position]
            Firebase.storage.reference.child("chatFiles/${textNameList[position]}").downloadUrl.addOnSuccessListener {
                Picasso.get().load(it).into(holder.imageItem)
                holder.imageItem.visibility = View.VISIBLE
            }.addOnFailureListener {  }
            Picasso.get().load(photoUserList[position]).into(holder.photoUser)
        } catch (n: NullPointerException) {
            Log.d("MyTag","$n")
        }
    }
    override fun getItemCount() = textNameList.size
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageItem:ImageView = itemView.findViewById(R.id.imageItem)
        val delete:ImageButton = itemView.findViewById(R.id.delete)
        val photoUser:ImageView = itemView.findViewById(R.id.userPhoto)
        val textName: TextView = itemView.findViewById(R.id.textName)
        val text: TextView = itemView.findViewById(R.id.text)
        val user: TextView = itemView.findViewById(R.id.userText)
    }
}