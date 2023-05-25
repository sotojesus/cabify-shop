package com.jesussoto.android.cabifyshop.ui.util

import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan

/**
 * Applies strikethrough span to the string passed is aht returns it as a SpannableString.
 */
fun applyStrikethroughSpan(text: String): SpannableString {
    return SpannableString(text).apply {
        setSpan(StrikethroughSpan(), 0, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}