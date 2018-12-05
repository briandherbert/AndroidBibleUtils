package com.example.brianherbert.biblenavwatch.data

class BibleRef constructor(
    var version: BibleVersion = BibleVersion.ESV,
    var book: BOOK,
    var chap: Int,
    var verse: Int? = null
) {
    constructor(book: BOOK, chap: Int, verse: Int? = null) : this(BibleVersion.ESV, book, chap, verse)

    constructor(ref: BibleRef) : this(ref.version, ref.book, ref.chap, ref.verse)

    override fun toString(): String {
        return "Version ${version.display} book ${book.display} chap $chap verse $verse"
    }

    public fun toHumanString(): String {
        return "${book.display} $chap" + if (verse != null) ".$verse" else ""
    }
}