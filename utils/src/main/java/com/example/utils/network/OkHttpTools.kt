package com.example.utils.network

import android.util.Log
import com.example.brianherbert.biblenavwatch.data.BibleVersion
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * This is a capitulation. I can't get YV's search to work w url params, and Volley doesn't support request
 * params for GET, so here's OKHttp, note that it's an older version, 3.12; beyond that requires including
 * Java 8 in the implementing project
 */
class OkHttpTools {
    companion object {
        fun searchOkhttp(query: String, responseCallback: Callback, version: BibleVersion = BibleVersion.ESV) {
            get(mapOf<String, String>("query" to query, "language_tag" to "eng"), responseCallback)
        }

        operator fun get(params: Map<String, String>, responseCallback: Callback) {
            val client = OkHttpClient().newBuilder()
                .build()

            val httpBuilder = HttpUrl.parse(YVApiTools.SEARCH_ENDPOINT)!!.newBuilder()

            for ((key, value) in params) {
                httpBuilder.addQueryParameter(key, value)
            }

            val builder = Request.Builder().url(httpBuilder.build())
            for ((key, value) in YVApiTools.HEADERS) {
                builder.addHeader(key, value)
            }

            builder.removeHeader("Content-Type")

            client.newCall(builder.build()).enqueue(responseCallback)
        }
    }
}