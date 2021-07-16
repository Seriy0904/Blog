package uz.urgench.blog

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

class AddFragment : Fragment() {
    private lateinit var editTextName: EditText
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editTextName= view.findViewById(R.id.editTextName)
        editTextName.setOnEditorActionListener { v, actionId, event ->
            if ((event != null && (event.keyCode == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                if (view.findViewById<EditText>(R.id.editTextName).text.toString() != "") {
                    activity?.findViewById<Button>(R.id.addBlog)?.text = getString(R.string.save_blog_resource)
                    activity?.supportFragmentManager?.beginTransaction()
                        ?.remove(AddFragment())?.commit()
                    activity?.supportFragmentManager?.beginTransaction()
                        ?.add(R.id.fragmentContainerView, AddTextDetalis(), null)?.commit()
                } else Toast.makeText(activity, "Сначала введите название", Toast.LENGTH_LONG).show()
            }
            false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add, container, false)
    }

}
