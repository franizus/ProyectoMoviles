package com.example.frani.proyectomoviles

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    lateinit var dbHandler: DBUserHandlerAplicacion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val toolbar = findViewById<Toolbar>(R.id.toolbarProfile)
        setSupportActionBar(toolbar)

        dbHandler = DBUserHandlerAplicacion(this)

        textviewNameProfile.text = "${Factory.user?.nombre} ${Factory.user?.apellido}"
        editTextNameProfile.setText(Factory.user?.nombre)
        editTextLastnameProfile.setText(Factory.user?.apellido)
        editTextEmailProfile.setText(Factory.user?.email)

        btnEditProfile.setOnClickListener{
            val user = Factory.user!!
            user.nombre = editTextNameProfile.text.toString()
            user.apellido = editTextLastnameProfile.text.toString()
            user.email = editTextEmailProfile.text.toString()
            user.password = editTextPasswordProfile.text.toString()
            DBUserAPI.updateUser(user)
            finish()
            startActivity(intent)
        }

        btnDeleteProfile.setOnClickListener{
            DBUserAPI.deleteUser(Factory.user?.id!!)
            dbHandler.deleteUser(1)
            irActivityLogin()
            finish()
        }

        // add back arrow to toolbar
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
    }

    private fun irActivityLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
