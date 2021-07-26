package uz.urgench.blog

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import uz.urgench.blog.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{
    private lateinit var binding: ActivityMainBinding
    private var onFragment: Short = 1
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
        val navigationDrawer: View = findViewById<NavigationView>(R.id.nav_view).getHeaderView(0)
        navigationDrawer.findViewById<TextView>(R.id.header_title_username).text =
            Firebase.auth.currentUser?.displayName
        navigationDrawer.findViewById<TextView>(R.id.header_title_email).text =
            Firebase.auth.currentUser?.email
        Glide.with(this).load(Firebase.auth.currentUser?.photoUrl)
            .into(navigationDrawer.findViewById(R.id.account_photo))
        toggle.syncState()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, ListFragment(), null).commit()
        findViewById<NavigationView>(R.id.nav_view).setCheckedItem(R.id.nav_home)
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else if (onFragment == (2).toShort()) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, ListFragment(), null).commit()
            onFragment = 1
            findViewById<NavigationView>(R.id.nav_view).setCheckedItem(R.id.nav_home)
        } else super.onBackPressed()
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