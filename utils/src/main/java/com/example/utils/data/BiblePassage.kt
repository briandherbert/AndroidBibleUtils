package com.example.utils.data

import com.example.brianherbert.biblenavwatch.data.BibleRef
import com.example.utils.data.yv.YVPassageResponse

class BiblePassage(val ref: BibleRef, val content: String) {
    constructor(yvData: YVPassageResponse.YVVerseData) : this(BibleRef(yvData.reference), yvData.content)

    fun getVerseText(): String {
        return content
    }

    fun getHumanRef(): String {
        return ref.toHumanString()
    }

    fun getUsfm(): String {
        return ref.usfm()
    }
}
