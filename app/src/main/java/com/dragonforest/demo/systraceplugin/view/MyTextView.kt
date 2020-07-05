package com.dragonforest.demo.systraceplugin.view

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView

/**
 *
 * create by DragonForest at 2020/7/5
 */
class MyTextView : androidx.appcompat.widget.AppCompatTextView {
    constructor(context: Context) : super(context) {}
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {}

    fun showMessage(s: String?) {
        Thread.sleep(300)
        setText(s)
    }
}