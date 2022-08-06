package com.pratik.mymlapp.camera.util

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.Rot90Op
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.detector.ObjectDetector

class ImageClassificationHelper(
    private var threshold: Float = 0.5f,
    private var numThreads: Int = 2,
    private var maxResults: Int = 5,
    private var currentDelegate: Int = 0,
    private val context: Context,
    private val objectDetectorListener: DetectorListener?
) {
    // For this example this needs to be a var so it can be reset on changes. If the ObjectDetector
    // will not change, a lazy val would be preferable.
    private var objectDetector: ObjectDetector? = null

    init {
        setupObjectDetector()
    }

    private fun setupObjectDetector() {
        val optionsBuilder = ObjectDetector.ObjectDetectorOptions.builder()
            .setScoreThreshold(threshold)
            .setMaxResults(maxResults)

        val baseOptionsBuilder = BaseOptions.builder().setNumThreads(numThreads)
        baseOptionsBuilder.useGpu()

        optionsBuilder.setBaseOptions(baseOptionsBuilder.build())

        try {
            objectDetector = ObjectDetector.createFromFileAndOptions(context, "cereal_model.tflite", optionsBuilder.build())
        } catch (e: IllegalAccessException) {
            objectDetectorListener?.onError(
                "Object detector failed to initialize"
            )
            Log.e(ObjectDetector::class.simpleName, "TFlite failed to load model with error: ${e.message}")
        }
    }

    fun detect(image: Bitmap, imageRotation: Int) {
        if (objectDetector == null) {
            setupObjectDetector()
        }

        var inferenceTime = SystemClock.uptimeMillis()

        val imageProcessor = ImageProcessor.Builder()
            .add(Rot90Op(-imageRotation / 90))
            .build()

        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(image))

        val results = objectDetector?.detect(tensorImage)

        inferenceTime = SystemClock.uptimeMillis() - inferenceTime
        objectDetectorListener?.onResults(
            results,
            inferenceTime,
            tensorImage.height,
            tensorImage.width
        )
    }
}
