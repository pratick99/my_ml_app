package com.pratik.mymlapp.camera.util

import com.google.mlkit.vision.objects.DetectedObject


interface DetectorListener {
    fun onError(error: String)
    fun onResults(
        results: MutableList<DetectedObject>?,
        imageHeight: Int,
        imageWidth: Int
    )
}
