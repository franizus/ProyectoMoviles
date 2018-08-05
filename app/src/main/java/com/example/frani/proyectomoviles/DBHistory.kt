package com.example.frani.proyectomoviles

import android.util.Log
import android.os.StrictMode
import com.beust.klaxon.*
import com.github.kittinunf.fuel.*


class DBHistory {

    companion object {

        fun insertarHistory(history: History) {
            "http://40.117.248.211/History".httpPost(listOf("text" to history.text, "translatedText" to history.translatedText, "language" to "", "translatedLanguage" to "", "userId" to history.userId))
                    .responseString { request, _, result ->
                        Log.d("http-ejemplo", request.toString())
                    }
        }

        fun deleteHistory(id: Int) {
            "http://40.117.248.211/History/$id".httpDelete()
                    .responseString { request, response, result ->
                        Log.d("http-ejemplo", request.toString())
                    }
        }

        fun getHistoryList(idUser: Int): ArrayList<History> {
            val historyList: ArrayList<History> = ArrayList()
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            val (request, response, result) = "http://40.117.248.211/History?userId=$idUser".httpGet().responseString()
            val jsonStringLibro = result.get()

            val parser = Parser()
            val stringBuilder = StringBuilder(jsonStringLibro)
            val array = parser.parse(stringBuilder) as JsonArray<JsonObject>

            array.forEach {
                val id = it["id"] as Int
                val text = it["text"] as String
                val translatedText = it["translatedText"] as String
                val history = History(id, text, translatedText, idUser)
                historyList.add(history)
            }
            return historyList
        }

    }

}
