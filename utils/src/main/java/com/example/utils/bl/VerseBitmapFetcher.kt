package com.example.utils.bl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.support.annotation.ColorInt
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

    /**
     * Get artistically styled text of a verse on a transparent bg
     */
    fun getBitmap(
        ref: BibleRef,
        @ColorInt textColor: Int = Color.WHITE
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
                listener.onVerseBitmapLoaded(getVerseTextImage(it, textColor))
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

    fun getVerseTextImage(
        bitmap: Bitmap?,
        colorNew: Int
    ): Bitmap? {
        val lowLimit = 0
        val hiLimit = 50
        if (bitmap != null) {
            val picw = bitmap.width
            val pich = bitmap.height
            val pix = IntArray(picw * pich)
            bitmap.getPixels(pix, 0, picw, 0, 0, picw, pich)
            for (y in 0 until pich) {
                for (x in 0 until picw) {
                    val index = y * picw + x
                    if (Color.red(pix[index]) >= lowLimit && Color.red(pix[index]) <= hiLimit &&
                        Color.green(pix[index]) >= lowLimit && Color.green(pix[index]) <= hiLimit &&
                        Color.blue(pix[index]) >= lowLimit && Color.blue(pix[index]) <= hiLimit
                    ) {
                        pix[index] = Color.TRANSPARENT
                    } else {
                        pix[index] = colorNew
                    }
                }
            }
            return Bitmap.createBitmap(pix, picw, pich, Bitmap.Config.ARGB_8888)
        }
        return null
    }
}