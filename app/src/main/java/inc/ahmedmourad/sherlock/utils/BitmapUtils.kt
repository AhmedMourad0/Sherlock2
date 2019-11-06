package inc.ahmedmourad.sherlock.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import splitties.init.appCtx
import java.io.ByteArrayOutputStream

internal fun getImageBytes(imagePath: String): ByteArray {
    return getImageBytes(getImageBitmap(imagePath))
}

internal fun getImageBytes(bitmap: Bitmap): ByteArray {
    return ByteArrayOutputStream().also { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }.toByteArray()
}

internal fun getImageBytes(@DrawableRes drawablePath: Int): ByteArray {
    return getImageBytes((ContextCompat.getDrawable(appCtx, drawablePath) as BitmapDrawable).bitmap)
}

internal fun getImageBitmap(imagePath: String) = BitmapFactory.decodeFile(imagePath)
        ?: throw IllegalArgumentException("$imagePath is not a valid image path!")
