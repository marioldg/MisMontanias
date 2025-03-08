package com.example.tareamoviles

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class AnadirMontania : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var edtNombre: EditText
    private lateinit var edtAltura: EditText
    private lateinit var spinnerUsuario: Spinner
    private lateinit var btnGuardar: Button
    private lateinit var btnCancelar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anadir_montania)

        dbHelper = DatabaseHelper(this)

        edtNombre = findViewById(R.id.edtNombreMontania)
        edtAltura = findViewById(R.id.edtAlturaMontania)
        spinnerUsuario = findViewById(R.id.spinnerUsuario)
        btnGuardar = findViewById(R.id.btnGuardarMontania)
        btnCancelar = findViewById(R.id.btnCancelarMontania)

        // Cargar usuarios en el Spinner
        val usuarios = arrayOf("invitado", "tu_usuario")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, usuarios)
        spinnerUsuario.adapter = adapter

        btnGuardar.setOnClickListener {
            guardarMontania()
        }

        btnCancelar.setOnClickListener {
            finish() // Cierra la Activity sin guardar
        }
    }

    private fun guardarMontania() {
        val nombre = edtNombre.text.toString().trim()
        val altura = edtAltura.text.toString().toIntOrNull()
        val usuario = spinnerUsuario.selectedItem.toString()

        if (nombre.isEmpty() || altura == null) {
            Toast.makeText(this, "Por favor, ingrese todos los datos", Toast.LENGTH_SHORT).show()
            return
        }

        val montania = Montania(nombre = nombre, usuario = usuario, altura = altura, concejo = "Desconocido")
        val guardado = dbHelper.guardarMontania(montania)

        if (guardado) {
            Toast.makeText(this, "Montaña añadida correctamente", Toast.LENGTH_SHORT).show()
            finish() // Cierra la Activity y regresa a la anterior
        } else {
            Toast.makeText(this, "Error al añadir montaña. ¿Ya existe?", Toast.LENGTH_SHORT).show()
        }
    }
}
