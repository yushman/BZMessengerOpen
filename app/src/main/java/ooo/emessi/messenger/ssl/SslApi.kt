package ooo.emessi.messenger.ssl

import android.util.Log.d
import com.google.gson.JsonParser
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.internal.commonToUtf8String
import ooo.emessi.messenger.constants.Constants
import ooo.emessi.messenger.utils.decodeHex
import ooo.emessi.messenger.utils.encodeStringHex
import org.bouncycastle.util.encoders.Base64
import org.whispersystems.curve25519.Curve25519
import org.whispersystems.curve25519.Curve25519KeyPair
import java.io.IOException
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class SslApi {

    private val APP_PREFERENCES = "SslApi"
    private val PUBSKEY = "PUBSKEY"
    private val PUBCKEY = "PUBCKEY"
    private val PRIVCKEY = "PRIVCKEY"
    private val SHKEY = "SHKEY"

//    private val pref: SharedPreferences

    private var curve25519: Curve25519? = null
    private var privateKey: ByteArray? = null
    private var publicKey: ByteArray? = null
    private var sharedKey: ByteArray? = null
    private var publicServerKey: ByteArray? = null
    private var kp: Curve25519KeyPair? = null

    private var cipher: Cipher? = null

    init {
//        pref = context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
//        initValues()
    }

    fun encode(url: String): String{
        val skb = SecretKeySpec(getSharedKey(),"AES")
        val ivEncBytes = ByteArray(16)
        SecureRandom().nextBytes(ivEncBytes)
        getCipher().init(Cipher.ENCRYPT_MODE, skb, IvParameterSpec(ivEncBytes))
        val ctText = cipher!!.doFinal(url.toByteArray())
        val comReq = (getPublicKey().encodeStringHex() + ctText.encodeStringHex()).decodeHex()
        d("SSL",comReq.encodeStringHex() + " size " + comReq.size)
        val req64 = Base64.toBase64String(comReq)
        d("SSL", req64)
        val sb = StringBuilder()
        for (i in req64.indices){
            val ch = req64[i]
            if (ch.toByte() in (0x41..0x5A) || ch.toByte() in 0x30..0x39 || ch.toByte() in 0x61..0x7A)
                sb.append(ch)
            else sb.append("%" + ch.toByte().toString(16).toUpperCase())
        }
        val reqStr = sb.toString()
        d("SSL", reqStr)
        return "?ENC=$reqStr"
    }

    fun decode(s: String): String{
        val skb = SecretKeySpec(getSharedKey(),"AES")
        val res = Base64.decode(s)
        d("SSL", res.encodeStringHex())
        val ivBytes = res.sliceArray(0..15)
        d("SSL", ivBytes.encodeStringHex())
        val text = res.sliceArray(16 until res.size)
        d("SSL", text.encodeStringHex())
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, skb, IvParameterSpec(ivBytes))
        val result = cipher.doFinal(text).commonToUtf8String()
        d("SSL", result)
        return result
    }

    private fun getCipher(): Cipher {
        if (cipher == null) cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        return cipher!!
    }

    private fun getSharedKey(): ByteArray {
        if (sharedKey == null) {
//            val sharedStr = pref.getString(SHKEY,"")
//            if (sharedStr == null || sharedStr.isEmpty()){
                createSharedKey()
//            } else sharedKey = sharedStr.decodeHex()
        }
        return sharedKey!!
    }

    private fun getPrivateKey(): ByteArray {
        if (privateKey == null) privateKey = getKeyPair().privateKey
        return privateKey!!
    }

    fun getPublicKey(): ByteArray {
        if (publicKey == null) publicKey = getKeyPair().publicKey
        return publicKey!!
    }

    private fun getKeyPair(): Curve25519KeyPair {
        if (kp == null) {
            kp = generateKeys()
//            val privateStr = pref.getString(PRIVCKEY, "")
//            val publicStr = pref.getString(PUBCKEY, "")
//            if (privateStr == null || privateStr.isEmpty() || publicStr == null || publicStr.isEmpty()){
//                createKeyPair()
//            } else {
//                privateKey = privateStr.decodeHex()
//                publicKey = publicStr.decodeHex()
//            }
        }
        return kp!!
    }

    private fun initValues() {
        cipher = getCipher()
        privateKey = getPrivateKey()
        publicKey = getPublicKey()
        sharedKey = getSharedKey()

    }

    fun httpGet(url: String): String? {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()
        var _response: String? = null
        try {
            client.newCall(request).execute().use {response ->
                d("HTTP", "HTTP call ${request.body}")
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                _response =  response.body!!.string()
                d("HTTP", _response ?: "null")
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return _response
    }

    private fun createKeyPair() {

//        pref.edit()
//            .putString(PRIVCKEY, privateKey!!.encodeStringHex())
//            .putString(PUBCKEY, publicKey!!.encodeStringHex())
//            .apply()
    }

    private fun createSharedKey() {
        sharedKey = getSharedSecret(publicServerKey!!, getPrivateKey())
//        pref.edit()
//            .putString(SHKEY, sharedKey!!.encodeStringHex())
//            .apply()
    }

    private fun getCipher25519(): Curve25519{
        if (curve25519 == null) curve25519 = Curve25519.getInstance(Curve25519.BEST)
        return curve25519!!
    }

    fun setupPublicSKey(): Boolean {
        publicServerKey = getPublicSKey()
        return publicServerKey != null
    }

    private fun getPublicSKey(): ByteArray? {
        if (publicServerKey == null) {
            val json = httpGet(Constants.SSL_API_PUBLIC_HOST)
            if (json == null) {
                return null
            }
            d("SSL", json)

            val data = JsonParser.parseString(json)
            val pubSStr = data.asJsonObject["key"].asString// .asJsonObject["key"].asString
//            val pubSPStr = pref.getString(PUBSKEY, "")
//            if (pubSPStr == null || pubSStr != pubSPStr){
                publicServerKey = pubSStr.decodeHex()
//                createSharedKey()
//                pref.edit()
//                    .putString(PUBSKEY, pubSStr)
//                    .apply()
//            }
        }

        return publicServerKey!!
    }

    private fun generateKeys() = getCipher25519().generateKeyPair()

    private fun getSharedSecret(publicKey: ByteArray, privateKey: ByteArray) = getCipher25519().calculateAgreement(publicKey, privateKey)


}