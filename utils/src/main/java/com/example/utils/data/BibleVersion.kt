package com.example.brianherbert.biblenavwatch.data

enum class BibleVersion(val id: Int, val display: String) {
    NIV(111, "NIV"),
    ESV(59, "ESV"),
    KJV(1, "KJV");

    companion object {
        fun fromId(id: Int): BibleVersion {
            for (version in BibleVersion.values()) {
                if (version.id == id) {
                    return version
                }
            }

            return KJV
        }
    }
}