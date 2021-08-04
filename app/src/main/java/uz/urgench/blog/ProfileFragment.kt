package uz.urgench.blog

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ProfileFragment : Fragment() {
    private lateinit var userName:EditText
    private lateinit var userEmail:TextView
    private lateinit var userPhoto:ImageView
    private lateinit var exit:Button
    private lateinit var save_name:ImageButton
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userName = view.findViewById(R.id.header_title_username_profile)
        userEmail = view.findViewById(R.id.header_title_email_profile)
        userPhoto = view.findViewById(R.id.account_photo_profile)
        exit = view.findViewById(R.id.exit_account)
        save_name = view.findViewById(R.id.save_name)
        val auth = Firebase.auth.currentUser
        Firebase.firestore.collection("Accounts")
            .document(auth?.email!!)
            .get().addOnSuccessListener {
                userName.setText(it["CustomName"].toString())
            }
        userEmail.text = auth.email
        Glide.with(userPhoto.context).load(auth.photoUrl).into(userPhoto)
        exit.setOnClickListener {
            if(activity!=null){
                FirebaseAuth.getInstance().signOut()
                GoogleSignIn.getClient(requireActivity(), GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()).signOut()
                startActivity(Intent(activity, LoginSucces::class.java))
            } }
        userName.setOnFocusChangeListener { v, hasFocus ->
            save_name.visibility = View.VISIBLE
        }
        save_name.setOnClickListener {
            val map = hashMapOf<String,Any>("CustomName" to userName.text.toString())
            Firebase.firestore.collection("Accounts")
                .document(auth.email!!)
                .set(map)
            userName.clearFocus()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }
}