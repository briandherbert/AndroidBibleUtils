package com.example.brianherbert.biblenavwatch.network

import android.util.Log

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.ArrayList

object ScraperUtils {
    val TAG = "ScraperUtils"

    val MAX_PAGE_SRC = 20000

    val IMAGE_URL_MARKER = "img src="
    val REDIRECT_MARKER = "URL="

    val ERROR_RESULT = "XXXERRORXXX"

    /**
     *
     * @param urlStr
     * @return
     */
    fun getPageSourceAsDesktop(urlStr: String, searchFor: String?): String {
        val isSearching = searchFor != null

        var url: URL? = null
        var inputLine = ""

        try {
            url = URL(urlStr)
            val connection = url
                .openConnection() as HttpURLConnection

            connection.requestMethod = "GET"
            connection.connectTimeout = 3000

            // Emulate the normal desktop
            connection
                .setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2"
                )
            val stream = connection.inputStream
            val `in` = BufferedReader(
                InputStreamReader(stream)
            )

            var line: String? = ""
            var numChars = 0
            while (inputLine.length < MAX_PAGE_SRC) {
                line = `in`.readLine()

                if (line == null) {
                    break
                }

                numChars += line.length

                if (!isSearching || line.contains(searchFor!!)) {
                    inputLine += line
                }
            }

            `in`.close()

        } catch (e: Exception) {
            Log.w(TAG, "Error getting final url!", e)
            return ERROR_RESULT
        }

        return inputLine
    }
}