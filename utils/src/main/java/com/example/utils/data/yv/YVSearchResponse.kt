package com.example.utils.data.yv

data class YVSearchResponse(val data: YVSearchData) {
    data class YVSearchData(val verses: ArrayList<YVPassageResponse.YVVerseData>)
}