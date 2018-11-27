package com.example.utils.data.yv

data class YVVerseResponse(
    val data: YVVerseData,
    val code: Int
) {

    data class YVVerseData(
        val content: String,
        val reference: YVReference
    ) {

    }

    fun getVerseText(): String {
        return data.content
    }

    fun getHumanRef(): String {
        return data.reference.human
    }

    fun getUsfm(): String {
        return data.reference.usfm[0]
    }
}