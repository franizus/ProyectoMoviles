package com.example.frani.proyectomoviles

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.TextView
import kotlinx.android.synthetic.main.content_drawer.*
import kotlinx.android.synthetic.main.activity_drawer.*
import kotlinx.android.synthetic.main.app_bar_drawer.*

class DrawerActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var dbHandler: DBUserHandlerAplicacion

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_text -> {
                loadFragment(TextFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_camera -> {
                loadFragment(CameraFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_voice -> {
                loadFragment(VoiceFragment())
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer)
        setSupportActionBar(toolbar)

        dbHandler = DBUserHandlerAplicacion(this)
        var user = dbHandler.getUser()
        Factory.user = user

        val headerView = nav_view.getHeaderView(0)
        val navname = headerView.findViewById<TextView>(R.id.navheaderName)
        navname.text = "${user?.nombre} ${user?.apellido}"
        val navemail = headerView.findViewById<TextView>(R.id.navheaderEmail)
        navemail.text = user?.email

        nav_view.menu.getItem(0).setChecked(true)
        nav_view.setNavigationItemSelectedListener(this)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        loadFragment(TextFragment())
    }

    override fun onResume() {
        super.onResume()
        nav_view.menu.getItem(0).setChecked(true)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {

            }
            R.id.nav_history -> {
                irActivityHistory()
            }
            R.id.nav_profile -> {
                irActivityProfile()
            }
            R.id.nav_logout -> {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(R.string.confirmation_session)
                        .setPositiveButton(R.string.yes, { dialog, which ->
                            dbHandler.deleteUser(1)
                            irActivityLogin()
                            finish()
                        }
                        )
                        .setNegativeButton(R.string.no, null)
                val dialogo = builder.create()
                dialogo.show()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun irActivityLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun irActivityHistory() {
        val intent = Intent(this, HistoryActivity::class.java)
        startActivity(intent)
    }

    private fun irActivityProfile() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    private fun loadFragment (fragment: Fragment?): Boolean {
        if (fragment != null) {
            supportFragmentManager.beginTransaction().replace(R.id.frame_layout, fragment).commit()

            return true
        }
        return false
    }
}
