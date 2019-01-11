package com.example.utils.data.yv

data class YVAudioResponse(
    val data: Array<YVAudioData>
) {
    data class YVAudioData(
        private val download_urls: YVAudioLinks,
        val timing: Array<YVAudioTimeBlock>
    ) {
        fun getStreamingUrl(): String {
            return "https:" + download_urls?.format_mp3_32k
        }
    }

    data class YVAudioLinks(val format_mp3_32k: String)

    data class YVAudioTimeBlock(
        val start: Double,
        val end: Double,
        val usfm: String
    )
}