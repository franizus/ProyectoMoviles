package com.example.frani.proyectomoviles

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login.*
import android.widget.Toast

class LoginActivity : AppCompatActivity() {

    lateinit var dbHandler: DBUserHandlerAplicacion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dbHandler = DBUserHandlerAplicacion(this)

        val user = dbHandler.getUser()

        if (user != null) {
            irActivityDrawer()
        }

        btn_login.setOnClickListener{
            login()
        }

        link_signup.setOnClickListener {
            irActivityRegistration()
        }
    }

    fun login() {
        if (!validate()) {
            onLoginFailed()
            return
        }

        btn_login.setEnabled(false)

        val email = input_email.text.toString()
        val password = input_password.text.toString()

        val user = DBUserAPI.getUser(email)
        if (user != null && user.password == password) {
            dbHandler.insertarUser(user)

            irActivityDrawer()
            finish()
        } else {
            onLoginFailed()
            return
        }
    }

    fun irActivityDrawer() {
        val intent = Intent(this, DrawerActivity::class.java)
        startActivity(intent)
    }

    fun irActivityRegistration() {
        val intent = Intent(this, RegistrationActivity::class.java)
        startActivity(intent)
    }

    fun onLoginFailed() {
        Toast.makeText(baseContext, getString(R.string.login_error), Toast.LENGTH_LONG).show()

        btn_login.setEnabled(true)
    }

    fun validate(): Boolean {
        var valid = true

        val email = input_email.text.toString()
        val password = input_password.text.toString()

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            input_email.error = getString(R.string.email_error)
            valid = false
        } else {
            input_email.error = null
        }

        if (password.isEmpty()) {
            input_password.error = getString(R.string.password_error)
            valid = false
        } else {
            input_password.error = null
        }

        return valid
    }

}
