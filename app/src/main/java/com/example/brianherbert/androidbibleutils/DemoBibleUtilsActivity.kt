package com.example.brianherbert.androidbibleutils

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.brianherbert.biblenavwatch.data.BOOK
import com.example.brianherbert.biblenavwatch.data.BibleRef
import com.example.brianherbert.biblenavwatch.data.BibleVersion
import com.example.brianherbert.biblenavwatch.ui.BibleNavSmall
import com.example.utils.bl.BibleFetcher
import com.example.utils.bl.PassagePlayer
import com.example.utils.bl.VerseBitmapFetcher
import com.example.utils.bl.YVFetcher
import com.example.utils.data.BibleData
import com.example.utils.data.BiblePassage


// TODO: Audio button states
class DemoBibleUtilsActivity : AppCompatActivity(), BibleNavSmall.BibleNavListener, BibleFetcher.BibleFetcherListener,
    PassagePlayer.PassagePlayerListener, VerseBitmapFetcher.VerseBitmapListener {

    val TAG = "DemoBibleUtilsActivity"

    lateinit var mLblVerse: TextView
    lateinit var mBibleNav: BibleNavSmall

    lateinit var mBtnNext: Button
    lateinit var mBtnPrev: Button
    lateinit var mBtnAudio: Button
    lateinit var mBtnBmp: Button
    lateinit var mImg: ImageView

    lateinit var mBibleFetcher: BibleFetcher
    lateinit var mVerseBmpFetcher: VerseBitmapFetcher

    var mBibleRef: BibleRef? = null

    var mIsPlayingAudio = false

    lateinit var mPassagePlayer: PassagePlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo_bible_utils)

        mBibleNav = findViewById(R.id.bible_nav)
        mBibleNav.setListener(this)

        mLblVerse = findViewById(R.id.lbl_verse)

        mBibleFetcher = YVFetcher(this, this)
        mVerseBmpFetcher = VerseBitmapFetcher(this, this)

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
            if (!mIsPlayingAudio) {
                mPassagePlayer.play()
                mBtnAudio.text = "Stop"
            } else {
                mPassagePlayer.pause()
                mBtnAudio.text = "Audio"
            }

            mIsPlayingAudio = !mIsPlayingAudio
        }

        mBtnBmp = findViewById(R.id.btn_image)
        mBtnBmp.setOnClickListener {
            if (mBibleRef?.verse != null) {
                mVerseBmpFetcher.getBitmap(mBibleRef!!, Color.RED)
            }
        }

        mImg = findViewById(R.id.img_verse)
        mImg.setLayerType(View.LAYER_TYPE_SOFTWARE, null)

        mImg.setOnClickListener { mImg.visibility = View.GONE }

        mPassagePlayer = PassagePlayer(this, this)

        mBibleFetcher.getPassage("GEN.1.1", BibleVersion.ESV)
    }

    override fun onRefSelected(ref: BibleRef) {
        mBtnAudio.isEnabled = false
        mBibleRef = ref
        mPassagePlayer!!.stop()
        mBibleFetcher.getPassage(ref)
    }

    override fun onNavBackPressed() {
        // NOOP
    }

    override fun onFetched(response: BiblePassage?) {
        Log.v(TAG, "got verse " + response?.toString())

        if (response == null) {
            Toast.makeText(this, "No verse, got internet?", Toast.LENGTH_LONG).show()
            mLblVerse.text = "No verse, got internet?"
            return
        }

        mLblVerse.text = (response?.getHumanRef() + "\n" + response?.getVerseText())
        mBibleNav.reset()

        mPassagePlayer.load(response!!.ref)
    }

    override fun onReadyToPlayPassage(ref: BibleRef) {
        Log.v(TAG, "ready to play")
        mBtnAudio.isEnabled = true
    }

    override fun onStoppedPassage(ref: BibleRef) {
        Log.v(TAG, "playing stopped")
        runOnUiThread {
            mIsPlayingAudio = false
            mBtnAudio.isEnabled = true
            mBtnAudio.text = "Audio"
        }
    }

    override fun onVerseBitmapLoaded(bmp: Bitmap?) {
        if (bmp == null) {
            Log.e(TAG, "Error getting bmp")
            return
        }

        Log.v(TAG, "got bitmap size ${bmp.width}, ${bmp.height}")

        mImg.visibility = View.VISIBLE
        mImg.setImageBitmap(bmp)
    }

}
