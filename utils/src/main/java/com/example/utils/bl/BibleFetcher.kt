package com.example.utils.bl

import android.content.Context
import com.example.brianherbert.biblenavwatch.data.BibleRef
import com.example.utils.data.BiblePassage

abstract class BibleFetcher(val context: Context, val listener: BibleFetcherListener) {
    interface BibleFetcherListener {
        fun onFetched(response: BiblePassage? = null)
    }

    abstract fun getPassage(ref: BibleRef)
}