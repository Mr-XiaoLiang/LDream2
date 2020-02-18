package com.lollipop.ldream.util

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import com.lollipop.ldream.drawer.FlashDrawable
import java.util.*
import kotlin.math.max

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

    private val handler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }

    private val random: Random by lazy {
        Random()
    }

    private val animator = ValueAnimator.ofFloat(-1F, 1F).apply {
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
    }

    fun bindToImage(view: ImageView) {
        unbind()
        view.setImageDrawable(flashDrawable)
        bindView = view
    }

    fun init(context: Context) {
        flashEnable = context.timerFlashEnable()
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
        postFlash(FlashDrawable.makeLocation(0F, isHorizontal),
            FlashDrawable.makeLocation(1F, isHorizontal))
    }

    fun postRandom(size: Int) {
        val tasks = IntArray(size)
        for (i in 0 until size) {
            tasks[i] = FlashDrawable.makeLocation(
                random.nextFloat().range(0F, 1F),
                random.nextBoolean())
        }
        postFlash(*tasks)
    }

    fun postFlash(vararg locations: Int) {
        pendingTask.add(FlashTask(locations))
        startFlash()
    }

    private fun startFlash() {
        if (animator.isRunning) {
            return
        }
        val delay = max(0, System.currentTimeMillis() - lastFlashTime - minDelay)
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