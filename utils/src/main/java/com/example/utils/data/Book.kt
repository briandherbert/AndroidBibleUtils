package com.example.brianherbert.biblenavwatch.data

public enum class BOOK {
    ACTS("Acts", "ACT"),
    AMOS("Amos", "AMO"),
    ONE_CHRONICLES("1 Chronicles", "1CH"),
    TWO_CHRONICLES("2 Chronicles", "2CH"),
    COLOSSIANS("Colossians", "COL"),
    ONE_CORINTHIANS("1 Corinthians", "1CO"),
    TWO_CORINTHIANS("2 Corinthians", "2CO"),
    DANIEL("Daniel", "DAN"),
    DEUTERONOMY("Deuteronomy", "DEU"),
    ECCLESIASTES("Ecclesiastes", "ECC"),
    EPHESIANS("Ephesians", "EPH"),
    ESTHER("Esther", "EST"),
    EXODUS("Exodus", "EXO"),
    EZEKIEL("Ezekiel", "EZK"),
    EZRA("EZRA", "EZR"),
    GALATIANS("Galatians", "GAL"),
    GENESIS("Genesis", "GEN"),
    HABAKKUK("Habakkuk", "HAB"),
    HAGGAI("Haggai", "HAG"),
    HEBREWS("Hebrews", "HEB"),
    HOSEA("Hosea", "HOS"),
    ISAIAH("Isaiah", "ISA"),
    JAMES("James", "JAS"),
    JEREMIAH("Jeremiah", "JER"),
    JOB("Job", "JOB"),
    JOEL("Joel", "JOL"),
    JOHN("John", "JHN"),
    JOHNS_CATEGORY("John (1,2,3)"),
    ONE_JOHN("1 John", "1JN"),
    TWO_JOHN("2 John", "2JN"),
    THREE_JOHN("3 John", "3JN"),
    JONAH("Jonah", "JON"),
    JOSHUA("Joshua", "JOS"),
    JUDE("Jude", "JUD"),
    JUDGES("Judges", "JDG"),
    ONE_KINGS("1 Kings", "1KI"),
    TWO_KINGS("2 Kings", "2KI"),
    LAMENTATIONS("Lamentations", "LAM"),
    LEVITICUS("Leviticus", "LEV"),
    LUKE("Luke", "LUK"),
    MALACHI("Malachi", "MAL"),
    MARK("Mark", "MRK"),
    MATTHEW("Matthew", "MAT"),
    MICAH("Micah", "MIC"),
    NAHUM("Nahum", "NAM"),
    NEHEMIAH("Nehemiah", "NEH"),
    NUMBERS("Numbers", "NUM"),
    OBADIAH("Obadiah", "OBA"),
    ONE_PETER("1 Peter", "1PE"),
    TWO_PETER("2 Peter", "2PE"),
    PHILEMON("Philemon", "PHM"),
    PHILIPPIANS("Philippians", "PHP"),
    PROVERBS("Proverbs", "PRO"),
    PSALMS("Psalms", "PSA"),
    REVELATION("Revelation", "REV"),
    ROMANS("Romans", "ROM"),
    RUTH("Ruth", "RUT"),
    ONE_SAMUEL("1 Samuel", "1SA"),
    TWO_SAMUEL("2 Samuel", "2SA"),
    SONG_OF_SOLOMON("Song of Solomon", "SNG"),
    ONE_THESSALONIANS("1 Thessalonians", "1TH"),
    TWO_THESSALONIANS("2 Thessalonians", "2TH"),
    ONE_TIMOTHY("1 Timothy", "1TI"),
    TWO_TIMOTHY("2 Timothy", "2TI"),
    TITUS("Titus", "TIT"),
    ZECHARIAH("Zechariah", "ZEC"),
    ZEPHANIAH("Zephaniah", "ZEP");

    var display: String? = null
    var abbr: String? = null
    constructor(display: String, abbr: String? = null) {
        this.display = display

        if (display == null) {
            this.display = name
        }

        if (abbr != null) {
            this.abbr = abbr
        }
    }

    companion object {
        fun fromAbbr(abbr: String): BOOK {
            for (book in BOOK.values()) {
                if (book.abbr.equals(abbr, true)) {
                    return book
                }
            }

            return GENESIS
        }

        fun fromName(name: String): BOOK {
            for (book in BOOK.values()) {
                if (book.display.equals(name, true)) {
                    return book
                }
            }

            return JUDE
        }
    }
}