package com.example.frani.proyectomoviles

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Button
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import kotlinx.android.synthetic.main.fragment_text.*

class TextFragment : Fragment() {

    val languageMap: MutableMap<String, String> = mutableMapOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var rootView = inflater.inflate(R.layout.fragment_text, container, false)

        FuelManager.instance.basePath = "https://api.cognitive.microsofttranslator.com"

        loadSpinner()

        var btn1 = rootView.findViewById<Button>(R.id.btnTranslate)

        btn1.setOnClickListener{
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editTextFrom.windowToken, 0)
            translate(editTextFrom.text.toString())
        }

        return rootView
    }

    fun loadSpinner() {
        Fuel.get("/languages?api-version=3.0&scope=translation").responseString { request, response, result ->
            val languagesList: ArrayList<String> = ArrayList()
            val json = result.get()
            val parser = Parser()
            val stringBuilder = StringBuilder(json)
            val jsonObj = parser.parse(stringBuilder) as JsonObject
            val translation = jsonObj["translation"] as JsonObject
            translation.forEach{
                val jObj = it.value as JsonObject
                val name = jObj.string("name")
                languageMap[name!!] = it.key
                languagesList.add(name)
            }
            val adapterSpinner = ArrayAdapter<String>(
                    activity,
                    android.R.layout.simple_spinner_item,
                    languagesList
            )
            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerTo.adapter = adapterSpinner
        }
    }

    fun translate(text: String) {
        val selectedItem = spinnerTo.selectedItem.toString()
        val to = languageMap[selectedItem]
        FuelManager.instance.baseHeaders = mapOf("Ocp-Apim-Subscription-Key" to "8c2b57fc4b314d41a3a25fc0988e5163", "Content-Type" to "application/json")

        Fuel.post("/translate?api-version=3.0&to=$to").body("[{'Text':'$text'}]").responseString { request, response, result ->
            Log.d("http-request", request.cUrlString())
            Log.d("http-response", response.toString())
            Log.d("http-result", result.toString())

            val json = result.get()
            val parser = Parser()
            val stringBuilder = StringBuilder(json)
            val jsonArr = parser.parse(stringBuilder) as JsonArray<JsonObject>
            val translations = jsonArr["translations"] as JsonArray<JsonObject>
            val textT = translations.string("text")[0]
            editTextTo.setText(textT)
            DBHistory.insertarHistory(History(0, text, textT!!, Factory.user?.id!!))
        }
    }

}
