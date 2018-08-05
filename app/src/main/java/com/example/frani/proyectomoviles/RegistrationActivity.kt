package com.example.frani.proyectomoviles

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_registration.*

class RegistrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        btn_signup.setOnClickListener {
            signup()
        }

        link_login.setOnClickListener {
            irActivityLogin()
        }
    }

    private fun irActivityLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    fun signup() {
        if (!validate()) {
            onSignupFailed()
            return
        }

        btn_signup.setEnabled(false)

        val name = input_name.text.toString()
        val lastname = input_lastname.text.toString()
        val email = input_emailR.text.toString()
        val password = input_passwordR.text.toString()

        val user = User(0, name, lastname, email, password)
        DBUserAPI.insertarUser(user)
        Toast.makeText(baseContext, getString(R.string.signup), Toast.LENGTH_LONG).show()
        irActivityLogin()
        finish()
    }

    fun onSignupFailed() {
        Toast.makeText(baseContext, getString(R.string.signup_error), Toast.LENGTH_LONG).show()

        btn_signup.setEnabled(true)
    }

    fun validate(): Boolean {
        var valid = true

        val name = input_name.text.toString()
        val lastname = input_lastname.text.toString()
        val email = input_emailR.text.toString()
        val password = input_passwordR.text.toString()

        if (name.isEmpty()) {
            input_name.error = getString(R.string.name_error)
            valid = false
        } else {
            input_name.error = null
        }

        if (lastname.isEmpty()) {
            input_lastname.error = getString(R.string.lastname_error)
            valid = false
        } else {
            input_lastname.error = null
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            input_emailR.error = getString(R.string.email_error)
            valid = false
        } else {
            input_emailR.error = null
        }

        if (password.isEmpty()) {
            input_passwordR.error = getString(R.string.password_error)
            valid = false
        } else {
            input_passwordR.error = null
        }

        return valid
    }
}
