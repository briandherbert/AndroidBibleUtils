package com.example.utils.bl

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.example.brianherbert.biblenavwatch.data.BibleRef
import com.example.brianherbert.biblenavwatch.data.BibleVersion
import com.example.utils.data.BiblePassage
import java.lang.Exception
import android.content.pm.PackageManager
import android.content.pm.PackageInfo
import android.text.TextUtils


/**
 * Tools for interacting (informally) with the YouVersion Bible app
 */
class YVAppTools {
    companion object {
        val TAG = "YVAppTools"

        val YV_PACKAGE_NAME = "com.sirma.mobile.bible.android"

        fun getAppLink(ref: BibleRef): String {
            var url = "https://bible.com/bible/59/${ref.usfm()}.${ref.version.display}"
            Log.v(TAG, "Created url $url")
            return url
        }

        fun goToApp(activity: Activity, ref: BibleRef) {
            activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getAppLink(ref))))
        }

        fun parseYVShareIntent(intent: Intent): BiblePassage? {
            var passage: BiblePassage? = null

            try {
                intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
                    Log.v(TAG, "parsing $it")
                    var str = it
                    var refStr = str.substringAfterLast('/').substringBeforeLast('.').trim()
                    var version = str.substringAfterLast('.').trim()
                    var content = str.substringBefore('\n').trim()
                    passage = BiblePassage(BibleRef(BibleVersion.valueOf(version), refStr), content)
                }
            } catch (e: Exception) {

            }

            return passage
        }

        fun isYVInstalled(pm : PackageManager): Boolean {
            try {
                val info = pm.getPackageInfo(YV_PACKAGE_NAME, PackageManager.GET_META_DATA)
            } catch (e: PackageManager.NameNotFoundException) {
                return false
            }

            return true
        }

    }
}