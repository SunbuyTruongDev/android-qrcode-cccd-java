package com.sunbuy.scancode.extensions

import com.sunbuy.scancode.config.ParcelableScannerConfig
import com.sunbuy.scancode.config.ScannerConfig

internal fun ScannerConfig.toParcelableConfig() =
  ParcelableScannerConfig(
    formats = formats,
    stringRes = stringRes,
    drawableRes = drawableRes,
    hapticFeedback = hapticFeedback,
    showTorchToggle = showTorchToggle,
    horizontalFrameRatio = horizontalFrameRatio,
    useFrontCamera = useFrontCamera,
  )