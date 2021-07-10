package uz.urgench.blog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddFragment : Fragment() {
    private lateinit var clearDatabase: Button
    private lateinit var editText: EditText
    private lateinit var editTextName: EditText
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editText = view.findViewById(R.id.editText)
        editText = view.findViewById(R.id.editTextName)
        try{
            if (editTextName.text.toString() != "" &&
                editText.text.toString() != ""
            ) {
                val db = Firebase.firestore
                db.collection("Blog").document(editTextName.text.toString())
                    .set(hashMapOf("Text" to editText.text.toString()))
                    .addOnSuccessListener { Log.d("MyTag", "Successful") }
                    .addOnFailureListener { Log.d("MyTag", "Failed") }
            }
        }catch (k:UninitializedPropertyAccessException){}
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add, container, false)
    }


}