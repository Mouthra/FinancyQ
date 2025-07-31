package com.example.financyq.ui.customView

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import java.text.NumberFormat
import java.util.Locale

class EditTextRupiah @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    init {
        addTextChangedListener(object : TextWatcher {
            private var current = ""
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != current) {
                    this@EditTextRupiah.removeTextChangedListener(this)

                    val locale = Locale("in", "ID")
                    val currencyFormat = NumberFormat.getCurrencyInstance(locale)
                    currencyFormat.maximumFractionDigits = 0
                    val cleanString = s.toString().replace("[Rp.]".toRegex(), "")

                    val parsed = cleanString.toDoubleOrNull()
                    val formatted = if (parsed != null) currencyFormat.format(parsed) else ""

                    current = formatted
                    this@EditTextRupiah.setText(formatted)
                    this@EditTextRupiah.setSelection(formatted.length)

                    this@EditTextRupiah.addTextChangedListener(this)
                }
            }
        })
    }
}
