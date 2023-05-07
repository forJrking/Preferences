package com.forjrking.preferences.serialize

import com.google.gson.Gson
import java.lang.reflect.Type

class GsonSerializer(val gson: Gson) : Serializer {

    override fun serialize(toSerialize: Any?): String? = gson.toJson(toSerialize)

    override fun deserialize(serialized: String?, type: Type): Any? = try {
        gson.fromJson<Any>(serialized, type)
    } catch (e: Throwable) {
        e.printStackTrace()
//        throw RuntimeException("Error in parsing to $type. The string to parse: \"$this\"", e)
        null
    }
}
