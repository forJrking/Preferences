package com.example.spholder.bo

/**
 * @description:
 * @author: 岛主
 * @date: 2020/7/21 20:19
 * @version: 1.0.0
 */
data class Game(
    var numberId: Int,
    val path: String?,
    val datas: List<String>? = null
)