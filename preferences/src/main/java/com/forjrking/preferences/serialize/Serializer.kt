package com.forjrking.preferences.serialize

import java.lang.reflect.Type

interface Serializer {

    fun serialize(toSerialize: Any?): String?

    fun deserialize(serialized: String?, type: Type): Any?
}
