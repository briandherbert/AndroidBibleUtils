package com.example.utils.bl

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
        val headers = HashMap<String, String>()
        headers["X-YouVersion-Client"] = "youversion"
        headers["X-YouVersion-App-Platform"] = "web"
        headers["X-YouVersion-App-Version"] = "1"
        headers["User-Agent"] = "Request-Promise"
        headers["Content-Type"] = "application/json"
        headers["Referer"] = "http://yvapi.youversionapi.com"

        return headers
    }
}