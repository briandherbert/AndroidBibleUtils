package com.example.utils.bl

import android.content.Context
import android.util.Log
import com.example.brianherbert.biblenavwatch.data.BibleRef
import com.example.utils.data.BiblePassage
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception

class LocalBibleFetcher(context: Context, listener: BibleFetcherListener) : BibleFetcher(context, listener) {
    val TAG = "LocalBibleFetcher"
    var mCurrentRef: BibleRef? = null

    var mVerseMap: HashMap<Int, String> = HashMap()

    override fun getPassage(ref: BibleRef) {
        var isChap = ref.verse == null

        // Short circuit from cache if verse in same book + chap
        if (!isChap && ref.book == mCurrentRef?.book && ref.chap == mCurrentRef?.chap) {
            Log.v(TAG, "getting verse from cache")
            listener.onFetched(BiblePassage(ref, mVerseMap[ref.verse]!!))
            return;
        }

        mVerseMap.clear()
        mCurrentRef = ref

        // Fetch the whole chap
        var passage = ""
        val path = "bibles/${ref.version.name}/${ref.book.abbr}/${ref.chap}.txt"
        Log.v(TAG, "fetch " + path)

        var text = ""
        var br : BufferedReader? = null

        // Open chap file
        try {
            var str = context.assets.open(path)
            br = BufferedReader(InputStreamReader(str)!!)
            text = br.use { it.readText() }
        } catch (e : Exception) {
            var msg= "Error getting " + ref.toHumanString() + ". Ensure your assets has a Bible in format 'bibles/ESV/GEN/1.txt'"
            Log.e(TAG, msg)

            listener.onFetched(BiblePassage(ref, msg))
            return
        }

        // Cache verses in map
        for (verse in text.split('#')) {
            try {
                var spaceIdx = verse.indexOf(' ')
                var verseNum = verse.subSequence(0, spaceIdx).toString().toInt()
                var verseText = verse.substring(spaceIdx + 1)
                mVerseMap.put(verseNum, verseText)

                if (verseNum == ref.verse) {
                    passage = verseText
                }
            } catch (e : Exception) {
                continue
            }
        }

        if (isChap) {
            passage = text.replace("#", "")
        }

        listener.onFetched(BiblePassage(ref, passage))
        br?.close()
    }
}