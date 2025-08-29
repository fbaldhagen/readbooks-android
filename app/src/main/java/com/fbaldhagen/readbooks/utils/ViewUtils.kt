package com.fbaldhagen.readbooks.utils

import android.view.View
import android.view.ViewGroup
import android.webkit.WebView

fun View.findWebView(): WebView? {
    if (this is WebView) {
        return this
    }
    if (this is ViewGroup) {
        val queue: MutableList<View> = mutableListOf(this)
        while (queue.isNotEmpty()) {
            val currentView = queue.removeAt(0)
            if (currentView is WebView) {
                return currentView
            }
            if (currentView is ViewGroup) {
                for (i in 0 until currentView.childCount) {
                    queue.add(currentView.getChildAt(i))
                }
            }
        }
    }
    return null
}