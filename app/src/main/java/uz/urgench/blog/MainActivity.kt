package uz.urgench.blog

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.customview.widget.ViewDragHelper
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import uz.urgench.blog.databinding.ActivityMainBinding
import java.lang.reflect.Field


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    var onFragment: Short = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Firebase.auth.currentUser == null) startActivity(Intent(this, LoginSucces::class.java))
        binding = ActivityMainBinding.inflate(layoutInflater)
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
                navigationDrawer.findViewById<TextView>(R.id.header_title_username).text = it["CustomName"].toString()
                navigationDrawer.findViewById<TextView>(R.id.header_title_email).text =
                    Firebase.auth.currentUser?.email
                Glide.with(this).load(it["CustomPhoto"].toString())
                    .into(navigationDrawer.findViewById(R.id.account_photo))
            }
        toggle.syncState()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, ListFragment(), null).commit()
        findViewById<NavigationView>(R.id.nav_view).setCheckedItem(R.id.nav_home)
    }

    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            return
        } else if (onFragment == (2).toShort()) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, ListFragment(), null).commit()
            onFragment = 1
            findViewById<NavigationView>(R.id.nav_view).setCheckedItem(R.id.nav_home)
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
        if (onFragment == (2).toShort()) {
            menu?.findItem(R.id.replaceList)?.isVisible = false
        } else if (onFragment == (1).toShort())
            menu?.findItem(R.id.replaceList)?.isVisible = true
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.replaceList -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, ListFragment(), null).commit()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home ->
                if (onFragment != (1).toShort()) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, ListFragment(), null).commit()
                    onFragment = 1
                    invalidateOptionsMenu()
                }
            R.id.nav_profile -> {
                if (onFragment != (2).toShort())
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, ProfileFragment(), null).commit()
                onFragment = 2
                invalidateOptionsMenu()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}