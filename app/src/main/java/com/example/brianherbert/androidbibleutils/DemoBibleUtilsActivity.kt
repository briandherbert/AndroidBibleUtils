package com.example.brianherbert.androidbibleutils

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import com.example.brianherbert.biblenavwatch.data.BibleRef
import com.example.brianherbert.biblenavwatch.ui.BibleNavSmall
import com.example.utils.data.yv.YVPassageResponse
import com.example.utils.network.YVFetcher


class DemoBibleUtilsActivity : AppCompatActivity(), BibleNavSmall.BibleNavListener, YVFetcher.YVFetcherListener {
    lateinit var mLblVerse: TextView
    lateinit var mBibleNav: BibleNavSmall

    lateinit var mYVFetcher: YVFetcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo_bible_utils)

        mBibleNav = findViewById(R.id.bible_nav)
        mBibleNav.setListener(this)

        mLblVerse = findViewById(R.id.lbl_verse)

        mYVFetcher = YVFetcher(this, this)
    }

    override fun onRefSelected(ref: BibleRef) {
        mYVFetcher.getPassage(ref)

    }

    override fun onNavBackPressed() {
        // NOOP
    }

    override fun onFetched(response: YVPassageResponse?) {
        Log.v("blarg", "got verse " + response?.toString())
        mLblVerse.text = (response?.getVerseText() + "\n" + response?.getHumanRef())
        mBibleNav.reset()
    }
}
