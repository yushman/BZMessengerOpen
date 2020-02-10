package ooo.emessi.messenger

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import ooo.emessi.messenger.di.myModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application(){

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            // declare used Android context
            androidContext(this@App)
            // declare modules
            modules(myModule)
        }

    }

    companion object{
        private const val APP_PREFERENCES = "APP_PREFERENCES"
        private lateinit var instance:App
        fun applicationContext() : Context {
            return ooo.emessi.messenger.App.Companion.instance.applicationContext
        }

        fun getSharedPrefs(): SharedPreferences{
            return applicationContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        }

    }

    init {
        ooo.emessi.messenger.App.Companion.instance = this
    }
}