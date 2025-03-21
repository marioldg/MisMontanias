package com.example.examenappkotlin

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// Clase Montania (sin el campo concejo)
data class Montania(
    var id: Int = 0,
    var nombre: String,
    var usuario: String,
    var altura: Int
)

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        // Crear tabla de usuarios
        val createUserTable = "CREATE TABLE $TABLE_USERS (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_USERNAME TEXT UNIQUE, " +
                "$COLUMN_PASSWORD TEXT)"
        db.execSQL(createUserTable)

        // Crear tabla de montanias (sin el campo concejo)
        val createMontaniaTable = "CREATE TABLE $TABLE_MONTANIAS (" +
                "$COLUMN_MONTANIA_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_NOMBRE TEXT UNIQUE, " +
                "$COLUMN_ALTURA INTEGER)"
        db.execSQL(createMontaniaTable)

        // Crear tabla de inscripciones
        val createInscripcionTable = "CREATE TABLE $TABLE_INSCRIPCIONES (" +
                "$COLUMN_INSCRIPCION_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_USERNAME TEXT, " +
                "$COLUMN_MONTANIA_ID INTEGER, " +
                "FOREIGN KEY ($COLUMN_USERNAME) REFERENCES $TABLE_USERS($COLUMN_USERNAME), " +
                "FOREIGN KEY ($COLUMN_MONTANIA_ID) REFERENCES $TABLE_MONTANIAS($COLUMN_MONTANIA_ID))"
        db.execSQL(createInscripcionTable)

        // Insertar usuarios por defecto
        insertUser(db, "admin", "admin")
        insertUser(db, "invitado", "guess")
        insertUser(db, "mario", "pass")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MONTANIAS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_INSCRIPCIONES")
        onCreate(db)
    }

    private fun insertUser(db: SQLiteDatabase, username: String, password: String) {
        val values = ContentValues()
        values.put(COLUMN_USERNAME, username)
        values.put(COLUMN_PASSWORD, password)
        db.insert(TABLE_USERS, null, values)
    }

    fun esAdmin(username: String): Boolean {
        return username == "admin"
    }

    fun validateUser(username: String, password: String): Boolean {
        val db = readableDatabase
        val query = "SELECT COUNT(*) FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?"
        val cursor = db.rawQuery(query, arrayOf(username, password))
        var existe = false

        if (cursor.moveToFirst()) {
            existe = cursor.getInt(0) > 0
        }

        cursor.close()
        db.close()

        return existe
    }

    fun guardarMontania(montania: Montania): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NOMBRE, montania.nombre)
        values.put(COLUMN_ALTURA, montania.altura)
        val result = db.insert(TABLE_MONTANIAS, null, values)
        db.close()
        return result != -1L
    }

    fun asociarMontaniaAUsuario(montania: Montania, usuario: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()

        // Obtenemos el ID de la montaña que ya debe estar en la base de datos
        val cursor = db.rawQuery(
            "SELECT $COLUMN_MONTANIA_ID FROM $TABLE_MONTANIAS WHERE $COLUMN_NOMBRE = ?",
            arrayOf(montania.nombre)
        )

        if (cursor.moveToFirst()) {
            val montaniaId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MONTANIA_ID))
            values.put(COLUMN_USERNAME, usuario)
            values.put(COLUMN_MONTANIA_ID, montaniaId)

            val result = db.insert(TABLE_INSCRIPCIONES, null, values)
            cursor.close()
            db.close()
            return result != -1L
        }

        cursor.close()
        db.close()
        return false
    }

    fun modificarMontania(montania: Montania): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_ALTURA, montania.altura)
        val result = db.update(TABLE_MONTANIAS, values, "$COLUMN_NOMBRE = ?", arrayOf(montania.nombre))
        db.close()
        return result > 0
    }

    fun eliminarMontania(nombre: String): Boolean {
        val db = this.writableDatabase
        val result = db.delete(TABLE_MONTANIAS, "$COLUMN_NOMBRE = ?", arrayOf(nombre))
        db.close()
        return result > 0
    }

    fun obtenerTodasLasMontanias(): List<Montania> {
        val montanias = mutableListOf<Montania>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_MONTANIAS", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val nombre = cursor.getString(1)
                val altura = cursor.getInt(2)
                montanias.add(Montania(id, nombre, "admin", altura))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return montanias
    }

    fun obtenerMontaniasPorUsuario(username: String): List<Montania> {
        val montanias = mutableListOf<Montania>()
        val db = this.readableDatabase
        val query = "SELECT m.$COLUMN_MONTANIA_ID, m.$COLUMN_NOMBRE, m.$COLUMN_ALTURA " +
                "FROM $TABLE_MONTANIAS m " +
                "INNER JOIN $TABLE_INSCRIPCIONES i ON m.$COLUMN_MONTANIA_ID = i.$COLUMN_MONTANIA_ID " +
                "WHERE i.$COLUMN_USERNAME = ?"
        val cursor = db.rawQuery(query, arrayOf(username))

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val nombre = cursor.getString(1)
                val altura = cursor.getInt(2)
                montanias.add(Montania(id, nombre, username, altura))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return montanias
    }

    companion object {
        private const val DATABASE_NAME = "montanias.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_USERS = "usuarios"
        private const val TABLE_MONTANIAS = "montanias"
        private const val TABLE_INSCRIPCIONES = "inscripciones"
        private const val COLUMN_ID = "id"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_MONTANIA_ID = "montania_id"
        private const val COLUMN_NOMBRE = "nombre"
        private const val COLUMN_ALTURA = "altura"
        private const val COLUMN_INSCRIPCION_ID = "inscripcion_id"
    }
}
