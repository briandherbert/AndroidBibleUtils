package com.example.utils.bl

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import com.example.brianherbert.biblenavwatch.data.BibleRef
import com.example.utils.data.yv.YVAudioResponse
import java.lang.Exception
import java.util.*

/**
 * This class uses MediaPlayer!
 */
open class PassagePlayer : YVAudioFetcher.AudioFetcherListener {
    var mp : MediaPlayer? = null
    var mAudioData : YVAudioResponse.YVAudioData? = null
    var mRef: BibleRef? = null
    var mContext: Context
    private var mEndOfVerseTask: TimerTask? = null

    var mRefToUrl : HashMap<BibleRef, YVAudioResponse.YVAudioData> = HashMap()

    val TAG = "PassagePlayer"

    private var mAudioFetcher: YVAudioFetcher

    private var mListener: PassagePlayerListener

    /** Paused still counts as playing */
    private var mIsPlaying = false
    private var mIsVerse = false

    var mIsLogging = false

    interface PassagePlayerListener {
        fun onReadyToPlayPassage(ref: BibleRef)
        fun onStoppedPassage(ref: BibleRef)
    }

    constructor(context: Context, listener: PassagePlayerListener) {
        mAudioFetcher = YVAudioFetcher(context, this)
        mContext = context
        mListener = listener
    }

    fun load(ref: BibleRef) {
        log("loading " + ref)
        mAudioData = null
        mRef = ref
        mIsVerse = ref.verse != null

        if (mRefToUrl.containsKey(ref)) {
            log("Found cached data")
            onAudioDataFetched(mRefToUrl[ref])
        } else {
            mAudioFetcher.getVerse(ref)
        }
    }

    override fun onAudioDataFetched(audioData: YVAudioResponse.YVAudioData?) {
        log("got audio data for " + mRef)
        mAudioData = audioData

        // cache
        if (mRef != null && audioData != null) {
            log("caching data for " + mRef)
            mRefToUrl.put(mRef!!, audioData)
        }
        preparePlayer()
    }

    /**
     * Preps MediaPlayer using local audio data and ref
     * @return success
     */
    private fun preparePlayer(): Boolean {
        try {
            mp = MediaPlayer.create(mContext, Uri.parse(mAudioData?.getStreamingUrl()))
            mp!!.setOnPreparedListener { mListener.onReadyToPlayPassage(mRef!!) }

            log("Successfully prepared player")
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Error getting audio stream", e)
        }

        return false
    }

    fun play() {
        log("Play, is mp playing? ${mp?.isPlaying}")
        if (mp == null && !preparePlayer()) {
            return;
        }

        if (mp?.isPlaying != true) {
            if (mIsVerse) {    // Seek to verse and schedule stopping at end
                var idx = mRef!!.verse!! - 1
                var timing = mAudioData?.timing?.get(idx)
                var start = timing!!.start * 1000
                var end = timing!!.end * 1000

                mEndOfVerseTask = EndOfVerseTask(mp)
                Timer().schedule(mEndOfVerseTask, (end - start).toLong())

                log("seeking to " + start)
                mp?.seekTo(start.toInt())
            }
            mp?.setOnCompletionListener { stop() }
            mp?.start()
            mIsPlaying = true;
        }
    }

    /**
     * Destroys player
     */
    fun stop() {
        log("Stop")

        pause()
        mp?.stop()
        mp?.release()
        mp = null
        if (mIsPlaying) {
            mListener?.onStoppedPassage(mRef!!)
        }
        mIsPlaying = false
    }

    fun pause() {
        log("Pause")

        mEndOfVerseTask?.cancel()
        if (mp?.isPlaying == true) {
            mp?.pause()
        }
    }

    inner class EndOfVerseTask(var player: MediaPlayer?) : TimerTask() {
        override fun run() {
            this@PassagePlayer.stop()
        }
    }
    
    fun log(s: String) {
        if (mIsLogging) log(s)
    }
}