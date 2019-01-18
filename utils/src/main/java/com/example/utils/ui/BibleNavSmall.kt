package com.example.brianherbert.biblenavwatch.ui

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.example.brianherbert.biblenavwatch.data.*
import com.example.brianherbert.biblenavwatch.network.BibleComVerseFetcher
import com.example.utils.R
import com.example.utils.data.BibleData

class BibleNavSmall : LinearLayout, OnClickListener, BibleComVerseFetcher.VerseFetcherListener {
    override fun onVerseReceived(verse: Verse) {
        Log.v(TAG, "Got verse " + verse.mText)
    }

    val TAG = "BibleNavSmall"

    enum class Screen {
        LETTER_PICK,
        BOOK_PICK,
        VERSE_PICK
    }

    // must go to Preferences->Code Style->General->Formatter Control and check Enable formatter markers in comments for this to work.
    // @formatter:off
    enum class MENU_LETTER(val books: Array<BOOK>) {
        A(arrayOf(BOOK.ACTS, BOOK.AMOS)),
        C(arrayOf(BOOK.ONE_CHRONICLES, BOOK.TWO_CHRONICLES, BOOK.COLOSSIANS, BOOK.ONE_CORINTHIANS, BOOK.TWO_CORINTHIANS)),
        D(arrayOf(BOOK.DANIEL, BOOK.DEUTERONOMY)),
        E(arrayOf(BOOK.ECCLESIASTES, BOOK.EPHESIANS, BOOK.ESTHER, BOOK.EXODUS, BOOK.EZEKIEL, BOOK.EZRA)),
        G(arrayOf(BOOK.GALATIANS, BOOK.GENESIS)),
        H(arrayOf(BOOK.HABAKKUK, BOOK.HAGGAI, BOOK.HEBREWS, BOOK.HOSEA)),
        I_O(arrayOf(BOOK.ISAIAH, BOOK.OBADIAH)),
        Jo(arrayOf(BOOK.JOB, BOOK.JOEL, BOOK.JOHNS_CATEGORY, BOOK.JONAH, BOOK.JOSHUA)),
        J(arrayOf(BOOK.JAMES, BOOK.JEREMIAH, BOOK.JUDE, BOOK.JUDGES)),
        K(arrayOf(BOOK.ONE_KINGS, BOOK.TWO_KINGS)),
        L(arrayOf(BOOK.LAMENTATIONS, BOOK.LEVITICUS, BOOK.LUKE)),
        M(arrayOf(BOOK.MALACHI, BOOK.MARK, BOOK.MATTHEW, BOOK.MICAH)),
        N(arrayOf(BOOK.NAHUM, BOOK.NEHEMIAH, BOOK.NUMBERS)),
        Ph(arrayOf(BOOK.PHILEMON, BOOK.PHILIPPIANS)),
        P(arrayOf(BOOK.ONE_PETER, BOOK.TWO_PETER, BOOK.PROVERBS, BOOK.PSALMS)),
        R(arrayOf(BOOK.REVELATION, BOOK.ROMANS, BOOK.RUTH)),
        S(arrayOf(BOOK.ONE_SAMUEL, BOOK.TWO_SAMUEL, BOOK.SONG_OF_SOLOMON)),
        T(arrayOf(BOOK.ONE_THESSALONIANS, BOOK.TWO_THESSALONIANS, BOOK.ONE_TIMOTHY, BOOK.TWO_TIMOTHY, BOOK.TITUS)),
        Z(arrayOf(BOOK.ZECHARIAH, BOOK.ZEPHANIAH));
    }
    // @formatter:on

    interface BibleNavListener {
        fun onRefSelected(ref: BibleRef)
        fun onNavBackPressed()
    }

    var mListener: BibleNavListener? = null

    val BOOKS_JOHN = arrayListOf(BOOK.JOHN, BOOK.ONE_JOHN, BOOK.TWO_JOHN, BOOK.THREE_JOHN)

    val NUM_COLS = 4
    val NUM_ROWS = 5

