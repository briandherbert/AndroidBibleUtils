package com.example.brianherbert.biblenavwatch.data

import com.example.utils.data.yv.YVReference
import java.lang.IllegalArgumentException

class BibleRef constructor(
    var version: BibleVersion = BibleVersion.KJV,
    var book: BOOK,
    var chap: Int,
    var verse: Int? = null,
    var verseRangeEnd: Int? = null
) {
    constructor(book: BOOK, chap: Int, verse: Int? = null, endVerse: Int? = null) : this(BibleVersion.ESV, book, chap, verse, endVerse)

    constructor(ref: BibleRef) : this(ref.version, ref.book, ref.chap, ref.verse, ref.verseRangeEnd)

    /**
     * TODO: Check for invalid refs
     * @param refStr - "GEN.1", "GEN.1.2", "GEN.1.2-3", "Genesis 1:2", "Genesis 1:2-3"
     */
    constructor(version: BibleVersion, refStr: String) : this(version, BOOK.GENESIS, 1, null) {
        var usfm = refStr

        // Range
        if (usfm.contains('-')) {
            var rangeParts = usfm.split('-')
            verseRangeEnd = rangeParts[1].trim().toInt()
            usfm = rangeParts[0]
        }

        if (usfm.contains('.')) {
            var parts = usfm.split('.')
            book = BOOK.fromAbbr(parts[0])

            chap = parts[1].toInt()

            if (parts.size > 2) {
                verse = parts[2].toInt()
            }
        } else if (usfm.contains(':')) {    // Try plaintext "Genesis 1:1". HERE BE DRAGONS
            var parts = usfm.split(' ')
            var nameHasSpace = parts.size > 2   // Tricky with spaces "1 Peter"
            var numPartIdx = if (nameHasSpace) 2 else 1

            var nums = parts[numPartIdx].split(':')

            var bookname = parts[0]
            if (nameHasSpace) bookname += " " + parts[1]
            book = BOOK.fromName(bookname)
            chap = nums[0].toInt()
            verse = nums[1].toInt()
        }
    }

    constructor(yvRef: YVReference) : this(BibleVersion.fromId(
        yvRef.version_id.toInt()), BOOK.GENESIS, 1, null) {

        var parts = yvRef.usfm[0].split('.')
        book = BOOK.fromAbbr(parts[0])!!
        chap = parts[1].toInt()

        if (parts.size > 2) {
            verse = parts[2].toInt()
        }

        // TODO: Support YV verse ranges?
    }

    override fun toString(): String {
        return "Version ${version.display} book ${book.display} chap $chap verse $verse"
    }

    public fun addVerseRange(endVerse : Int) {
        verseRangeEnd = endVerse
    }

    public fun toHumanString(): String {
        var str = "${book.display} $chap" + if (verse != null) ":$verse" else ""
        if (verseRangeEnd != null) str += "-$verseRangeEnd"
        return str
    }

    fun usfm(): String {
        var str = "${book.abbr}.$chap" + if (verse != null) ".$verse" else ""
        if (verseRangeEnd != null) str += "-$verseRangeEnd"
        return str
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BibleRef

        if (version != other.version) return false
        if (book != other.book) return false
        if (chap != other.chap) return false
        if (verse != other.verse) return false

        return true
    }

    override fun hashCode(): Int {
        var result = version.hashCode()
        result = 31 * result + book.hashCode()
        result = 31 * result + chap
        result = 31 * result + (verse ?: 0)
        return result
    }
}