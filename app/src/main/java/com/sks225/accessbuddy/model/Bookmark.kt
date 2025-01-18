package com.sks225.accessbuddy.model

data class Bookmark(
    val name: String,
    val url: String,
    var image: ByteArray? = null,
    var imagePath: Int? = null
)