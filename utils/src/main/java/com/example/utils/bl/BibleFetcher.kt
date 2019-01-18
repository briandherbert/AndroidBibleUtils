package com.example.utils.bl

import android.content.Context
import com.example.brianherbert.biblenavwatch.data.BibleRef
import com.example.brianherbert.biblenavwatch.data.BibleVersion
import com.example.utils.data.BiblePassage
import kotlin.IllegalArgumentException

abstract class BibleFetcher(val context: Context, var listener: BibleFetcherListener) {
    interface BibleFetcherListener {
        fun onFetched(response: BiblePassage? = null)
    }

    abstract fun getPassage(ref: BibleRef)

    /**
     * TODO This is a really ghetto way to fetch multiple verses
     *
     */
    fun getPassages(ref: BibleRef) {
        if (ref.verse == null || ref.verseRangeEnd == null) {
            throw Exception("Must have an end verse!")
        }

        MultiVerseFetcher(this, ref)
    }

    /**
     * Get a verse formatted as a human string like "Genesis 1:1" or a range like "Genesis 1:1-3"
     */
    fun getPassage(refStr: String, version: BibleVersion) {
        var ref = BibleRef(version, refStr)

        if (ref.verseRangeEnd == null) {
            getPassage(ref)
        } else {
            getPassages(ref)
        }
    }
}