package io.adjoe.sdk.internal

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import java.util.concurrent.ConcurrentSkipListSet

internal class SharedPreferencesRepository private constructor(context: Context) {
    companion object {
        private const val PREF_NAME = "adjoe"
        private const val INSTALLED_APPS_KEY = "installed_apps"
        private const val TOS_APPROVED_KEY = "tos_approved"

        @Volatile
        private var instance: SharedPreferencesRepository? = null

        @JvmStatic
        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance ?: SharedPreferencesRepository(context).also {
                    instance = it
                }
            }
    }

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    operator fun set(key: String, value: Any?) {
        value?.let { prefs[key] = it }
    }

    inline operator fun <reified T : Any> get(key: String, defaultValue: T): T? {
        return prefs[key, defaultValue]
    }

    // Function to get value with Class type for compatibility with Java
    fun <T : Any> get(key: String, clazz: Class<T>, defaultValue: T? = null): T? {
        return try {
            when (clazz) {
                String::class.java -> prefs.getString(key, defaultValue as? String) as T?
                Int::class.java, java.lang.Integer::class.java -> prefs.getInt(
                    key,
                    defaultValue as? Int ?: -1,
                ) as T?

                Boolean::class.java, java.lang.Boolean::class.java -> prefs.getBoolean(
                    key,
                    defaultValue as? Boolean ?: false,
                ) as T?

                Float::class.java, java.lang.Float::class.java -> prefs.getFloat(
                    key,
                    defaultValue as? Float ?: -1f,
                ) as T?

                Long::class.java, java.lang.Long::class.java -> prefs.getLong(
                    key,
                    defaultValue as? Long ?: -1L,
                ) as T?

                Set::class.java, java.util.Set::class.java -> {
                    val stringSet = prefs.getStringSet(key, defaultValue as? Set<String>)
                    if (stringSet != null) {
                        ConcurrentSkipListSet(stringSet) as T?
                    } else {
                        defaultValue
                    }
                }

                else -> throw IllegalArgumentException("Unsupported type: ${clazz.simpleName}")
            }
        } catch (e: ClassCastException) {
            e.printStackTrace()
            defaultValue
        }
    }

    private operator fun SharedPreferences.set(key: String, value: Any) = when (value) {
        is String -> edit { putString(key, value) }
        is Int -> edit { putInt(key, value) }
        is Boolean -> edit { putBoolean(key, value) }
        is Float -> edit { putFloat(key, value) }
        is Long -> edit { putLong(key, value) }
        is Set<*> -> edit {
            if (value.all { it is String }) {
                @Suppress("UNCHECKED_CAST")
                edit { putStringSet(key, value as Set<String>) }
            } else {
                throw IllegalArgumentException("Set contains non-string elements")
            }
        }

        else -> throw IllegalArgumentException("Unsupported type: ${value.javaClass.simpleName}")
    }

    inline operator fun <reified T : Any> SharedPreferences.get(key: String, defaultValue: T?): T? {
        return try {
            when (T::class) {
                String::class -> getString(key, defaultValue as? String) as T?
                Int::class -> getInt(key, defaultValue as? Int ?: -1) as T?
                Boolean::class -> getBoolean(key, defaultValue as? Boolean ?: false) as T?
                Float::class -> getFloat(key, defaultValue as? Float ?: -1f) as T?
                Long::class -> getLong(key, defaultValue as? Long ?: -1) as T?
                Set::class -> {
                    val stringSet =
                        getStringSet(key, defaultValue as? Set<String> ?: setOf<String>())
                    ConcurrentSkipListSet(stringSet) as T?
                }

                else -> throw IllegalArgumentException("Unsupported type: ${T::class.simpleName}")
            }
        } catch (e: ClassCastException) {
            e.printStackTrace()
            defaultValue
        }
    }

    fun getInstalledApps(): String? {
        return get(INSTALLED_APPS_KEY, String::class.java, null)
    }

    fun getTosApproved(): Boolean {
        return get(TOS_APPROVED_KEY, Boolean::class.java, null) ?: false
    }

    fun setInstalledApps(value: String?) {
        set(INSTALLED_APPS_KEY, value)
    }

    fun setTosApproved(value: Boolean?) {
        set(TOS_APPROVED_KEY, value)
    }
}
