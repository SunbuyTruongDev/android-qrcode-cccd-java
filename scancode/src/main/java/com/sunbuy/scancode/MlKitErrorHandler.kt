package com.sunbuy.scancode

import android.app.Activity

object MlKitErrorHandler {

  @Suppress("UNUSED_PARAMETER", "FunctionOnlyReturningConstant")
  fun isResolvableError(activity: Activity, exception: Exception) = false // always false when bundled
}