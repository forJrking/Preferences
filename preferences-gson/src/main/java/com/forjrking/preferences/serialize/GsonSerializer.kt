package com.forjrking.preferences.serialize

import android.content.Context
import androidx.startup.Initializer
import com.forjrking.preferences.PreferencesOwner
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.lang.reflect.Type

class GsonSerializerInitializer : Initializer<Unit> {

    private val gson by lazy { GsonBuilder().serializeNulls().create() }

    override fun create(context: Context) {
        PreferencesOwner.context = context.applicationContext
        PreferencesOwner.serializer = GsonSerializer(gson)
    }

    override fun dependencies() = emptyList<Class<Initializer<*>>>()
}

class GsonSerializer(private val gson: Gson) : Serializer {

    override fun serialize(toSerialize: Any?): String? = gson.toJson(toSerialize)

    override fun deserialize(serialized: String?, type: Type): Any? = try {
        gson.fromJson<Any>(serialized, type)
    } catch (e: Throwable) {
        null
    }
}
