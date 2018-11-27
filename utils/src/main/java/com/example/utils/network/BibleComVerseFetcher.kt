package com.example.brianherbert.biblenavwatch.network

import android.os.AsyncTask
import android.util.Log
import com.example.brianherbert.biblenavwatch.data.BibleRef

import com.example.brianherbert.biblenavwatch.data.Verse

class BibleComVerseFetcher private constructor(
    internal var mRef: BibleRef,
    internal var mListener: VerseFetcherListener
) : AsyncTask<Void, Void, String>() {

    interface VerseFetcherListener {
        fun onVerseReceived(verse: Verse)
    }

    override fun doInBackground(vararg params: Void): String {
        var url = "https://www.bible.com/en-GB/bible/111/${mRef.book.abbr}.${mRef.chap}"
        if (mRef.verse != null) {
            url += "." + mRef.verse
        }

        log("fetching url $url with ref ${mRef.toString()}")

        var verse = ""
        var raw = ScraperUtils.getPageSourceAsDesktop(url, START_KEY)
        var endIdx = raw.indexOf(END_KEY)

        if (endIdx > 0) {
            raw = raw.substring(START_KEY.length, endIdx - 1)
            val versePretty = raw.substring(0, raw.indexOf(';'))
            verse = raw.substring(versePretty.length + 1)
        }

        // TODO: Figure out what to do w the pretty version

        return verse
    }

    override fun onPostExecute(text: String) {
        mListener.onVerseReceived(Verse(mRef, text))
        return
    }

    companion object {
        internal val TAG = BibleComVerseFetcher::class.java.simpleName

        internal val START_KEY = "<title data-react-helmet=\"true\">"
        internal val END_KEY = "</title>"

        fun fetchText(verseRef: BibleRef, listener: VerseFetcherListener) {
            BibleComVerseFetcher(verseRef, listener).execute()
        }

        internal fun log(s: String) {
            Log.v(TAG, s)
        }
    }
}
