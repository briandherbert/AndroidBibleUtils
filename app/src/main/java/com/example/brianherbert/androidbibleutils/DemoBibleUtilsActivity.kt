package com.example.brianherbert.androidbibleutils

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.example.brianherbert.biblenavwatch.data.BibleRef
import com.example.brianherbert.biblenavwatch.ui.BibleNavSmall
import com.example.utils.bl.BibleFetcher
import com.example.utils.bl.PassagePlayer
import com.example.utils.bl.YVFetcher
import com.example.utils.data.BibleData
import com.example.utils.data.BiblePassage


// TODO: Audio button states
class DemoBibleUtilsActivity : AppCompatActivity(), BibleNavSmall.BibleNavListener, BibleFetcher.BibleFetcherListener,
    PassagePlayer.PassagePlayerListener {

    val TAG = "Demo Bible"

    lateinit var mLblVerse: TextView
    lateinit var mBibleNav: BibleNavSmall

    lateinit var mBtnNext: Button
    lateinit var mBtnPrev: Button
    lateinit var mBtnAudio: Button

    lateinit var bibleFetcher: BibleFetcher

    var mBibleRef: BibleRef? = null

    var isPlayingAudio = false

    lateinit var mPassagePlayer: PassagePlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo_bible_utils)

        mBibleNav = findViewById(R.id.bible_nav)
        mBibleNav.setListener(this)

        mLblVerse = findViewById(R.id.lbl_verse)

        bibleFetcher = YVFetcher(this, this)

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

        mBtnAudio = findViewById(R.id.btn_audio)
        mBtnAudio.isEnabled = false
        mBtnAudio.setOnClickListener { view ->
            if (!isPlayingAudio) {
                mPassagePlayer.play()
                mBtnAudio.text = "Stop"
            } else {
                mPassagePlayer.pause()
                mBtnAudio.text = "Audio"
            }

            isPlayingAudio = !isPlayingAudio
        }

        mPassagePlayer = PassagePlayer(this, this)
    }

    override fun onRefSelected(ref: BibleRef) {
        mBtnAudio.isEnabled = false
        mBibleRef = ref
        mPassagePlayer!!.stop()
        bibleFetcher.getPassage(ref)
    }

    override fun onNavBackPressed() {
        // NOOP
    }

    override fun onFetched(response: BiblePassage?) {
        Log.v(TAG, "got verse " + response?.toString())
        mLblVerse.text = (response?.getHumanRef() + "\n" + response?.getVerseText())
        mBibleNav.reset()

        mPassagePlayer.load(response!!.ref)
    }

    override fun onReadyToPlay(ref: BibleRef) {
        Log.v("blarg", "ready to play")
        mBtnAudio.isEnabled = true

    }
}
