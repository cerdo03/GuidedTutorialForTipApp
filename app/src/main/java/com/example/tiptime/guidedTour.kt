package com.example.tiptime


import android.content.Context
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.Toast
import java.util.*


class guidedTour {
    fun tour(
        v: ArrayList<View>,
        context: Context,
        title: ArrayList<String>,
        content: ArrayList<String>,
        time: Long,
        fullScreen: androidx.constraintlayout.widget.ConstraintLayout,
    ){
        for(i in 0 until v.size){
            var onClicked=1
            var handler = Handler()
            handler.postDelayed({
                val guide = GuideView(context,v.get(i))
                guide.setTitle(title.get(i))
                guide.setContentText(content.get(i))
                guide.show()


                Handler().postDelayed({
                    guide.dismiss()

                },time*onClicked)}, (i*time)*onClicked)
        }

    }
}