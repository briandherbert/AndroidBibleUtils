package com.example.utils.bl

import android.util.Log
import com.example.brianherbert.biblenavwatch.data.BibleRef
import com.example.utils.data.BiblePassage
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.collections.LinkedHashSet

/**
 * This is a ghetto way to fetch verses, but the YouVersion endpoint is broken
 * https://docs.thewardro.be/api/docs/3.1/sections/bible/verses.html
 * and the bible app doesn't even use that, it fetches the whole chapter as HTML and parses out the range
 */
class MultiVerseFetcher : BibleFetcher.BibleFetcherListener {
    val TAG = "MultiVerseFetcher"

    val origListener: BibleFetcher.BibleFetcherListener
    var mRef: BibleRef
    var mEndVerse: Int
    var mBibleFetcher: BibleFetcher

    var mContent = ""

    var mVerseQ = LinkedList<BibleRef>()


    constructor(fetcher: BibleFetcher, ref: BibleRef) {
        var endVerse = ref.verseRangeEnd
        if (ref.verse == null || endVerse == null) {
            throw IllegalArgumentException("Must have an end verse!")
        }

        if (ref.verse == null || ref.verse!! > endVerse) {
            throw IllegalArgumentException ("Bad verse range ${ref.verse} - $endVerse")
        }

        mBibleFetcher = fetcher
        origListener = fetcher.listener
        fetcher.listener = this

        mRef = ref
        mEndVerse = endVerse

        for (v in mRef.verse!! .. endVerse) {
            var r = BibleRef(ref)
            r.verse = v
            mVerseQ.add(r)
        }

        var verse = mVerseQ.remove()
        Log.v(TAG, "get next passage $verse")
        mBibleFetcher.getPassage(verse)
    }

    override fun onFetched(response: BiblePassage?) {
        mContent += " " + response?.content

        if (mVerseQ.isEmpty()) {
            origListener.onFetched(BiblePassage(mRef, mContent))
            mBibleFetcher.listener = origListener
        } else {
            var verse = mVerseQ.remove()
            Log.v(TAG, "get next passage $verse")
            mBibleFetcher.getPassage(verse)
        }
    }
}