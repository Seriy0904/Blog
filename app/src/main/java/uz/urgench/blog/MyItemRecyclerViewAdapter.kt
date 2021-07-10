package uz.urgench.blog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyItemRecyclerViewAdapter(
    private val textNameList: ArrayList<String>,
    private val textList: ArrayList<String>
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
        } catch (n: NullPointerException) {
        }
    }

    override fun getItemCount() = textNameList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textName: TextView = itemView.findViewById(R.id.textName)
        var text: TextView = itemView.findViewById(R.id.text)
    }

}