    var mCurrentBooks: ArrayList<BOOK> = ArrayList()
    var mCurrentScreen = Screen.LETTER_PICK

    lateinit var mBtnBack: View

    val DEFAULT_WEIGHT = 1f / NUM_COLS.toFloat()

    var mVerseQuery = ""
    var mCurrentVersesCounts: Array<Int>? = null
    var mCurrentRef: BibleRef = BibleRef(BibleVersion.KJV, BOOK.GENESIS, 1, 1)
    var mTvGrid = arrayOfNulls<TextView>(NUM_ROWS * NUM_COLS)
    var mRowViews = arrayOfNulls<ViewGroup>(NUM_ROWS)

    var mNumpad = arrayOfNulls<TextView>(10)

    lateinit var mLblVerseQuery: TextView
    lateinit var mBtnColon: TextView
    lateinit var mBtnBackspace: TextView

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    public fun setListener(listener: BibleNavListener) {
        mListener = listener
    }

    private fun init(context: Context) {
        orientation = LinearLayout.VERTICAL

        // Build the views grid
        var viewIdx = 0
        for (rowIdx in 0 until NUM_ROWS) {
            // Build a row and keep a ref
            var lettersRow = LinearLayout(context)
            addView(lettersRow)
            var lp = lettersRow.layoutParams as LayoutParams
            lp.height = 0
            lp.weight = 1f
            lp.width = LayoutParams.MATCH_PARENT

            mRowViews[rowIdx] = lettersRow

            for (colIdx in 0 until NUM_COLS) {  // Fill each row w views for buttons
                var tv = LayoutInflater.from(context).inflate(R.layout.nav_button, lettersRow, false) as TextView
                tv.setOnClickListener(this)
                mTvGrid[viewIdx++] = tv

                // Back button setup
                if (rowIdx == 0 && colIdx == 0) {
                    Log.v(TAG, "set up back, my size is $width , $height")
                    var container = LayoutInflater.from(context).inflate(R.layout.back_btn_container, lettersRow, false)
                    lettersRow.addView(container)
                    (container.layoutParams as LayoutParams).weight = DEFAULT_WEIGHT
                    mBtnBack = container.findViewById(R.id.btn_back)
                    mBtnBack.setOnClickListener(this)

                    // The first btn is a dummy TV to fill out the grid for easy math. The back button will actually live here. Sorry
                } else {
                    lettersRow.addView(tv)
                }
            }
        }

        setScreen(Screen.LETTER_PICK)
        invalidate()
    }

    fun reset() {
        setScreen(Screen.LETTER_PICK)
    }

