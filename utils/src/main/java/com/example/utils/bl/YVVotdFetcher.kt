package com.example.utils.bl

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.example.brianherbert.biblenavwatch.data.BibleRef
import com.example.brianherbert.biblenavwatch.data.BibleVersion
import com.example.utils.data.BiblePassage
import com.example.utils.data.yv.YVAudioResponseWrapper
import com.example.utils.data.yv.YVVotdResponse
import com.example.utils.data.yv.YVVotdResponseWrapper
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.nio.charset.Charset
import java.util.*

/**
 * @see com.example.utils.bl.YVFetcher#getVotd(day : Int)
 * Get verse of the day
 * @param day - Day of the year STARTING AT 1
 */
class YVVotdFetcher(val context: Context, val listener: VotdListener, var day: Int = -1) : BibleFetcher.BibleFetcherListener {
    interface VotdListener {
        fun onGotVotd(passage: BiblePassage?)
    }

    companion object {
        val TAG = "YVVotdFetcher"
        val PREFS_KEY = "votds"
    }

    fun getVotd() {
        // See if we have this year cached
        var year = Calendar.getInstance().get(Calendar.YEAR)
        Log.v(TAG, "year is " + year)

        var raw = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE).getString(PREFS_KEY, null)
        if (raw == null) {
            fetchVotd()
        } else {
            onGotJson(raw)
        }
    }

    private fun fetchVotd() {
        val queue = Volley.newRequestQueue(context)
        val url = "https://bible.youversionapi.com/3.1/verse_of_the_day.json"
        val getRequest = object : YVStringRequest(
            Request.Method.GET, url,
            Response.Listener { response ->
                // response
                Log.v(TAG, "response is $response")
                onGotJson(response)
            },
            Response.ErrorListener { error ->
                // TODO Auto-generated method stub
                if (error.networkResponse == null) {
                    Log.e(YVAudioFetcher.TAG, "error ", error)
                } else {
                    var msg = String(error.networkResponse.data, Charset.forName("UTF-8"))
                    Log.e(YVAudioFetcher.TAG, "error bytes: $msg")
                }
            }
        ) {

        }
        getRequest.setShouldCache(false)
        queue.add(getRequest)
    }

    fun onGotJson(json : String?) {
        var response = GsonBuilder().create().fromJson(json, YVVotdResponseWrapper::class.java).response
        var refs = response.data

        if (day == -1) {
            day = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        }

        day--
        var refStr = refs[day].references[0]

        var ref = refFromStr(refStr)
        Log.v(TAG, "ref is $ref")
        var yvFetcher = YVFetcher(context, this)
        MultiVerseFetcher(yvFetcher, ref)
    }

    fun refFromStr(refStr : String) : BibleRef {
        var refs = refStr.split('+')
        var ref = BibleRef(BibleVersion.ESV, refs[0])
        if (refs.size > 1) {
            var lastRef = refs[refs.size - 1]
            var lastVerse = lastRef.substringAfterLast('.').toInt()
            Log.v(TAG, "last verse $lastVerse")
            ref.verseRangeEnd = lastVerse
        }

        return ref
    }

    override fun onFetched(response: BiblePassage?) {
        Log.v(TAG, "Got passage " + response)
        listener?.onGotVotd(response)
    }
}