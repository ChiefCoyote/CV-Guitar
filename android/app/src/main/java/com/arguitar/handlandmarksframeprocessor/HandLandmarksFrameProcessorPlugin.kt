package com.arguitar.handlandmarksframeprocessor

import android.util.Log
import com.arguitar.AppContextProvider
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.MediaMetadataRetriever
import androidx.camera.core.ImageProxy
import android.os.SystemClock
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.mrousavy.camera.frameprocessors.Frame
import com.mrousavy.camera.frameprocessors.FrameProcessorPlugin
import com.mrousavy.camera.frameprocessors.VisionCameraProxy
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.facebook.react.bridge.ReactApplicationContext
import java.nio.ByteBuffer

class HandLandmarksFrameProcessorPlugin(proxy: VisionCameraProxy, options: Map<String, Any>?): FrameProcessorPlugin() {
  override fun callback(frame: Frame, arguments: Map<String, Any>?): Any? {
    // code goes here

      val context = AppContextProvider.getContext()
      val baseOptionsBuilder = BaseOptions.builder()

      baseOptionsBuilder.setModelAssetPath("hand_landmarks.task")

      try {
          baseOptionsBuilder.setDelegate(Delegate.GPU)
      } catch (e: Exception) {
          baseOptionsBuilder.setDelegate(Delegate.CPU)
      }

      val baseOptions = baseOptionsBuilder.build()


      val optionsBuilder = HandLandmarker.HandLandmarkerOptions.builder()
          .setBaseOptions(baseOptions)
          .setNumHands(2)
          .setMinHandDetectionConfidence(0.5f)
          .setMinTrackingConfidence(0.5f)
          .setMinHandPresenceConfidence(0.5f)
          .setRunningMode(RunningMode.VIDEO)

      val options = optionsBuilder.build()


    try {
        var handLandmarker = HandLandmarker.createFromOptions(context, options)
        val imageProxy = frame.getImageProxy()

        val width = imageProxy.width
        val height = imageProxy.height

        // Check the format and extract the appropriate plane data
        val buffer = imageProxy.planes[0].buffer // Assuming we are using the first plane
        val data = ByteArray(buffer.remaining())
        buffer.get(data)


        // Convert byte data to Bitmap (if needed)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.copyPixelsFromBuffer(buffer)

        val image = BitmapImageBuilder(bitmap).build()

        val result = handLandmarker.detectForVideo(image, frame.timestamp)

        val landmarks = mutableListOf<List<Map<String, Float>>>()

        for (hand in result.landmarks()) {
          val marks = mutableListOf<Map<String, Float>>()

          for (handmark in hand) {
            marks.add(mapOf(
              "x" to handmark.x(),
              "y" to handmark.y()
            ))
          }

          landmarks.add(marks)
        }
      return landmarks
    }
    catch (e: Exception){
      return null
    }

  }
}