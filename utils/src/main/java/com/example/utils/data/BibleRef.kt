package com.example.brianherbert.biblenavwatch.data

import com.example.utils.data.yv.YVPassageResponse
import com.example.utils.data.yv.YVReference

class BibleRef constructor(
    var version: BibleVersion = BibleVersion.ESV,
    var book: BOOK,
    var chap: Int,
    var verse: Int? = null
) {
    constructor(book: BOOK, chap: Int, verse: Int? = null) : this(BibleVersion.ESV, book, chap, verse)

    constructor(ref: BibleRef) : this(ref.version, ref.book, ref.chap, ref.verse)

    constructor(version: BibleVersion, usfm: String) : this(version, BOOK.GENESIS, 1, null) {
        var parts = usfm.split('.')
        book = BOOK.fromAbbr(parts[0])
        chap = parts[1].toInt()

        if (parts.size > 2) {
            verse = parts[2].toInt()
        }
    }

    constructor(yvRef: YVReference) : this(BibleVersion.fromId(
        yvRef.version_id.toInt()), BOOK.GENESIS, 1, null) {

        var parts = yvRef.usfm[0].split('.')
        book = BOOK.fromAbbr(parts[0])
        chap = parts[1].toInt()

        if (parts.size > 2) {
            verse = parts[2].toInt()
        }
    }

    override fun toString(): String {
        return "Version ${version.display} book ${book.display} chap $chap verse $verse"
    }

    public fun toHumanString(): String {
        return "${book.display} $chap" + if (verse != null) ":$verse" else ""
    }

    fun usfm(): String {
        return "${book.name}.$chap" + if (verse != null) ".$verse" else ""
    }
}