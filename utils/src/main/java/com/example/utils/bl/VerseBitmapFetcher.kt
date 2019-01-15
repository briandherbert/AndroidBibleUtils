package com.example.utils.bl

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.ImageView
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley
import com.example.brianherbert.biblenavwatch.data.BibleRef

class VerseBitmapFetcher(
    val context: Context,
    val listener: VerseBitmapListener,
    val sideSize: Int = 500,
    val baseUrl: String = DEFAULT_URL
) {

    val TAG = "VerseBitmapFetcher"

    //https://broad-westerberg-ekonqh.bespoken.link/image?ref=ROM.1.16&version=111&width=500&height=500
    companion object {
        val DEFAULT_URL = "https://broad-westerberg-ekonqh.bespoken.link/image"

    }

    interface VerseBitmapListener {
        fun onVerseBitmapLoaded(bmp: Bitmap?)
    }

    fun getBitmap(
        ref: BibleRef
    ) {
        Log.v(TAG, "Getting image for $ref")
        if (ref.verse == null) {
            throw IllegalArgumentException("Must specify a verse!")
        }

        val path = baseUrl + "?ref=${ref.usfm()}&version=${ref.version.id}&width=$sideSize&height=$sideSize"
        Log.v(TAG, "fetching $path")
        var request = ImageRequest(
            path,
            Response.Listener<Bitmap> {
                Log.v(TAG, "Got image $ref")
                listener.onVerseBitmapLoaded(it)
            },
            0,
            0,
            ImageView.ScaleType.CENTER_INSIDE,
            Bitmap.Config.RGB_565,
            Response.ErrorListener {
                Log.e(TAG, "error loading image!", it)
                listener.onVerseBitmapLoaded(null) }
        )

        request.setShouldCache(false)
        Volley.newRequestQueue(context).add(request)
    }
}