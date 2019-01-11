package com.example.utils.bl

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import com.example.brianherbert.biblenavwatch.data.BibleRef
import com.example.utils.data.yv.YVAudioResponse
import java.lang.Exception
import java.util.*

class PassagePlayer : YVAudioFetcher.AudioFetcherListener {
    var mp : MediaPlayer? = null
    var mAudioData : YVAudioResponse.YVAudioData? = null
    var mLoadedRef: BibleRef? = null
    var mContext: Context
    var mEndOfVerseTask: TimerTask? = null

    var mRefToUrl : HashMap<BibleRef, YVAudioResponse.YVAudioData> = HashMap()

    val TAG = "PassagePlayer"

    var mAudioFetcher: YVAudioFetcher

    var mListener: PassagePlayerListener


    interface PassagePlayerListener {
        fun onReadyToPlay(ref: BibleRef)
    }

    constructor(context: Context, listener: PassagePlayerListener) {
        mAudioFetcher = YVAudioFetcher(context, this)
        mContext = context
        mListener = listener
    }

    fun load(ref: BibleRef) {
        Log.v(TAG, "loading " + ref)
        mLoadedRef = ref
        mAudioFetcher.getVerse(ref)
    }

    fun play() {
        if (mp?.isPlaying != true) {
            if (mLoadedRef?.verse != null) {    // Seek to verse
                var idx = mLoadedRef!!.verse!! - 1
                var timing = mAudioData?.timing?.get(idx)
                var start = timing!!.start * 1000
                var end = timing!!.end * 1000

                mEndOfVerseTask = EndOfVerseTask(mp)
                Timer().schedule(mEndOfVerseTask, (end - start).toLong())

                Log.v(TAG, "seeking to " + start)
                mp?.seekTo(start.toInt())
            }
            mp?.start()
        }
    }

    fun stop() {
        pause()
        mEndOfVerseTask?.cancel()
        mp?.stop()
        mp?.release()
        mp = null
    }

    fun pause() {
        if (mp?.isPlaying == true) {
            mp?.pause()
        }
    }

    override fun onAudioDataFetched(audioData: YVAudioResponse.YVAudioData?) {
        Log.v(TAG, "got audio data for " + mLoadedRef)
        mAudioData = audioData

        stop()

        var url = audioData?.getStreamingUrl()
        try {
            mRefToUrl.put(mLoadedRef!!, audioData!!)
            mp = MediaPlayer.create(mContext, Uri.parse(url))
            mp!!.setOnPreparedListener { mListener.onReadyToPlay(mLoadedRef!!) }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting audio stream", e)
        }
    }

    internal class EndOfVerseTask(var player: MediaPlayer?) : TimerTask() {
        override fun run() {
            player?.stop()
        }

    }
}