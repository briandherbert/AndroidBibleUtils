package com.example.brianherbert.biblenavwatch.data

class BibleRef constructor(var version: BibleVersion = BibleVersion.ESV, var book: BOOK, var chap: Int, var verse: Int? = null) {
    override fun toString(): String {
        return "Version ${version.display} book ${book.display} chap $chap verse $verse"
    }

    public fun toHumanString(): String {
        return "${book.display} $chap" + if (verse != null) ".$verse" else ""
    }
}