package uz.urgench.blog.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.customview.widget.ViewDragHelper
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import uz.urgench.blog.fragments.ListFragment
import uz.urgench.blog.fragments.ProfileFragment
import uz.urgench.blog.R
import uz.urgench.blog.databinding.ActivityMainBinding
import java.lang.reflect.Field

const val APP_PREFERENCE = "mysettings"
const val APP_PREFERENCE_THEME = "Theme"

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private val mListFragment = ListFragment()
    private val mProfileFragment = ProfileFragment()
    private var profileFragmentActive = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val sp = getSharedPreferences(APP_PREFERENCE, MODE_PRIVATE)
        setTheme(
            when (sp.getInt(APP_PREFERENCE_THEME, 0)) {
                1 -> R.style.OldTheme
                else -> R.style.MainTheme
            }
        )
        binding.navView.inflateHeaderView(R.layout.nav_header)
        if (Firebase.auth.currentUser == null) startActivity(Intent(this, LoginSucces::class.java))
        setContentView(binding.root)
        setSupportActionBar(binding.mainBar.mainBar)
        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.mainBar.mainBar,
            R.string.drawer_navigation_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        findViewById<NavigationView>(R.id.nav_view).setNavigationItemSelectedListener(this)
        try {
            val draggerField: Field = DrawerLayout::class.java.getDeclaredField("mLeftDragger")
            draggerField.isAccessible = true
            val vdh = draggerField.get(binding.drawerLayout)
            val edgeSizeField: Field = ViewDragHelper::class.java.getDeclaredField("mEdgeSize")
            edgeSizeField.isAccessible = true
            edgeSizeField.setInt(vdh, (edgeSizeField.get(vdh) as Int * 3))
        } catch (e: Exception) {
        }
        val navigationDrawer = binding.navView.getHeaderView(0)
        Firebase.firestore.collection("Accounts").document(Firebase.auth.currentUser?.email!!).get()
            .addOnSuccessListener {
                navigationDrawer.findViewById<TextView>(R.id.header_title_username).text =
                    it["CustomName"].toString()
                navigationDrawer.findViewById<TextView>(R.id.header_title_email).text =
                    Firebase.auth.currentUser?.email
                Glide.with(this).load(it["CustomPhoto"].toString())
                    .into(navigationDrawer.findViewById(R.id.account_photo))
            }
        toggle.syncState()
        supportFragmentManager.beginTransaction().add(R.id.fragmentContainerView, mListFragment)
            .add(R.id.fragmentContainerView, mProfileFragment).hide(mProfileFragment).commit()
        findViewById<NavigationView>(R.id.nav_view).setCheckedItem(R.id.nav_home)
        invalidateOptionsMenu()
    }

    private fun setFragment(fragmentInt: Int) {
        val hide = if (fragmentInt == 2) mListFragment else mProfileFragment
        val show = if (fragmentInt == 2) mProfileFragment else mListFragment
        supportFragmentManager.beginTransaction().show(show).hide(hide).commit()
//        if(fragmentInt==1)
//            mListFragment.iti()
    }

    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            return
        } else if (profileFragmentActive) {
            supportFragmentManager.beginTransaction().hide(mProfileFragment).commit()
            binding.navView.setCheckedItem(R.id.nav_home)
            return
        } else if (doubleBackToExitPressedOnce) {
            finish()
            return
        }
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Нажмите еще раз для выхода", Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed({
            doubleBackToExitPressedOnce = false
        }, 2000)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        menu?.findItem(R.id.replaceList)?.isVisible = !profileFragmentActive
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.replaceList -> mListFragment.putToList()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                supportFragmentManager.beginTransaction().hide(mProfileFragment).commit()
            }
            R.id.nav_chats -> {
                overridePendingTransition(android.R.anim.fade_out, android.R.anim.fade_in)
                startActivity(Intent(this, AddBlogActivity::class.java))
            }
            R.id.nav_profile -> {
                profileFragmentActive = true
                supportFragmentManager.beginTransaction()
                    .show(mProfileFragment).commit()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}