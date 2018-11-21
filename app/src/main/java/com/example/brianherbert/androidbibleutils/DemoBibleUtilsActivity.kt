package com.example.brianherbert.androidbibleutils

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import com.example.brianherbert.biblenavwatch.data.BOOK
import com.example.brianherbert.biblenavwatch.data.BibleRef
import com.example.brianherbert.biblenavwatch.data.BibleVersion
import com.example.brianherbert.biblenavwatch.data.Verse
import com.example.brianherbert.biblenavwatch.network.VerseFetcher

import kotlinx.android.synthetic.main.activity_demo_bible_utils.*

class DemoBibleUtilsActivity : AppCompatActivity(), VerseFetcher.VerseFetcherListener {
    override fun onVerseReceived(verse: Verse) {
        Log.v("blarg", "got verse " + verse.mText)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo_bible_utils)

        var ref = BibleRef(BibleVersion.ESV, BOOK.ACTS, 1)
        VerseFetcher.fetchText(ref, this)
    }

}
