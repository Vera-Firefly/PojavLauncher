package com.mio

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import android.util.Printer

class Detector : Printer {
    companion object {
        fun install() {
            Looper.getMainLooper().setMessageLogging(Detector())
        }
    }

    private val sampler = StackSampler(300)
    private var isStarted = false
    private var startTime = 0L

    override fun println(x: String?) {
        if (!isStarted) {
            isStarted = true
            startTime = System.currentTimeMillis()
            sampler.startDump()
        } else {
            isStarted = false
            val endTime = System.currentTimeMillis()
            if (endTime - startTime > 300) {
                Log.e("Detector", "block time = ${endTime - startTime}")
            }
            sampler.stopDump()
        }
    }

    inner class StackSampler(val interval: Long) {
        private val handler: Handler
        private val runnable = Runnable {
            val sb = StringBuilder()
            Looper.getMainLooper().thread.stackTrace.forEach {
                sb.append(it.toString())
                sb.append("\n")
            }
            Log.e("Detector", sb.toString())
        }

        init {
            val handlerThread = HandlerThread("")
            handlerThread.start()
            handler = Handler(handlerThread.looper)
        }

        fun startDump() {
            handler.postDelayed(runnable, interval)
        }

        fun stopDump() {
            handler.removeCallbacks(runnable)
        }
    }
}