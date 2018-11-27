package com.example.utils.data.yv

/**
 * This is silly, but the YV API response starts with a top-level "response" field
 */
data class YVPassageResponseWrapper(val response: YVPassageResponse) {
    fun getVerseText(): String {
        return response.data.content
    }

    fun getHumanRef(): String {
        return response.data.reference.human
    }

    fun getUsfm(): String {
        return response.data.reference.usfm[0]
    }
}