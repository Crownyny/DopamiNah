package co.edu.unicauca.dopaminah.ui.utils

import androidx.compose.ui.graphics.ImageBitmap

/** Platform-specific decoding of a [ByteArray] into an [ImageBitmap] (`expect`/`actual` pattern). */
expect fun ByteArray.decodeToImageBitmap(): ImageBitmap?
