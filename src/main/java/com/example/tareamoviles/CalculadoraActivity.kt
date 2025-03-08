package com.example.tareamoviles

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CalculadoraActivity : AppCompatActivity() {

    private lateinit var txtDisplay: TextView
    private var operador: String? = null
    private var primerNumero: Int? = null
    private var segundoNumero: Int? = null
    private var nuevoNumero = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculadora)

        txtDisplay = findViewById(R.id.txtDisplay)

        val botonesNumericos = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        )

        for (id in botonesNumericos) {
            findViewById<Button>(id).setOnClickListener { agregarNumero((it as Button).text.toString()) }
        }

        findViewById<Button>(R.id.btnSuma).setOnClickListener { setOperador("+") }
        findViewById<Button>(R.id.btnResta).setOnClickListener { setOperador("-") }
        findViewById<Button>(R.id.btnMultiplicacion).setOnClickListener { setOperador("*") }
        findViewById<Button>(R.id.btnDivision).setOnClickListener { setOperador("/") }

        findViewById<Button>(R.id.btnIgual).setOnClickListener { calcularResultado() }
        findViewById<Button>(R.id.btnBorrar).setOnClickListener { reiniciarCalculadora() }
        findViewById<Button>(R.id.btnInv).setOnClickListener { invertirNumero() }
    }

    private fun agregarNumero(numero: String) {
        if (nuevoNumero) {
            txtDisplay.text = numero
            nuevoNumero = false
        } else {
            txtDisplay.append(numero)
        }
    }

    private fun setOperador(op: String) {
        if (txtDisplay.text.isNotEmpty()) {
            primerNumero = txtDisplay.text.toString().toInt()
            operador = op
            nuevoNumero = true
        }
    }

    private fun calcularResultado() {
        if (operador != null && primerNumero != null && txtDisplay.text.isNotEmpty()) {
            segundoNumero = txtDisplay.text.toString().toInt()
            val resultado = when (operador) {
                "+" -> primerNumero!! + segundoNumero!!
                "-" -> primerNumero!! - segundoNumero!!
                "*" -> primerNumero!! * segundoNumero!!
                "/" -> if (segundoNumero != 0) primerNumero!! / segundoNumero!! else 0
                else -> 0
            }
            txtDisplay.text = resultado.toString()
            primerNumero = resultado
            operador = null
            nuevoNumero = true
        }
    }

    private fun invertirNumero() {
        if (txtDisplay.text.isNotEmpty()) {
            txtDisplay.text = txtDisplay.text.toString().reversed()
        }
    }

    private fun reiniciarCalculadora() {
        txtDisplay.text = "0"
        operador = null
        primerNumero = null
        segundoNumero = null
        nuevoNumero = true
    }
}
