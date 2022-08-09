package sk.backbone.parent.repositories.shared_preferences

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import sk.backbone.parent.utils.jsonToObject
import sk.backbone.parent.utils.toJsonString
import javax.inject.Inject

abstract class SharedPreferencesDataProvider {
    @Inject @ApplicationContext lateinit var context: Context

    abstract val sharedPreferencesKey: String

    protected val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(sharedPreferencesKey, Context.MODE_PRIVATE)
    }

    protected fun storeValue(key: String, value: Any?){
        with(sharedPreferences.edit()){
            putString(key, value?.toJsonString())
            commit()
        }
    }

    protected inline fun <reified T>getValue(key: String) : T?{
        return sharedPreferences.getString(key, null)?.jsonToObject()
    }
}