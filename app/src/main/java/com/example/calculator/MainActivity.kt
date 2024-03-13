package com.example.calculator

import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {

    private lateinit var tvInput: TextView
    private lateinit var tvResult: TextView
    private lateinit var tvResultHex: TextView
    private lateinit var tvResultDec: TextView
    private lateinit var tvResultOct: TextView
    private var currentNumberSystem: NumberSystem = NumberSystem.BINARY
    private var isOperationPerformed: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadCorrectLayout()

        tvInput = findViewById(R.id.tvInput)
        tvResult = findViewById(R.id.tvResult)
        tvResultHex = findViewById(R.id.tvResultHex)
        tvResultDec = findViewById(R.id.tvResultDec)
        tvResultOct = findViewById(R.id.tvResultOct)

        if (currentNumberSystem == NumberSystem.BINARY) {
            setupBinaryCalculatorListeners()
        } else {
            setupHexadecimalCalculatorListeners()
        }
    }

    private fun setupBinaryCalculatorListeners() {
        val btnOne: ImageButton = findViewById(R.id.btnOne)
        val btnZero: ImageButton = findViewById(R.id.btnZero)
        val btnSum: ImageButton = findViewById(R.id.btnSum)
        val btnRes: ImageButton = findViewById(R.id.btnRes)
        val btnMul: ImageButton = findViewById(R.id.btnMul)
        val btnDiv: ImageButton = findViewById(R.id.btnDiv)
        val btnClear: ImageButton = findViewById(R.id.btnClear)
        val btnEquals: ImageButton = findViewById(R.id.btnEquals)

        btnOne.setOnClickListener { appendToInput("1") }
        btnZero.setOnClickListener { appendToInput("0") }
        btnSum.setOnClickListener { appendOperator("+") }
        btnRes.setOnClickListener { appendOperator("-") }
        btnMul.setOnClickListener { appendOperator("*") }
        btnDiv.setOnClickListener { appendOperator("/") }
        btnClear.setOnClickListener { clearInput() }
        btnEquals.setOnClickListener { calculateResult() }
    }

    private fun setupHexadecimalCalculatorListeners() {
        val btnClear: Button = findViewById(R.id.button_clear)
        val btnBack: Button = findViewById(R.id.button_back)
        val btnEquals: Button = findViewById(R.id.button_equals)
        val btnDigits = arrayOf<Button>(
            findViewById(R.id.button_0), findViewById(R.id.button_1),
            findViewById(R.id.button_2), findViewById(R.id.button_3),
            findViewById(R.id.button_4), findViewById(R.id.button_5),
            findViewById(R.id.button_6), findViewById(R.id.button_7),
            findViewById(R.id.button_8), findViewById(R.id.button_9),
            findViewById(R.id.button_a), findViewById(R.id.button_b),
            findViewById(R.id.button_c), findViewById(R.id.button_d),
            findViewById(R.id.button_e), findViewById(R.id.button_f)
        )

        btnClear.setOnClickListener { clearInput() }
        btnBack.setOnClickListener { removeLastCharFromInput() }
        btnEquals.setOnClickListener { calculateResult() }

        btnDigits.forEach { button ->
            button.setOnClickListener {
                appendToInput(button.text.toString())
            }
        }
    }

    private fun appendToInput(str: String) {
        val currentText = tvInput.text.toString()
        tvInput.text = currentText + str
        isOperationPerformed = false
    }

    private fun removeLastCharFromInput() {
        val currentText = tvInput.text.toString()
        if (currentText.isNotEmpty()) {
            tvInput.text = currentText.substring(0, currentText.length - 1)
        }
    }

    private fun appendOperator(operator: String) {
        if (!isOperationPerformed) {
            val currentText = tvInput.text.toString()
            if (currentText.isNotEmpty()) {
                val lastChar = currentText.last()
                if (lastChar.isDigit()) {
                    tvInput.text = currentText + operator
                    isOperationPerformed = true
                }
            }
        }
    }

    private fun clearInput() {
        tvInput.text = ""
        isOperationPerformed = false
    }

    private fun calculateResult() {
        val expression = tvInput.text.toString()

        val parts = expression.split(Regex("(?<=[+\\-*/])|(?=[+\\-*/])"))

        if (parts.size != 3 || !isOperator(parts[1])) {
            tvInput.text = "Error"
            return
        }

        val num1 = parseNumber(parts[0])
        val operator = parts[1]
        val num2 = parseNumber(parts[2])

        if (num1 == null || num2 == null) {
            tvInput.text = "Error"
            return
        }

        val result = when (operator) {
            "+" -> num1 + num2
            "-" -> num1 - num2
            "*" -> num1 * num2
            "/" -> if (num2 != 0) num1 / num2 else null
            else -> null
        }

        if (result != null) {
            val binaryResult = result.toString(2)
            val decimalResult = result.toString()
            val octalResult = Integer.toOctalString(result)
            val message = "Binary: $binaryResult\nDecimal: $decimalResult\nOctal: $octalResult"
            tvInput.text = message
        } else {
            tvInput.text = "Error"
        }
    }

    private fun isOperator(str: String): Boolean {
        return str.matches(Regex("[+\\-*/]"))
    }

    private fun parseNumber(str: String): Int? {
        return try {
            str.toInt(currentNumberSystem.base)
        } catch (e: NumberFormatException) {
            null
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        loadCorrectLayout()
    }

    private fun loadCorrectLayout() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_main_land)
            currentNumberSystem = NumberSystem.HEXADECIMAL
        } else {
            setContentView(R.layout.activity_main)
            currentNumberSystem = NumberSystem.BINARY
        }
    }

    enum class NumberSystem(val base: Int) {
        BINARY(2),
        HEXADECIMAL(16)
    }
}