    fun setScreen(screen: Screen, books: ArrayList<BOOK> = arrayListOf()) {
        Log.v(TAG, "setScreen, books: " + books)

        // Reset everything to blank grid
        var skippedDummy = false
        for (tv in mTvGrid) {
            if (!skippedDummy) {
                skippedDummy = true
                continue
            }

            (tv?.layoutParams as LayoutParams).weight = DEFAULT_WEIGHT
            tv?.visibility = View.VISIBLE
            tv?.tag = null
            tv?.text = ""
            tv?.gravity = Gravity.CENTER
            tv?.setPadding(0, 0, 0, 0)
            tv?.isSelected = false
            if (tv == mBtnBack) {
                tv?.text = "<<"
            }
            tv?.isEnabled = true
            mVerseQuery = ""
            mCurrentRef.verse = null
        }

        for (view in mRowViews) {
            view?.visibility = View.VISIBLE
        }

        // Make only the first column visible so the name can span the row
        // If there are exactly 5 books, shoehorn another book in there
        when (screen) {
            Screen.LETTER_PICK -> {
                // 4 cols x 5 rows
                var letterIdx = 0
                for (i in 1 until mTvGrid.size) {
                    if (letterIdx < MENU_LETTER.values().size) {
                        mTvGrid[i]?.text = MENU_LETTER.values()[letterIdx++].name
                    }
                }
            }

            Screen.BOOK_PICK -> {
                mCurrentBooks = books

                // Set books display
                for (i in 1 until mTvGrid.size) {
                    mTvGrid[i]?.visibility = View.GONE
                }

                var bookIdx = 0;

                // Show the first two cols; the first is for alignment, the second is the book name
                for (i in 0 until NUM_ROWS) {
                    if (bookIdx >= books.size) {    // Hide unused cols, keep at least a 3rd around for padding
                        mRowViews[i]?.visibility = if (i > 2) View.GONE else View.INVISIBLE
                        continue
                    }

                    var tvIdx = NUM_COLS * (i) + 1
                    if (i != 0) {
                        // Try aligning everything to right of BACK BTN
                        mTvGrid[tvIdx - 1]?.visibility = View.INVISIBLE
                    }

                    var tv = mTvGrid[tvIdx]!!
                    (tv.layoutParams as LayoutParams).weight = DEFAULT_WEIGHT * 3
                    tv.visibility = View.VISIBLE
                    tv.gravity = Gravity.LEFT or (Gravity.CENTER_VERTICAL)
                    tv.setPadding(resources.getDimensionPixelSize(R.dimen.book_btn_pad_left), 0, 0, 0)

                    var book = books[bookIdx++]
                    tv.text = book.display
                    tv.tag = book
                }
            }

            Screen.VERSE_PICK -> {
                var idx = 0
                for (i in 1..9) {
                    var row = (i - 1) / 3 + 1
                    var col = (i - 1) % 3

                    idx = row * NUM_COLS + col
                    mTvGrid[idx]?.text = i.toString()
                    mNumpad[i] = mTvGrid[idx]
                }

                mNumpad[0] = mTvGrid[idx + 1]
                mNumpad[0]?.text = "0"
                mNumpad[0]?.isEnabled = false

                mCurrentRef.book = books[0]
                mCurrentVersesCounts = BibleData.VERSES_MAP[mCurrentRef.book]

                var btnIdx = NUM_COLS * 1 + NUM_ROWS - 2
                mBtnBackspace = mTvGrid[btnIdx]!!
                mBtnBackspace.text = "⌫"

                btnIdx += NUM_COLS
                mBtnColon = mTvGrid[btnIdx]!!
                mBtnColon.text = ":"

                mLblVerseQuery = mTvGrid[1]!!
                (mLblVerseQuery.layoutParams as LayoutParams).weight = DEFAULT_WEIGHT * 3
                //mLblVerseQuery.text = mCurrentRef.book.display
                mTvGrid[2]?.visibility = View.GONE
                mTvGrid[3]?.visibility = View.GONE

                mRowViews[mRowViews.size - 1]?.visibility = View.GONE
                onVerseUpdated()
            }
        }

        mCurrentScreen = screen
    }

    /*
    Build the verse. Complicated
     */
    fun onVerseUpdated(digit: Char? = null) {
        if (digit != null) {
            mVerseQuery += digit
        }

        mLblVerseQuery.text = mCurrentRef.book.display + " " + mVerseQuery

        Log.v(TAG, "Verse updated " + mVerseQuery)

        // First, get the number we're working on, either the verse or chap, as "myPart",
        // along with the largest number it can be as "partMax"
        var partMax = -1
        var myPart = ""
        var hasColon = mVerseQuery.contains(":")
        if (hasColon) {
            var parts = mVerseQuery.split(":")
            partMax = mCurrentVersesCounts!![parts[0].toInt() - 1]
            myPart = parts[1]

            if (!TextUtils.isEmpty(myPart)) {
                mCurrentRef.verse = myPart.toInt()
            }

            Log.v(
                TAG,
                "chap is " + mCurrentRef.chap + " building verse " + myPart + " max " + partMax + " verseref " + mCurrentRef.verse
            )

        } else {
            partMax = mCurrentVersesCounts!!.size
            myPart = mVerseQuery

            if (!TextUtils.isEmpty(myPart)) {
                mCurrentRef.chap = myPart.toInt()
            }
            Log.v(TAG, "building chap " + myPart + " max is " + partMax)
        }

        var myLength = myPart.length
        var partLength = partMax.toString().length

        mBtnColon.isEnabled = !hasColon && myLength > 0
        mBtnBackspace.isEnabled = mVerseQuery.length > 0
        mLblVerseQuery.isSelected = mBtnBackspace.isEnabled

        // Now figure out what numbers can come next so we can disable impossible ones
        var maxDigit = partMax
        if (myLength == partLength) {   // If our number has maxed out digit spaces, no numbers can follow
            maxDigit = -1
        } else if (myLength + 1 == partLength) {
            if (myLength == 0) {
                maxDigit = partMax.toString()[myLength] - '0'
            } else {
                var myLastDigit = myPart[myLength - 1] - '0'
                var partLastDigit = partMax.toString()[myLength - 1] - '0'

                if (myLastDigit > partLastDigit) {
                    maxDigit = -1
                } else if (myLastDigit == partLastDigit) {
                    maxDigit = partMax.toString()[myLength] - '0'
                }
            }
        }

        Log.v(TAG, "maxdigit " + maxDigit)
        for (i in 0..9) {
            var enable = i <= maxDigit
            mNumpad[i]?.isEnabled = enable
        }

        // Zero
        if (myLength == 0) {
            mNumpad[0]?.isEnabled = false
        }
    }

