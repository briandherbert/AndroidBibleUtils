package com.example.utils.bl

import android.content.Context
import android.os.Handler
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.example.brianherbert.biblenavwatch.data.BibleRef
import com.example.utils.data.BiblePassage
import com.example.utils.data.yv.YVPassageResponseWrapper
import com.example.utils.data.yv.YVSearchResponseWrapper
import com.example.utils.network.OkHttpTools
import com.example.utils.network.YVStringRequest
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import java.io.IOException
import java.nio.charset.Charset


class YVFetcher(context: Context, listener: BibleFetcherListener) : BibleFetcher(context, listener),
    YVVotdFetcher.VotdListener {

    companion object {
        internal val TAG = YVFetcher::class.java.simpleName
    }

    override fun getPassage(ref: BibleRef) {
        var refStr = ref.book.abbr + "." + ref.chap
        var isChapter = ref.verse == null

        if (!isChapter) {
            refStr += ".${ref.verse}"
        }

        if (ref.verseRangeEnd != null) {
            getPassages(ref)
        } else {
            getVerse(ref.version.id, refStr, isChapter)
        }
    }

    private fun getVerse(version: Int, ref: String, isChapter: Boolean = false) {
        Log.v(TAG, "Requesting $ref")
        val queue = Volley.newRequestQueue(context)
        var endpoint = if (isChapter) "chapter" else "verse"
        val url = "https://bible.youversionapi.com/3.1/$endpoint.json?id=$version&reference=$ref&format=text"
        val getRequest = object : YVStringRequest(
            Request.Method.GET, url,
            Response.Listener { response ->
                // response
                var responseWrapper: YVPassageResponseWrapper =
                    Gson().fromJson(response, YVPassageResponseWrapper::class.java)
                listener.onFetched(BiblePassage(responseWrapper.response.data))
            },
            Response.ErrorListener { error ->
                // TODO Auto-generated method stub
                if (error.networkResponse == null) {
                    Log.e(TAG, "error ", error)
                } else {
                    var msg = String(error.networkResponse.data, Charset.forName("UTF-8"))
                    Log.e(TAG, "error bytes: $msg")
                }

                listener.onFetched()
            }
        ) {

        }
        getRequest.setShouldCache(false)
        queue.add(getRequest)
    }

    /**
     * Get the Verse of the Day based on the day of the year, starting at 1
     */
    fun getVotd(day: Int = -1) {
        YVVotdFetcher(context, this, day).getVotd()
    }

    fun search(strs: ArrayList<String>) {
        search(strs.joinToString(separator = " "))
    }

    fun search(query: String) {
        Log.v(TAG, "searching $query")
        var mainHandler = Handler(context.mainLooper)

        var callback = object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.v(TAG, "Failure searching $query", e)
                listener.onFetched(null)
            }

            override fun onResponse(call: Call, response: okhttp3.Response) {

                var content: String? = null

                if (response.body() != null) content = response.body()!!.string()

                var passage: BiblePassage? = null

                if (content != null) {
                    var responseWrapper: YVSearchResponseWrapper =
                        Gson().fromJson(content, YVSearchResponseWrapper::class.java)

                    passage = BiblePassage(responseWrapper.response.data.verses[0])
                }

                mainHandler.post {
                    listener.onFetched(passage)
                }

            }
        }

        OkHttpTools.searchOkhttp(query, callback)
    }

    override fun onGotVotd(passage: BiblePassage?) {
        listener.onFetched(passage)
    }
}