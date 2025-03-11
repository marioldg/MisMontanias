package com.example.examenappkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.examenappkotlin.R

class CalculadoraActivity : AppCompatActivity() {

    private lateinit var tvDisplay: TextView
    private var firstOperand: Int? = null
    private var operator: String? = null
    // Bandera para indicar si se debe iniciar un nuevo número en el display
    private var newOperation: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculadora)

        tvDisplay = findViewById(R.id.tvDisplay)

        // Configuramos los botones numéricos
        val numberButtonIds = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        )
        for (id in numberButtonIds) {
            findViewById<Button>(id).setOnClickListener { btn ->
                val digit = (btn as Button).text.toString()
                if (newOperation || tvDisplay.text.toString() == "0") {
                    tvDisplay.text = digit
                    newOperation = false
                } else {
                    tvDisplay.append(digit)
                }
            }
        }

        // Configuramos los botones de operaciones
        findViewById<Button>(R.id.btnPlus).setOnClickListener { operatorClicked("+") }
        findViewById<Button>(R.id.btnMinus).setOnClickListener { operatorClicked("-") }
        findViewById<Button>(R.id.btnMultiply).setOnClickListener { operatorClicked("x") }
        findViewById<Button>(R.id.btnDivide).setOnClickListener { operatorClicked("/") }

        // Botón de igual: realiza el cálculo
        findViewById<Button>(R.id.btnEqual).setOnClickListener { calculateResult() }

        // Botón INV: invierte el número mostrado
        findViewById<Button>(R.id.btnInv).setOnClickListener { invertNumber() }

        // Botón BORRAR: reinicia la calculadora
        findViewById<Button>(R.id.btnBorrar).setOnClickListener { clearCalculator() }
    }

    // Guarda el primer operando y la operación; no muestra el operador en el display
    private fun operatorClicked(op: String) {
        firstOperand = tvDisplay.text.toString().toIntOrNull()
        operator = op
        newOperation = true
    }

    // Realiza el cálculo con el segundo operando y la operación guardada
    private fun calculateResult() {
        val secondOperand = tvDisplay.text.toString().toIntOrNull()
        if (firstOperand == null || secondOperand == null || operator == null) {
            return
        }
        val result = when (operator) {
            "+" -> firstOperand!! + secondOperand
            "-" -> firstOperand!! - secondOperand
            "x" -> firstOperand!! * secondOperand
            "/" -> if (secondOperand != 0) firstOperand!! / secondOperand else 0
            else -> 0
        }
        tvDisplay.text = result.toString()
        // Reiniciamos la operación para que no se acumulen
        firstOperand = null
        operator = null
        newOperation = true
    }

    // Invierte el número que se muestra (por ejemplo, "456" pasará a ser "654")
    private fun invertNumber() {
        val numberStr = tvDisplay.text.toString()
        tvDisplay.text = numberStr.reversed()
    }

    // Reinicia la calculadora a su estado inicial
    private fun clearCalculator() {
        tvDisplay.text = "0"
        firstOperand = null
        operator = null
        newOperation = true
    }
}
