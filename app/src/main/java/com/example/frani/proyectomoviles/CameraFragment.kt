package com.example.frani.proyectomoviles

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import kotlinx.android.synthetic.main.fragment_camera.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CameraFragment : Fragment() {

    val languageMap: MutableMap<String, String> = mutableMapOf()
    var directorioActualImagen = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var rootView = inflater.inflate(R.layout.fragment_camera, container, false)
        loadSpinner()

        var btn1 = rootView.findViewById<ImageView>(R.id.imgViewCamera)
        btn1.setOnClickListener{
            editTextFromCamera.text.clear()
            tomarFoto()
        }

        var btn2 = rootView.findViewById<Button>(R.id.btnTranslateCamera)
        btn2.setOnClickListener{
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editTextFromCamera.windowToken, 0)
            translate(editTextFromCamera.text.toString())
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
            spinnerToCamera.adapter = adapterSpinner
        }
    }

    fun translate(text: String) {
        val selectedItem = spinnerToCamera.selectedItem.toString()
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
            editTextToCamera.setText(textT)
            DBHistory.insertarHistory(History(0, text, textT!!, Factory.user?.id!!))
        }
    }

    private fun tomarFoto() {
        val archivoImagen = crearArchivo("JPEG_", Environment.DIRECTORY_PICTURES, ".jpg")
        directorioActualImagen = archivoImagen.absolutePath
        enviarIntentFoto(archivoImagen)
    }

    private fun crearArchivo(prefijo: String, directorio: String, extension: String): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = prefijo + timeStamp + "_"
        val storageDir = activity?.getExternalFilesDir(directorio)
        return File.createTempFile(
                imageFileName, /* prefix */
                extension, /* suffix */
                storageDir      /* directory */
        )
    }

    private fun enviarIntentFoto(archivo: File) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoURI: Uri = FileProvider.getUriForFile(
                activity!!.applicationContext,
                "com.example.frani.proyectomoviles.fileprovider",
                archivo)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        if (takePictureIntent.resolveActivity(activity?.packageManager) != null) {
            startActivityForResult(takePictureIntent, TOMAR_FOTO_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            TOMAR_FOTO_REQUEST -> {
                val foto = File(directorioActualImagen)
                val fotoBitmap = BitmapFactory.decodeFile(foto.getAbsolutePath())
                imgViewCamera.setImageBitmap(fotoBitmap)
                obtenerInfoCodigoBarras(fotoBitmap)
            }
        }
    }

    fun obtenerInfoCodigoBarras(bitmap: Bitmap) {
        val image = FirebaseVisionImage.fromBitmap(bitmap)
        val detector = FirebaseVision.getInstance().visionTextDetector
        var text = ""
        val result = detector.detectInImage(image)
                .addOnSuccessListener { firebaseVisionText ->
                    for (block in firebaseVisionText.blocks) {
                        val bounds = block.getBoundingBox()
                        val corners = block.getCornerPoints()
                        text += block.getText()
                    }
                    editTextFromCamera.setText(text)
                }
                .addOnFailureListener {
                    Log.i("info", "------- No reconocio nada")
                }
    }

    companion object {
        val TOMAR_FOTO_REQUEST = 1
    }

}