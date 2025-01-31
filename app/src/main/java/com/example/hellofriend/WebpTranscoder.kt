package com.example.hellofriend

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.bitmap.BitmapResource
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.Glide
import java.io.ByteArrayOutputStream
import java.security.MessageDigest
import kotlin.hashCode
import kotlin.text.toByteArray

class WebpTranscoder : Transformation<Bitmap> {

    override fun transform(
        context: Context,
        resource: Resource<Bitmap>,
        outWidth: Int,
        outHeight: Int
    ): Resource<Bitmap> {
        val bitmap = resource.get()
        return try {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.WEBP, 80, stream) // Compress to WebP
            val webpBytes = stream.toByteArray()
            val webpBitmap = BitmapFactory.decodeByteArray(webpBytes, 0, webpBytes.size)
            BitmapResource.obtain(webpBitmap, Glide.get(context).bitmapPool) ?: resource
        } catch (e: Exception) {
            Log.e("WebpTranscoder", "Error converting to WebP: ${e.message}")
            resource
        }
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update("WebpTranscoder".toByteArray(Charsets.UTF_8))
    }

    override fun equals(other: Any?): Boolean = other is WebpTranscoder

    override fun hashCode(): Int = "WebpTranscoder".hashCode()
}