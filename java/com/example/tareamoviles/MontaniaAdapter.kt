package com.example.tareamoviles

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.tareamoviles.R

class MontaniaAdapter(private val context: Context, private val montanias: List<Montania>, private val dbHelper: DatabaseHelper) :
    ArrayAdapter<Montania>(context, R.layout.item_montania, montanias) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val view = convertView ?: inflater.inflate(R.layout.item_montania, parent, false)

        val imgMontania = view.findViewById<ImageView>(R.id.imgMontania)
        val txtMontania = view.findViewById<TextView>(R.id.txtMontania)
        val txtConcejo = view.findViewById<TextView>(R.id.txtConcejo)

        val montania = montanias[position]

        // Cargar imagen desde drawable con el nombre de la montaña
        val resId = context.resources.getIdentifier(montania.nombre.lowercase(), "drawable", context.packageName)
        if (resId != 0) {
            imgMontania.setImageResource(resId)
        } else {
            imgMontania.setImageResource(R.drawable.montañaFoto)
        }

        // Mostrar datos
        txtMontania.text = "${montania.nombre} - ${montania.altura}m"
        txtConcejo.text = montania.concejo

        // Configurar clic para menú popup
        view.setOnLongClickListener {
            mostrarPopupMenu(it, montania)
            true
        }

        return view
    }

    private fun mostrarPopupMenu(view: View, montania: Montania) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.menuInflater.inflate(R.menu.menu_montania, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menuModificar -> mostrarDialogoModificarMontania(montania)
                R.id.menuEliminar -> mostrarDialogoEliminarMontania(montania)
            }
            true
        }
        popupMenu.show()
    }

    private fun mostrarDialogoModificarMontania(montania: Montania) {
        val builder = android.app.AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialogo_modificar_montania, null)

        val edtNombreMontania = view.findViewById<EditText>(R.id.edtNombreMontania)
        val edtAlturaMontania = view.findViewById<EditText>(R.id.edtAlturaMontania)
        val edtConcejoMontania = view.findViewById<EditText>(R.id.edtConcejoMontania)
        val btnGuardarModificacion = view.findViewById<Button>(R.id.btnGuardarModificacion)
        val btnCancelarModificacion = view.findViewById<Button>(R.id.btnCancelarModificacion)

        // Setear valores actuales
        edtNombreMontania.setText(montania.nombre)
        edtNombreMontania.isEnabled = false // No se puede modificar el nombre
        edtAlturaMontania.setText(montania.altura.toString())
        edtConcejoMontania.setText(montania.concejo)

        val dialog = builder.setView(view)
            .setTitle("Modificar montaña")
            .create()

        // Guardar cambios
        btnGuardarModificacion.setOnClickListener {
            val nuevaAltura = edtAlturaMontania.text.toString().toIntOrNull()
            val nuevoConcejo = edtConcejoMontania.text.toString()

            if (nuevaAltura != null) {
                dbHelper.modificarMontania(montania.copy(altura = nuevaAltura, concejo = nuevoConcejo))
                (context as? MontaniasActivity)?.cargarMontanias()
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Ingrese una altura válida", Toast.LENGTH_SHORT).show()
            }
        }

        // Cancelar modificación
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
