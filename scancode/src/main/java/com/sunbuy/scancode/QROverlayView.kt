package com.sunbuy.scancode

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources.NotFoundException
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.base.common.extensions.hidden
import com.base.common.extensions.setSingleClickListener
import com.base.common.extensions.show
import com.sunbuy.scancode.databinding.QuickieOverlayViewBinding
import kotlin.math.roundToInt

@Suppress("TooManyFunctions")
public class QROverlayView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

  private val binding = QuickieOverlayViewBinding.inflate(LayoutInflater.from(context),this,false)
  private val grayColor = ContextCompat.getColor(context, R.color.quickie_gray)
  private val accentColor = getAccentColor()
  private var horizontalFrameRatio = 1f


  init {
    setWillNotDraw(false)
    addView(binding.root)
  }

  @Suppress("UnsafeCallOnNullableType")
  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
  }

  fun setCustomText(stringRes: Int) {
    if (stringRes != 0) {
      try {
        binding.titleTextView.setText(stringRes)
      } catch (ignore: NotFoundException) {
        // string resource not found
      }
    }
  }

  fun setCustomIcon(drawableRes: Int?) {
    if (drawableRes == null) {
      binding.titleTextView.setCompoundDrawables(null, null, null, null)
    } else if (drawableRes != 0) {
      try {
        ResourcesCompat.getDrawable(resources, drawableRes, null)?.limitDrawableSize()?.let {
          binding.titleTextView.setCompoundDrawables(null, it, null, null)
        }
      } catch (ignore: NotFoundException) {
        // drawable resource not found
      }
    }
  }

  fun setHorizontalFrameRatio(ratio: Float) {
    if (ratio > 1f) {
      horizontalFrameRatio = ratio
    }
  }

  fun setScanAnimationShow(){
    binding.lottieScanAnimation.show()
  }

  fun setScanAnimationHide(){
    binding.lottieScanAnimation.hidden()
  }

  fun setFlashState(isOn : Boolean){
    if (isOn){
      binding.imgFlash.setImageResource(R.drawable.ic_flash_on)
    }else{
      binding.imgFlash.setImageResource(R.drawable.ic_flash_off)
    }
  }

  fun setOpenFlash(openFlash : () ->Unit = {}){
    binding.imgFlash.setSingleClickListener {
      openFlash.invoke()
    }
  }

  fun setOpenSoundPip(onPipSound : () -> Unit = {}){
    binding.imgSoundPip.setSingleClickListener {
        onPipSound.invoke()
    }
  }

  fun setSoundPipState(isOn : Boolean){
      binding.imgSoundPip.setImageResource(if (isOn)R.drawable.ic_sound_pip_on else R.drawable.ic_sound_pip_off)
  }

  fun flipCamera(flipCamera : () -> Unit ={}){
    binding.btnFlipCamera.setSingleClickListener {
      flipCamera()
    }
  }

  fun importFromGallery(pickImage : () -> Unit = {}){
    binding.btnImportImage.setSingleClickListener {
      pickImage()
    }
  }



  private fun getAccentColor(): Int {
    return TypedValue().let {
      if (context.theme.resolveAttribute(android.R.attr.colorAccent, it, true)) {
        it.data
      } else {
        ContextCompat.getColor(context, R.color.quickie_accent_fallback)
      }
    }
  }

  private fun View.updateTopMargin(topPx: Int) {
    val params = layoutParams as MarginLayoutParams
    params.topMargin = topPx
    layoutParams = params
  }

  private fun Drawable.limitDrawableSize(): Drawable {
    val heightLimit = ICON_MAX_HEIGHT.toPx()
    val scale = heightLimit / minimumHeight
    if (scale < 1) {
      setBounds(0, 0, (minimumWidth * scale).roundToInt(), (minimumHeight * scale).roundToInt())
    } else {
      setBounds(0, 0, minimumWidth, minimumHeight)
    }
    return this
  }

  private fun View.setTintAndStateAwareBackground() {
    background?.let { drawable ->
      val wrappedDrawable = DrawableCompat.wrap(drawable)

      val states = arrayOf(
        intArrayOf(android.R.attr.state_pressed, android.R.attr.state_selected),
        intArrayOf(android.R.attr.state_pressed, -android.R.attr.state_selected),
        intArrayOf(-android.R.attr.state_pressed, android.R.attr.state_selected),
        intArrayOf()
      )
      val stateColors = intArrayOf(grayColor, accentColor, accentColor, grayColor)
      val colorStateList = ColorStateList(states, stateColors).withAlpha(BUTTON_BACKGROUND_ALPHA.roundToInt())

      DrawableCompat.setTintList(wrappedDrawable, colorStateList)
      background = wrappedDrawable
    }
  }

  private fun Float.toPx() =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, resources.displayMetrics)

  companion object {
    private const val BACKGROUND_ALPHA = 0.77 * 255
    private const val BUTTON_BACKGROUND_ALPHA = 0.6 * 255
    private const val STROKE_WIDTH = 4f
    private const val OUT_RADIUS = 16f
    private const val FRAME_MARGIN_RATIO = 1f / 4
    private const val ICON_MAX_HEIGHT = 56f
  }
}