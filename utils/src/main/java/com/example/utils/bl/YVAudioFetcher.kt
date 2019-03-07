package com.example.utils.bl

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.example.brianherbert.biblenavwatch.data.BibleRef
import com.example.utils.data.yv.YVAudioResponse
import com.example.utils.data.yv.YVAudioResponseWrapper
import com.example.utils.network.YVStringRequest
import com.google.gson.Gson
import java.nio.charset.Charset

class YVAudioFetcher(val context: Context, val listener: AudioFetcherListener) {
    companion object {
        internal val TAG = YVAudioFetcher::class.java.simpleName
    }

    interface AudioFetcherListener {
        fun onAudioDataFetched(audioData: YVAudioResponse.YVAudioData? = null)
    }

    var data: YVAudioResponse.YVAudioData? = null
    var bibleRef: BibleRef? = null

    fun getVerse(ref: BibleRef) {
        var bookAbbr = ref.book.abbr
        var chap = ref.chap

        // See if we have it cached
        if (bookAbbr.equals(bibleRef?.book?.abbr) && ref.chap == bibleRef?.chap) {
            listener.onAudioDataFetched(data)
            return
        }

        bibleRef = ref

        Log.v(TAG, "Requesting $bookAbbr")
        val queue = Volley.newRequestQueue(context)
        val url = "http://audio-bible.youversionapistaging.com/3.1/chapter.json?version_id=${ref.version.id}&reference=$bookAbbr.$chap"
        val getRequest = object : YVStringRequest(
            Request.Method.GET, url,
            Response.Listener { response ->
                // response
                var responseWrapper: YVAudioResponseWrapper =
                    Gson().fromJson(response, YVAudioResponseWrapper::class.java)

                data = responseWrapper.response.data?.get(0)

                // Url doesn't have "https:"
                listener.onAudioDataFetched(data)
            },
            Response.ErrorListener { error ->
                // TODO Auto-generated method stub
                if (error.networkResponse == null) {
                    Log.e(TAG, "error ", error)
                } else {
                    var msg = String(error.networkResponse.data, Charset.forName("UTF-8"))
                    Log.e(TAG, "error bytes: $msg")
                }

                listener.onAudioDataFetched()
            }
        ) {

        }
        getRequest.setShouldCache(false)
        queue.add(getRequest)
    }
}