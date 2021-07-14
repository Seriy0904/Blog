package uz.urgench.blog

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment


class AddTextDetalis : Fragment() {
    private val PICK_IMAGE_REQUEST = 329
    private lateinit var uploadImage: ImageView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uploadImage = view.findViewById(R.id.uploadImage)
        view.findViewById<ImageButton>(R.id.add_image).setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                329
            ) }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            if (requestCode === PICK_IMAGE_REQUEST && resultCode === AppCompatActivity.RESULT_OK) {
                uploadImage.visibility = View.VISIBLE
                uploadImage.setImageURI(data.data)
                Log.d("MyTag","Its must working")
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_text_detalis, container, false)
    }
}