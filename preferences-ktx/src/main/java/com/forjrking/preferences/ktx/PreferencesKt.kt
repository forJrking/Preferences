package com.forjrking.preferences.ktx

import androidx.lifecycle.MutableLiveData
import com.forjrking.preferences.PreferencesOwner
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun <V> ReadWriteProperty<PreferencesOwner, V>.asLiveData() =
    object : ReadOnlyProperty<PreferencesOwner, MutableLiveData<V>> {
        private var cache: MutableLiveData<V>? = null

        override fun getValue(thisRef: PreferencesOwner, property: KProperty<*>) =
            cache ?: object : MutableLiveData<V>() {
                override fun getValue() = this@asLiveData.getValue(thisRef, property)

                override fun setValue(value: V) {
                    this@asLiveData.setValue(thisRef, property, value)
                    super.setValue(value)
                }

                override fun onActive() = super.setValue(value)
            }.also { cache = it }
    }
