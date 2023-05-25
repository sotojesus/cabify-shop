package com.jesussoto.android.cabifyshop.ui.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

/**
 * Loads static image Bitmap from assets by product code
 *
 * It is not very efficient to do it this way since bitmaps are too large and reusing them
 * for thumbnails and full-sized images causes unnecessary memory allocation.
 *
 * Ideally, we should have thumbnails and full-size versions of the images to load on each
 * scenario to save as much memory as possible.
 */
fun getImageBitmapForProduct(productCode: String, context: Context): Bitmap?  {
    val imageName = imageNameByProductCode[productCode] ?: return null

    var bitmap = productImageCache[productCode]
    if (bitmap == null) {
        bitmap = BitmapFactory.decodeStream(context.assets.open(imageName))
        productImageCache[productCode] = bitmap
    }

    return bitmap
}

private val productImageCache = mutableMapOf<String, Bitmap>()

private val imageNameByProductCode = mapOf(
    "VOUCHER" to "voucher-illustration-1.jpeg",
    "TSHIRT" to "tshirt-illustration-1.jpeg",
    "MUG" to "mug-illustration-2.webp"
)
