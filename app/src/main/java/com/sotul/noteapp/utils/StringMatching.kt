package com.sotul.noteapp.utils

import com.sotul.noteapp.viewmodel.StateViewModel

private fun computeBorder(string: String): IntArray {
    val border = IntArray(string.length) { 0 }
    border[0] = 0

    val m = string.length
    var j = 0
    var i = 1

    while (i < m) {
        if (string[j] == string[i]) {
            border[i] = j + 1
            i++
            j++
        } else if (j > 0) {
            j = border[j - 1]
        } else {
            border[i] = 0
            i++
        }
    }
    return border
}

fun getIndexOfKMP(text: String, pattern: String): Int {
    val n = text.length
    val m = pattern.length

    if (m > n) return  -1

    val border = computeBorder(pattern)

    var i = 0
    var j = 0

    while (i < n) {
        if (pattern[j] == text[i]) {
            if (j == m - 1) return i - m + 1
            i++
            j++
        } else if (j > 0) {
            j = border[j-1]
        } else {
            i++
        }
    }
    return -1
}

fun buildLast(pattern: String): IntArray {
    val last = IntArray(256) { -1 }

    for (i in pattern.indices) {
        last[pattern[i].code] = i
    }

    return last
}

fun getIndexOfBM(text: String, pattern: String): Int {
    val last = buildLast(pattern)
    val n = text.length
    val m = pattern.length
    var i = m - 1

    if (i > n - 1) {
        return -1
    }

    var j = m - 1
    do {
        if (pattern[j] == text[i]) {
            if (j == 0) {
                return i
            } else {
                i--
                j--
            }
        } else {
            val lo = last[text[i].code]
            i += m - minOf(j, 1 + lo)
            j = m - 1
        }
    } while (i <= n - 1)

    return -1
}

fun String.isContains(pattern: String, mode: Boolean = StateViewModel.STRING_MATCHING_MODE_KMP): Boolean {
    return if (mode == StateViewModel.STRING_MATCHING_MODE_BM) getIndexOfBM(this, pattern) != -1
    else getIndexOfKMP(this, pattern) != -1
}
