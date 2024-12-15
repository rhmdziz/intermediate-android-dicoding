package academy.bangkit.storyapp.customview

import academy.bangkit.storyapp.R
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns

class EmailEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.editTextStyle
) : androidx.appcompat.widget.AppCompatEditText(context, attrs, defStyleAttr) {

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                val email = charSequence.toString()

                error = if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    context.getString(R.string.email_error)
                } else {
                    null
                }
            }

            override fun afterTextChanged(editable: Editable?) {}
        })
    }
}