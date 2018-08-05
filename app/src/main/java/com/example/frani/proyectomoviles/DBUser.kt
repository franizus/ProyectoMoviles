package com.example.frani.proyectomoviles

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBUser {
    companion object {
        val DB_NAME = "user"
        val TABLE_NAME = "users"
        val CAMPO_ID = "id"
        val CAMPO_NOMBRE = "nombre"
        val CAMPO_APELLIDO = "apellido"
        val CAMPO_EMAIL = "email"
    }
}

class DBAutorHandlerAplicacion(context: Context) : SQLiteOpenHelper(context, DBUser.DB_NAME, null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {

        val createTableSQL = "CREATE TABLE ${DBUser.TABLE_NAME} (${DBUser.CAMPO_ID} INTEGER PRIMARY KEY, ${DBUser.CAMPO_NOMBRE} VARCHAR(50),${DBUser.CAMPO_APELLIDO} VARCHAR(50),${DBUser.CAMPO_EMAIL} VARCHAR(50))"
        db?.execSQL(createTableSQL)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun insertarUser(user: User) {
        val dbWriteable = writableDatabase
        val cv = ContentValues()

        cv.put(DBUser.CAMPO_NOMBRE, user.nombre)
        cv.put(DBUser.CAMPO_APELLIDO, user.apellido)
        cv.put(DBUser.CAMPO_EMAIL, user.email)

        val resultado = dbWriteable.insert(DBUser.TABLE_NAME, null, cv)

        dbWriteable.close()
    }

    fun deleteUser(id: Int): Boolean {
        val dbWriteable = writableDatabase
        val whereClause = "${DBUser.CAMPO_ID} = $id"
        return dbWriteable.delete(DBUser.TABLE_NAME, whereClause, null) > 0
    }

    fun getUser(): User? {
        val dbReadable = readableDatabase
        val query = "SELECT * FROM ${DBUser.TABLE_NAME}"
        val resultado = dbReadable.rawQuery(query, null)
        var user: User? = null

        if (resultado.moveToFirst()) {
            do {
                val id = resultado.getString(0).toInt()
                val nombre = resultado.getString(1)
                val apellido = resultado.getString(2)
                val email = resultado.getString(3)

                user = User(id, nombre, apellido, email)
            } while (resultado.moveToNext())
        }

        resultado.close()
        dbReadable.close()

        return user
    }

}