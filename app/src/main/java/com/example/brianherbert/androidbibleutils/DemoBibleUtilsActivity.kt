package com.example.brianherbert.androidbibleutils

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.example.brianherbert.biblenavwatch.data.BibleRef
import com.example.brianherbert.biblenavwatch.ui.BibleNavSmall
import com.example.utils.bl.BibleFetcher
import com.example.utils.bl.LocalBibleFetcher
import com.example.utils.data.BibleData
import com.example.utils.data.yv.YVPassageResponse
import com.example.utils.bl.YVFetcher
import com.example.utils.data.BiblePassage
import java.io.BufferedReader
import java.io.InputStreamReader


class DemoBibleUtilsActivity : AppCompatActivity(), BibleNavSmall.BibleNavListener, BibleFetcher.BibleFetcherListener {
    val TAG = "Demo Bible"

    lateinit var mLblVerse: TextView
    lateinit var mBibleNav: BibleNavSmall

    lateinit var mBtnNext: Button
    lateinit var mBtnPrev: Button

    lateinit var bibleFetcher: BibleFetcher

    var mBibleRef: BibleRef? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo_bible_utils)

        mBibleNav = findViewById(R.id.bible_nav)
        mBibleNav.setListener(this)

        mLblVerse = findViewById(R.id.lbl_verse)

        //bibleFetcher = YVFetcher(this, this)
        bibleFetcher = LocalBibleFetcher(this, this)

        mBtnNext = findViewById(R.id.btn_next)
        mBtnNext.setOnClickListener { view ->
            var ref = BibleData.nextRef(mBibleRef)
            if (ref != null) {
                onRefSelected(ref)
            }
        }

        mBtnPrev = findViewById(R.id.btn_prev)
        mBtnPrev.setOnClickListener { view ->

            var ref = BibleData.prevRef(mBibleRef)
            if (ref != null) {
                onRefSelected(ref)
            }
        }
    }

    override fun onRefSelected(ref: BibleRef) {
        mBibleRef = ref
        bibleFetcher.getPassage(ref)
    }

    override fun onNavBackPressed() {
        // NOOP
    }

    override fun onFetched(response: BiblePassage?) {
        Log.v(TAG, "got verse " + response?.toString())
        mLblVerse.text = (response?.getHumanRef() + "\n" + response?.getVerseText())
        mBibleNav.reset()
    }
}
