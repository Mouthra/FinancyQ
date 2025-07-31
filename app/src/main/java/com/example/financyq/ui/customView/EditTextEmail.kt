package com.example.financyq.ui.customView

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.example.financyq.R

class EditTextEmail @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
                val inputText = s.toString()
                val isValidEmail = inputText.matches(emailPattern.toRegex())
                error = if (!isValidEmail) {
                    val errorMessage = context.getString(R.string.invalid_email_format)
                    errorMessage
                } else {
                    null
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }
}