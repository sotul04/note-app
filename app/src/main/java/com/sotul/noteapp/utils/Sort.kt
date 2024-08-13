package com.sotul.noteapp.utils

import com.sotul.noteapp.model.Note

fun MutableList<Note>.mergeSort(comparator: Comparator<Note>) {
    if (this.size <= 1) return

    val middle = this.size / 2
    val left = this.subList(0, middle).toMutableList()
    val right = this.subList(middle, this.size).toMutableList()

    left.mergeSort(comparator)
    right.mergeSort(comparator)

    this.merge(left, right, comparator)
}

private fun MutableList<Note>.merge(left: MutableList<Note>, right: MutableList<Note>, comparator: Comparator<Note>) {
    var indexLeft = 0
    var indexRight = 0
    var indexResult = 0

    while (indexLeft < left.size && indexRight < right.size) {
        if (comparator.compare(left[indexLeft], right[indexRight]) <= 0) {
            this[indexResult] = left[indexLeft]
            indexLeft++
        } else {
            this[indexResult] = right[indexRight]
            indexRight++
        }
        indexResult++
    }

    while (indexLeft < left.size) {
        this[indexResult] = left[indexLeft]
        indexLeft++
        indexResult++
    }

    while (indexRight < right.size) {
        this[indexResult] = right[indexRight]
        indexRight++
        indexResult++
    }
}

fun MutableList<Note>.quickSort(low: Int = 0, high: Int = this.size - 1, comparator: Comparator<Note>) {
    if (low < high) {
        val pivotIndex = this.partition(low, high, comparator)
        this.quickSort(low, pivotIndex - 1, comparator)
        this.quickSort(pivotIndex + 1, high, comparator)
    }
}

private fun MutableList<Note>.partition(low: Int, high: Int, comparator: Comparator<Note>): Int {
    val pivot = this[high]
    var i = low - 1

    for (j in low until high) {
        if (comparator.compare(this[j], pivot) <= 0) {
            i++
            if (i != j) {
                this[i] = this[j].also { this[j] = this[i] }
            }
        }
    }

    if (i + 1 != high) {
        this[i + 1] = this[high].also { this[high] = this[i + 1] }
    }
    return i + 1
}