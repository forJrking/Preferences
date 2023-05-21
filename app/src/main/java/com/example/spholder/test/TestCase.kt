package com.example.spholder.test

import com.example.spholder.bo.Game

val testObj = Game(91, "Tank Go", mutableListOf("A-1", "95-A", "B-5"))

interface TestCase {
    var intCase: Int
    var floatCase: Float
    var longCase: Long
    var booleanCase: Boolean
    var stringCase: String
    var objCase: Game
    var setStringCase: Set<String>
    var setObjCase: Set<Game>
    var setStringNullableCase: Set<String>?
    var setObjNullableCase: Set<Game>?
}
