package com.example.frani.proyectomoviles

import android.os.StrictMode
import android.util.Log
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.github.kittinunf.fuel.*
import com.github.kittinunf.fuel.core.FuelManager

class DBUserAPI {

    companion object {

        fun insertarUser(user: User) {
            FuelManager.instance.baseHeaders = mapOf()
            "http://40.117.248.211/Users".httpPost(listOf("name" to user.nombre, "lastName" to user.apellido, "email" to user.email, "password" to user.password))
                    .responseString { request, _, result ->
                        Log.d("http-ejemplo", request.toString())
                    }
        }

        fun updateUser(user: User) {
            "http://40.117.248.211/Users/${user.id}".httpPut(listOf("name" to user.nombre, "lastName" to user.apellido, "email" to user.email, "password" to user.password))
                    .responseString { request, _, result ->
                        Log.d("http-ejemplo", request.toString())
                    }
        }

        fun deleteUser(id: Int) {
            "http://40.117.248.211/Users/$id".httpDelete()
                    .responseString { request, response, result ->
                        Log.d("http-ejemplo", request.toString())
                    }
        }

        fun getUser(email: String): User? {
            var user: User? = null
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            val (request, response, result) = "http://40.117.248.211/Users?email=$email".httpGet().responseString()
            val jsonStringLibro = result.get()

            val parser = Parser()
            val stringBuilder = StringBuilder(jsonStringLibro)
            val array = parser.parse(stringBuilder) as JsonArray<JsonObject>

            array.forEach {
                val id = it["id"] as Int
                val name = it["name"] as String
                val lastName = it["lastName"] as String
                val email = it["email"] as String
                val password = it["password"] as String
                user = User(id, name, lastName, email, password)
            }

            return user
        }

    }

}