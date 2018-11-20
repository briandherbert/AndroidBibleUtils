package com.example.brianherbert.biblenavwatch.data

import com.example.utils.TestingJitpack

class BibleRef(var version: BibleVersion, var book: BOOK, var chap: Int, var verse: Int? = null) {
    override fun toString(): String {
        return "Version ${version.display} book ${book.display} chap $chap verse $verse"
    }
}