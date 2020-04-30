package ooo.emessi.messenger.managers.account

import android.util.Log.d
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ooo.emessi.messenger.constants.Constants
import ooo.emessi.messenger.ssl.SslApi


class LoginManager {

    private val TAG = this.javaClass.simpleName

    private val token = Constants.DEVICE_ID
    private val apiHost = Constants.SSL_API_HOST
    val ssl = SslApi()
    val httpOk: MutableLiveData<Boolean> = MutableLiveData()

    val login: String = ""
    val pass: String = ""

    var code: MutableLiveData<String?> = MutableLiveData()

    fun getPublicSKey() = CoroutineScope(Dispatchers.IO).launch {
        httpOk.postValue(ssl.setupPublicSKey())
    }


    fun getCode(email: String) = CoroutineScope(Dispatchers.IO).launch{
        val isTokenExist = isTokenExistReq()
        if (!isTokenExist) {
            code.postValue(getCodeReq(email))
            d(TAG, "Token NotExist")
        } else {
            code.postValue(getCodeReq(email))
            d(TAG, "Token Exist")
        }
    }

    fun getCodeReq(email: String): String? {
        val request = ssl.encode("token=$token&mail=$email")
        val json = ssl.httpGet("$apiHost/code$request") ?: return null
        val responseEncoded = JsonParser.parseString(json).asJsonObject["ENC"].asString
        val response = ssl.decode(responseEncoded)
        return JsonParser.parseString(response).asJsonObject["code"].asString
    }

    fun isTokenExistReq(): Boolean {
        val request = ssl.encode("token=$token")
        val json =  ssl.httpGet("$apiHost/exist$request") ?: return false
        val responseEncoded = JsonParser.parseString(json).asJsonObject["ENC"].asString
        val response = ssl.decode(responseEncoded)
        return JsonParser.parseString(response).asJsonObject["exist"].asBoolean
    }

    fun register(email: String, code: String): Pair<String, String> {
        val request = ssl.encode("token=$token&mail=$email&code=$code")
        val json =  ssl.httpGet("$apiHost/register$request") ?: return "" to ""
        val responseEncoded = JsonParser.parseString(json).asJsonObject["ENC"].asString
        val response = ssl.decode(responseEncoded)

        return JsonParser.parseString(response).asJsonObject["login"].asString to JsonParser.parseString(response).asJsonObject["password"].asString
    }

    fun getPublicKey(_ssl: SslApi) = CoroutineScope(Dispatchers.IO).launch{
        ssl.getPublicKey()
    }
}