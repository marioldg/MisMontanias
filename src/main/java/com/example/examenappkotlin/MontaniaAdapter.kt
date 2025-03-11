package com.example.examenappkotlin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class MontaniaAdapter(
    private val context: Context,
    private val montanias: List<Montania>,
    private val dbHelper: DatabaseHelper,
    private val esAdmin: Boolean
) : ArrayAdapter<Montania>(context, R.layout.item_montania, montanias) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val view = convertView ?: inflater.inflate(R.layout.item_montania, parent, false)

        val imgMontania = view.findViewById<ImageView>(R.id.imgMontania)
        val txtMontania = view.findViewById<TextView>(R.id.txtMontania)

        val montania = montanias[position]

        // Cargar imagen desde drawable usando el nombre de la montaña
        val resId = context.resources.getIdentifier(montania.nombre.lowercase(), "drawable", context.packageName)
        if (resId != 0) {
            imgMontania.setImageResource(resId)
        } else {
            imgMontania.setImageResource(R.drawable.naranjo_de_bulnes)
        }

        // Mostrar datos: nombre y altura
        txtMontania.text = "${montania.nombre} - ${montania.altura}m"

        // Mostrar menú popup al hacer clic
        view.setOnClickListener {
            mostrarPopupMenu(it, montania)
            true
        }

        return view
    }

    private fun mostrarPopupMenu(view: View, montania: Montania) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.menuInflater.inflate(R.menu.menu_montania, popupMenu.menu)
        // Para usuarios no admin, se elimina la opción de eliminar
        if (!esAdmin) {
            popupMenu.menu.removeItem(R.id.menuEliminar)
        }
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menuModificar -> {
                    mostrarDialogoModificarMontania(montania)
                    true
                }
                R.id.menuEliminar -> {
                    // Solo se ejecuta si es admin
                    if (esAdmin) {
                        mostrarDialogoEliminarMontania(montania)
                        true
                    } else {
                        false
                    }
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun mostrarDialogoModificarMontania(montania: Montania) {
        val builder = android.app.AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialogo_modificar_montania, null)

        val edtNombreMontania = view.findViewById<EditText>(R.id.edtNombreMontania)
        val edtAlturaMontania = view.findViewById<EditText>(R.id.edtAlturaMontania)
        val btnGuardarModificacion = view.findViewById<Button>(R.id.btnGuardarModificacion)
        val btnCancelarModificacion = view.findViewById<Button>(R.id.btnCancelarModificacion)

        // Se muestra el nombre pero no es editable; solo se permite modificar la altura
        edtNombreMontania.setText(montania.nombre)
        edtNombreMontania.isEnabled = false
        edtAlturaMontania.setText(montania.altura.toString())

        val dialog = builder.setView(view)
            .setTitle("Modificar montaña")
            .create()

        btnGuardarModificacion.setOnClickListener {
            val nuevaAltura = edtAlturaMontania.text.toString().toIntOrNull()
            if (nuevaAltura != null) {
                dbHelper.modificarMontania(montania.copy(altura = nuevaAltura))
                (context as? MontaniasActivity)?.cargarMontanias()
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Ingrese una altura válida", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancelarModificacion.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun mostrarDialogoEliminarMontania(montania: Montania) {
        val builder = android.app.AlertDialog.Builder(context)
        builder.setTitle("Eliminar montaña")
            .setMessage("¿Estás seguro de que quieres eliminar la montaña?")
            .setPositiveButton("Sí") { _, _ ->
                dbHelper.eliminarMontania(montania.nombre)
                (context as? MontaniasActivity)?.cargarMontanias()
            }
            .setNegativeButton("No", null)
            .create()
            .show()
    }
}
