package uz.urgench.blog.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import uz.urgench.blog.R

class LoginSucces : AppCompatActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 154
    private lateinit var auth: FirebaseAuth
    private lateinit var reg_btn: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sp = getSharedPreferences(APP_PREFERENCE, MODE_PRIVATE)
            setTheme(when(sp.getInt(APP_PREFERENCE_THEME,0)){
                1->R.style.OldTheme
                else -> R.style.MainTheme
            })
        setContentView(R.layout.activity_login_succes)
        auth = Firebase.auth
        val currentUser = auth.currentUser
        reg_btn = findViewById(R.id.singIn)
        reg_btn.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
        updateUI(currentUser)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("722083836199-h7bff22bi9j0ith5n80ouqls4f6abnpa.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    override fun onBackPressed() {
        Toast.makeText(this, "Сначала войдите", Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.d("MyTag","Error: ${e.localizedMessage}")
            }
        }
    }

    private fun updateUI(userInfo: FirebaseUser?) {
        if (userInfo != null) {
            val intent = Intent(this, MainActivity::class.java)
            Firebase.firestore.collection("Accounts").document(userInfo.email!!)
                .get().addOnSuccessListener {
                    if (it["CustomName"] == null) {
                        Firebase.firestore.collection("Accounts")
                            .document(userInfo.email!!)
                            .set(
                                hashMapOf(
                                    "CustomName" to userInfo.displayName!!,
                                    "CustomPhoto" to userInfo.photoUrl!!.toString()
                                )
                            ).addOnSuccessListener { startActivity(intent) }
                    }else if(it["CustomPhoto"] == null){
                        Firebase.firestore.collection("Accounts")
                            .document(userInfo.email!!)
                            .update(
                                mapOf("CustomPhoto" to userInfo.photoUrl!!.toString())
                            ).addOnSuccessListener { startActivity(intent) }
                    }else startActivity(intent)
                }

        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                // Sign in success, update UI with the signed-in user's information
                Log.d("TAG", "signInWithCredential:success")
                val user = auth.currentUser
                updateUI(user)
            }

    }

}