package com.example.utils.network

class YVApiTools {
    companion object {
        val SEARCH_ENDPOINT = "https://search.youversionapi.com/3.1/bible.json"
        val HEADERS = mapOf(
            "X-YouVersion-Client" to "youversion",
            "X-YouVersion-App-Platform" to "web",
            "X-YouVersion-App-Version" to "1",
            "User-Agent" to "Request-Promise",
            "Content-Type" to "application/json",
            "Referer" to "http://yvapi.youversionapi.com"
        )
    }
}