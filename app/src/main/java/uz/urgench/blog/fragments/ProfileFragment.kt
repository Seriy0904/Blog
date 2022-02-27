package uz.urgench.blog.fragments

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.graphics.drawable.toBitmap
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import uz.urgench.blog.R
import uz.urgench.blog.activities.*
import uz.urgench.blog.databinding.ActivityMainBinding
import java.io.ByteArrayOutputStream


class ProfileFragment : Fragment() {
    private val IMAGE_REQUEST = 53
    private lateinit var userName: EditText
    private lateinit var userEmail: TextView
    private lateinit var userPhoto: ImageView
    private lateinit var exit: Button
    private lateinit var saveName: ImageButton
    private lateinit var spinnerThemes: Spinner
    private lateinit var myBlogsButton: TextView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val auth = Firebase.auth.currentUser
        val db = Firebase.firestore.collection("Accounts").document(auth?.email!!)
        val sp = requireActivity().getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE)
        myBlogsButton = view.findViewById(R.id.my_blogs)
        spinnerThemes = view.findViewById(R.id.themes_spinner)
        userName = view.findViewById(R.id.header_title_username_profile)
        userEmail = view.findViewById(R.id.header_title_email_profile)
        userPhoto = view.findViewById(R.id.account_photo_profile)
        exit = view.findViewById(R.id.exit_account)
        saveName = view.findViewById(R.id.save_name)
        var oldName = ""
        db.get().addOnSuccessListener {
            oldName = it["CustomName"].toString()
            userName.setText(it["CustomName"].toString())
            Glide.with(userPhoto.context).load(it["CustomPhoto"]).into(userPhoto)
        }
        userEmail.text = auth.email
        exit.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Вы уверены что хотите выйти из аккаунта?")
                .setNeutralButton("Да, уверен"){_,_ ->
                    FirebaseAuth.getInstance().signOut()
                    GoogleSignIn.getClient(
                        requireActivity(),
                        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                    ).signOut()
                    startActivity(Intent(activity, LoginSucces::class.java))
                }.setNegativeButton("Отмена"){_,_->
                }.create().show()
        }
        myBlogsButton.setOnClickListener {
            val myBlogs = Intent(requireContext(), OtherProfileActivity::class.java)
            myBlogs.putExtra("myProfile", true)
            myBlogs.putExtra("Email", Firebase.auth.currentUser?.email)
            startActivity(myBlogs)
        }
        userName.addTextChangedListener {
            if (oldName != it.toString())
                saveName.visibility = View.VISIBLE
            else if (oldName == it.toString() || it.toString() == "$oldName ")
                saveName.visibility = View.GONE
        }
        saveName.setOnClickListener {
            val map = mapOf<String, Any>("CustomName" to userName.text.toString())
            ActivityMainBinding.inflate(layoutInflater).navView.setCheckedItem(R.id.nav_home)
            db.update(map)
            oldName = userName.text.toString()
            it.visibility = View.GONE
            userName.clearFocus()
            val baos = ByteArrayOutputStream()
            sp?.edit()?.putInt(APP_PREFERENCE_THEME, spinnerThemes.selectedItemPosition)
                ?.apply()
            userPhoto.drawable.toBitmap().compress(Bitmap.CompressFormat.JPEG, 50, baos)
            val byteArray = baos.toByteArray()
            val storage =
                Firebase.storage.reference.child("accountsFiles/${Firebase.auth.currentUser?.email}/customAccountPhoto")
            storage.putBytes(byteArray).addOnSuccessListener {
                storage.downloadUrl.addOnSuccessListener { uri ->
                    Firebase.firestore.collection("Accounts")
                        .document(Firebase.auth.currentUser?.email!!)
                        .update(mapOf("CustomPhoto" to uri.toString())).addOnSuccessListener {
                            startActivity(Intent(activity, MainActivity::class.java))
                            activity?.finish()
                        }
                }
            }

        }
        if (sp != null) {
            spinnerThemes.setSelection(sp.getInt(APP_PREFERENCE_THEME, 0))
        }
        spinnerThemes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == sp.getInt(APP_PREFERENCE_THEME, 0))
                    saveName.visibility = View.GONE
                else
                    saveName.visibility = View.VISIBLE
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}

        }
        userPhoto.setOnClickListener {
            val i = Intent(Intent.ACTION_PICK)
            i.type = "image/*"
            startActivityForResult(i, IMAGE_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == IMAGE_REQUEST) {
            if (data?.data != null) {
                Glide.with(requireActivity()).load(data.data.toString()).into(userPhoto)
                saveName.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_profile, container, false)
}