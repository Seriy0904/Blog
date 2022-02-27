package uz.urgench.blog.adapters

import com.google.firebase.Timestamp

data class BlogModel(
    val textName: String,
    val text: String,
    val user: String,
    val date: Timestamp,
    val uri: String?,
    val uriList:ArrayList<String> = arrayListOf()
)
