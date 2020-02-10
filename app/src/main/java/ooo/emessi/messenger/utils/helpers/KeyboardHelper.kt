package ooo.emessi.messenger.utils.helpers

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

object KeyboardHelper {

    fun showKeyboard(et: EditText, ctx: Context){
        et.requestFocus()
        val imm = ctx.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
    }

    fun hideKeyboard(et: EditText, ctx: Context){
        et.clearFocus()
        val imm = ctx.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(et.windowToken, 0)
    }
}