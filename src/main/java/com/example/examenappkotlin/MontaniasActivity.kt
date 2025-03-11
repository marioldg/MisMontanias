package com.example.examenappkotlin

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.examenappkotlin.AnadirMontania

class MontaniasActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var listViewMontanias: ListView
    private lateinit var btnAgregarMontania: Button
    private lateinit var txtNumCimas: TextView
    private var username: String = "invitado" // Se debe obtener del login

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_montanias)

        dbHelper = DatabaseHelper(this)

        listViewMontanias = findViewById(R.id.listaMontanias)
        btnAgregarMontania = findViewById(R.id.btnAgregarMontania)
        txtNumCimas = findViewById(R.id.txtNumCimas)

        // Obtener el usuario logueado (debe venir del LoginActivity)
        username = intent.getStringExtra("USERNAME") ?: "invitado"

        // Si el usuario es admin, mostrar botón "Añadir Montaña"
        if (dbHelper.esAdmin(username)) {
            btnAgregarMontania.visibility = Button.VISIBLE
        }

        btnAgregarMontania.setOnClickListener {
            val intent = Intent(this, AnadirMontania::class.java)
            startActivity(intent)
        }

        cargarMontanias()
    }

    override fun onResume() {
        super.onResume()
        cargarMontanias() // Actualizar la lista al regresar de AnadirMontania
    }

    fun cargarMontanias() {
        val montanias = if (dbHelper.esAdmin(username)) {
            dbHelper.obtenerTodasLasMontanias()
        } else {
            dbHelper.obtenerMontaniasPorUsuario(username)
        }


        // Usar MontaniaAdapter para mostrar las montañas
        val adapter = MontaniaAdapter(
            this,
            montanias,  // Lista de montañas obtenidas
            dbHelper,
            dbHelper.esAdmin(username)// Pasar dbHelper si necesitas usarlo en el adaptador
        )

        listViewMontanias.adapter = adapter

        txtNumCimas.text = "Nº de cimas conquistadas: ${montanias.size}"
    }
}
