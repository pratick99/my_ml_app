package com.pratik.mymlapp.camera.util

import android.graphics.Bitmap
import android.util.Log
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions

class ImageClassificationHelper(
    val objectDetectorListener: DetectorListener?
) {
    private val logTag = ImageClassificationHelper::class.java.simpleName

//    private fun setupObjectDetector() {
// //        val optionsBuilder = ObjectDetector.ObjectDetectorOptions.builder()
// //            .setScoreThreshold(threshold)
// //            .setMaxResults(maxResults)
// //
// //        val baseOptionsBuilder = BaseOptions.builder().setNumThreads(numThreads)
// //
// //        optionsBuilder.setBaseOptions(baseOptionsBuilder.build())
// //
// //        try {
// //            objectDetector = ObjectDetector.createFromFileAndOptions(context, "cereal_model.tflite", optionsBuilder.build())
// //        } catch (e: Exception) {
// //            objectDetectorListener?.onError(
// //                "Object detector failed to initialize"
// //            )
// //            Log.e(ObjectDetector::class.simpleName, "TFlite failed to load model with error: ${e.stackTrace}")
// //        }
//
//    }

    fun detect(image: Bitmap) {
        val inputImage: InputImage = InputImage.fromBitmap(image, 0)

        val localModel: LocalModel = LocalModel.Builder().setAssetFilePath("cereal_model.tflite").build()

        val options: CustomObjectDetectorOptions = CustomObjectDetectorOptions.Builder(localModel)
            .setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE)
            .enableClassification()
            .setClassificationConfidenceThreshold(0.5f)
            .setMaxPerObjectLabelCount(3)
            .build()

        val objectDetector = ObjectDetection.getClient(options)

        objectDetector.process(inputImage).addOnSuccessListener {
            objectDetectorListener?.onResults(it, image.height, image.width)
        }.addOnFailureListener {
            Log.e(logTag, "Failed to detect objects : ${it.stackTrace}")
        }
    }
}
