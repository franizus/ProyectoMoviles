package com.example.frani.proyectomoviles

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import kotlinx.android.synthetic.main.fragment_voice.*
import java.util.*

class VoiceFragment : Fragment() {

    val languageMap: MutableMap<String, String> = mutableMapOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var rootView = inflater.inflate(R.layout.fragment_voice, container, false)

        FuelManager.instance.basePath = "https://api.cognitive.microsofttranslator.com"

        loadSpinner()

        var btn1 = rootView.findViewById<ImageView>(R.id.imgViewVoice)
        btn1.setOnClickListener{
            getSpeechInput()
        }

        var btn2 = rootView.findViewById<Button>(R.id.btnTranslateVoice)
        btn2.setOnClickListener{
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editTextFromVoice.windowToken, 0)
            translate(editTextFromVoice.text.toString())
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
            spinnerToVoice.adapter = adapterSpinner
        }
    }

    fun translate(text: String) {
        val selectedItem = spinnerToVoice.selectedItem.toString()
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
            editTextToVoice.setText(textT)
            DBHistory.insertarHistory(History(0, text, textT!!, Factory.user?.id!!))
        }
    }

    fun getSpeechInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

        if (intent.resolveActivity(activity?.packageManager) != null) {
            startActivityForResult(intent, 10)
        } else {
            Toast.makeText(activity, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            10 -> if (resultCode == Activity.RESULT_OK && data != null) {
                val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                editTextFromVoice.setText(result[0])
            }
        }
    }

}