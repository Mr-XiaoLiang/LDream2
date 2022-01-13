package com.lollipop.ldream.service

import android.os.Handler
import android.service.dreams.DreamService
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.flexbox.FlexboxLayout
import com.lollipop.base.findInSelf
import com.lollipop.ldream.R
import com.lollipop.ldream.util.FlashHelper
import com.lollipop.ldream.util.TimerHelper
import com.lollipop.lpreference.util.isPortrait
import com.lollipop.lpreference.util.lifecycleBinding
import com.lollipop.lpreference.util.onEnd
import com.lollipop.lpreference.util.onStart
import java.util.*
import kotlin.math.abs

/**
 * 充电屏保的服务
 * @author Lollipop
 */
class LDreamService : DreamService() {

    private var timerHelper: TimerHelper? = null
    private val flashHelper = FlashHelper()

    private val handler = Handler()

    private val random: Random by lazy {
        Random()
    }

    private val locationChangeTask: Runnable by lazy {
        Runnable {
            doHideAnimation()
            postMoveTask()
        }
    }

    private val notificationFlashTask: Runnable by lazy {
        Runnable {
            val size = timerHelper?.notificationSize ?: 0
            if (size > 0) {
                flashHelper.postRandom(size)
                postFlashTask()
            }
        }
    }

    private val timerView: TextView? by findInSelf()
    private val notificationGroup: FlexboxLayout? by findInSelf()
    private val powerView: TextView? by findInSelf()
    private val backgroundView: ImageView? by findInSelf()
    private val flashView: View? by findInSelf()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setFullscreen()
        // Set the dream layout
        setContentView(R.layout.dream_root)
        initView()
    }

    private fun initView() {
        timerHelper = TimerHelper(
            this,
            { timerView },
            { notificationGroup },
            { powerView },
            { backgroundView }
        )
        flashHelper.bindToBackground(flashView)
        timerHelper?.onBatteryChange {
            flashHelper.postDefault(isPortrait())
        }
        timerHelper?.onNotificationChange {
            flashHelper.postRandom(it)
            postFlashTask()
        }
    }

    override fun onDreamingStarted() {
        super.onDreamingStarted()
        timerHelper?.onStart()
        setFullscreen()
        getTimeBody { view ->
            view.alpha = 0F
            view.visibility = View.INVISIBLE
            view.post {
                changeLocation()
                doShowAnimation()
            }
        }
        postMoveTask()
        postFlashTask()
    }

    private fun setFullscreen() {
        // Exit dream upon user touch
        isInteractive = false
        // Hide system UI
        isFullscreen = true
        ViewCompat.getWindowInsetsController(window.decorView)?.apply {
            hide(WindowInsetsCompat.Type.systemBars())
        }
    }

    private fun doHideAnimation() {
        getTimeBody { timeBodyView ->
            timeBodyView.animate().let { animator ->
                animator.cancel()
                animator.duration = 1000L
                animator.alpha(0F)
                animator.lifecycleBinding {
                    onEnd {
                        changeLocation()
                        doShowAnimation()
                    }
                }
                animator.start()
            }
        }
    }

    private fun doShowAnimation() {
        getTimeBody { timeBodyView ->
            timeBodyView.animate().let { animator ->
                animator.cancel()
                animator.duration = 1000L
                animator.alpha(1F)
                animator.lifecycleBinding {
                    onStart {
                        if (timeBodyView.visibility != View.VISIBLE) {
                            timeBodyView.visibility = View.VISIBLE
                        }
                        removeThis(it)
                    }
                }
                animator.start()
            }
        }
    }

    private fun changeLocation() {
        getTimeBody { timeBodyView ->
            val rootView = timeBodyView.parent as View
            val x = abs(random.nextInt(rootView.width - timeBodyView.width))
            val y = abs(random.nextInt(rootView.height - timeBodyView.height))
            timeBodyView.translationX = x.toFloat()
            timeBodyView.translationY = y.toFloat()
        }
    }

    private fun postMoveTask() {
        handler.postDelayed(locationChangeTask, 1000L * 10)
    }

    private fun postFlashTask() {
        handler.removeCallbacks(notificationFlashTask)
        handler.postDelayed(notificationFlashTask, 1000L * 5)
    }

    private fun getTimeBody(run: (View) -> Unit) {
        findViewById<View>(R.id.timerRoot)?.let {
            run(it)
        }
    }

    override fun onDreamingStopped() {
        super.onDreamingStopped()
        timerHelper?.onStop()
        flashHelper.stop()
        handler.removeCallbacks(locationChangeTask)
        handler.removeCallbacks(notificationFlashTask)
        getTimeBody {
            it.animate().cancel()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timerHelper?.onDestroy()
        flashHelper.unbind()
    }

}