    fun goBack() {
        when (mCurrentScreen) {
            Screen.BOOK_PICK -> setScreen(Screen.LETTER_PICK)
            Screen.VERSE_PICK -> setScreen(Screen.BOOK_PICK, mCurrentBooks)
            Screen.LETTER_PICK -> mListener?.onNavBackPressed()
        }
    }

    fun findBooks(query: String): ArrayList<BOOK> {
        var matches: ArrayList<BOOK> = ArrayList()

        try {
            var books = MENU_LETTER.valueOf(query).books
            Log.v(TAG, "found books " + books.toString())
            matches.addAll(books)
        } catch (e: Exception) {
            Log.v(TAG, "not found", e)
        }

        return matches
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        // Hack to lock in back button to initial grid size
        if (mBtnBack.layoutParams.width == LayoutParams.MATCH_PARENT && mBtnBack.measuredWidth != 0) {
            Log.v(TAG, "setting back width to " + mBtnBack.measuredWidth)
            mBtnBack.layoutParams.width = mBtnBack.measuredWidth
            mBtnBack.layoutParams.height = mBtnBack.measuredHeight
        }
    }

    override fun onClick(view: View?) {
        if (view is TextView) {
            var text = view.text.toString()

            if (view == mBtnBack) {
                goBack()
                return;
            }

            when (mCurrentScreen) {
                Screen.LETTER_PICK -> {
                    var books = findBooks(text)
                    Log.v(TAG, "clicked to get books " + books)
                    setScreen(Screen.BOOK_PICK, books)
                }

                Screen.BOOK_PICK -> {
                    var tag = view.tag
                    Log.v(TAG, "Clicked to get book " + tag)
                    if (tag is BOOK) {
                        if (tag == BOOK.JOHNS_CATEGORY) {
                            setScreen(Screen.BOOK_PICK, BOOKS_JOHN)
                        } else {
                            setScreen(Screen.VERSE_PICK, arrayListOf(tag))
                        }
                    }
                }

                Screen.VERSE_PICK -> {
                    var digit = (view as TextView).text[0]
                    Log.v(TAG, "Clicked digit " + digit)

                    if (view == mBtnBackspace) {
                        mVerseQuery = mVerseQuery.substring(0, Math.max(mVerseQuery.length - 1, 0))
                        onVerseUpdated()
                    } else if (view == mLblVerseQuery) {
                        if (view.isSelected) {
                            //Toast.makeText(context, mLblVerseQuery.text, Toast.LENGTH_LONG).show()

                            Log.v(TAG, "Clicked verse " + mCurrentRef.toString())

                            mListener?.onRefSelected(BibleRef(mCurrentRef))
                        }
                    } else {
                        onVerseUpdated(digit)
                    }
                }
            }

        }
    }
}