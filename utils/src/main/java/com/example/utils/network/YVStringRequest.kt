package com.example.utils.network

import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest

abstract class YVStringRequest(
    method: Int,
    url: String?,
    listener: Response.Listener<String>?,
    errorListener: Response.ErrorListener?
) : StringRequest(method, url, listener, errorListener) {
    @Throws(AuthFailureError::class)
    override fun getHeaders(): Map<String, String> {
        return YVApiTools.HEADERS
    }

    override fun getParams(): MutableMap<String, String> {
        Log.v("blarg", "get params")
        return super.getParams()
    }
}