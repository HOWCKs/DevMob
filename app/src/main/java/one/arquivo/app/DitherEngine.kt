package one.arquivo.app

import android.graphics.Bitmap
import android.graphics.Color

/** Local ordered-dither processor. Images never leave the device. */
object DitherEngine {
    private val gameBoy = intArrayOf(
        Color.rgb(15, 56, 15),
        Color.rgb(48, 98, 48),
        Color.rgb(139, 172, 15),
        Color.rgb(155, 188, 15)
    )

    private val bayer4 = arrayOf(
        intArrayOf(0, 8, 2, 10),
        intArrayOf(12, 4, 14, 6),
        intArrayOf(3, 11, 1, 9),
        intArrayOf(15, 7, 13, 5)
    )

    fun gameBoy(source: Bitmap, cellSize: Int = 3): Bitmap {
        val safeCell = cellSize.coerceIn(1, 8)
        val width = (source.width / safeCell).coerceAtLeast(1)
        val height = (source.height / safeCell).coerceAtLeast(1)
        val scaled = Bitmap.createScaledBitmap(source, width, height, false)
        val pixels = IntArray(width * height)
        scaled.getPixels(pixels, 0, width, 0, 0, width, height)

        for (y in 0 until height) for (x in 0 until width) {
            val color = pixels[y * width + x]
            val luminance = (Color.red(color) * 0.299f + Color.green(color) * 0.587f + Color.blue(color) * 0.114f)
            val threshold = (bayer4[y % 4][x % 4] - 7.5f) * 7.5f
            val index = ((luminance + threshold) / 64f).toInt().coerceIn(0, 3)
            pixels[y * width + x] = gameBoy[index]
        }
        val dithered = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        dithered.setPixels(pixels, 0, width, 0, 0, width, height)
        return Bitmap.createScaledBitmap(dithered, source.width, source.height, false)
    }
}
