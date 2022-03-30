package com.example.tiptime

import android.graphics.Path
import android.graphics.RectF


interface Targetable {

    fun guidePath(): Path?

    fun boundingRect(): RectF?
}