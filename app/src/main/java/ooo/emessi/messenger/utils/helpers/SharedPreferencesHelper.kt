package ooo.emessi.messenger.utils.helpers

import android.content.Context
import android.content.Context.MODE_PRIVATE
import ooo.emessi.messenger.constants.Constants

class SharedPreferencesHelper(private val context: Context) {

    fun putString(key: String, s: String) {
        getPreferences().edit()
            .putString(key, s)
            .apply()
    }

    fun getString(key: String, defaultValue: String?): String? {
        return getPreferences().getString(key, defaultValue)
    }

    private fun getPreferences() = context.getSharedPreferences(Constants.SHARED_NAME, MODE_PRIVATE)


}