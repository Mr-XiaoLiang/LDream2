package com.lollipop.ldream.util

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.ImageView
import com.lollipop.ldream.drawer.FlashDrawable
import java.util.*
import kotlin.math.min

/**
 * @author lollipop
 * @date 2020-02-17 23:50
 * 闪光的辅助类
 */
class FlashHelper(private val flashDrawable: FlashDrawable = FlashDrawable()):
    ValueAnimator.AnimatorUpdateListener,
    Animator.AnimatorListener{

    private var bindView: View? = null

    var flashEnable = true

    private val pendingTask = LinkedList<FlashTask>()

    private var lastFlashTime = 0L

    private val minDelay = 100L * 5

    private val flashDuration = 100L * 8

    private val handler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }

    private val random: Random by lazy { Random() }

    private val animator = ValueAnimator.ofFloat(-1F, 1F).apply {
        duration = flashDuration
        interpolator = AccelerateInterpolator()
        addUpdateListener(this@FlashHelper)
        addListener(this@FlashHelper)
    }

    private val flashLauncher: Runnable by lazy {
        Runnable {
            if (pendingTask.isNotEmpty()) {
                val task = pendingTask.removeFirst()
                flashDrawable.setLocations(task.locations)
                animator.cancel()
                animator.start()
            }
        }
    }

    fun unbind() {
        bindView?.let {
            if (it.background == flashDrawable) {
                it.background = null
            }
            if (it is ImageView && it.drawable == flashDrawable) {
                it.setImageDrawable(null)
            }
        }
    }

    fun bindToBackground(view: View) {
        unbind()
        view.background = flashDrawable
        bindView = view
        init(view.context)
    }

    fun bindToImage(view: ImageView) {
        unbind()
        view.setImageDrawable(flashDrawable)
        bindView = view
        init(view.context)
    }

    fun init(context: Context) {
        flashEnable = context.timerFlashEnable()
        flashDrawable.strokeWidth = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 3F, context.resources.displayMetrics)
        updateInfo(context)
    }

    fun updateInfo(context: Context) {
        if (flashEnable) {
            val colorArray = context.timerFlashColor()
            flashDrawable.setColor(colorArray.values())
        } else {
            animator.cancel()
            pendingTask.clear()
            flashDrawable.clear()
        }
        flashDrawable.notifyDataChange()
    }

    fun postDefault(isHorizontal: Boolean = false) {
        val o = random.nextBoolean()
        postFlash(FlashDrawable.makeLocation(0F, !isHorizontal, o),
            FlashDrawable.makeLocation(1F, !isHorizontal, !o))

    }

    fun postRandom(size: Int) {
        val tasks = IntArray(size)
        for (i in 0 until size) {
            tasks[i] = FlashDrawable.makeLocation(
                random.nextFloat().range(0F, 1F),
                random.nextBoolean(),
                random.nextBoolean())
        }
        postFlash(*tasks)
    }

    private fun postFlash(vararg locations: Int) {
        pendingTask.add(FlashTask(locations))
        startFlash()
    }

    fun stop() {
        handler.removeCallbacks(flashLauncher)
        pendingTask.clear()
        animator.cancel()
    }

    private fun startFlash() {
        if (animator.isRunning) {
            return
        }
        val delay = min(0, lastFlashTime + minDelay - System.currentTimeMillis())
        handler.postDelayed(flashLauncher, delay)
    }

    private class FlashTask(val locations: IntArray)

    override fun onAnimationUpdate(animation: ValueAnimator?) {
        if (animation == animator) {
            flashDrawable.progress = animator.animatedValue as Float
        }
    }

    override fun onAnimationRepeat(animation: Animator?) {  }

    override fun onAnimationEnd(animation: Animator?) {
        lastFlashTime = System.currentTimeMillis()
        flashDrawable.clear()
        startFlash()
    }

    override fun onAnimationCancel(animation: Animator?) {
        lastFlashTime = -1
    }

    override fun onAnimationStart(animation: Animator?) {}

}