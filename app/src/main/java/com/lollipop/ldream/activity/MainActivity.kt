package com.lollipop.ldream.activity

import android.content.BroadcastReceiver
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.flexbox.FlexboxLayout
import com.lollipop.base.findInSelf
import com.lollipop.ldream.databinding.ActivityMainBinding
import com.lollipop.ldream.util.*
import com.lollipop.lpreference.PreferenceHelper
import com.lollipop.lpreference.util.isPortrait
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazyBind()

    private lateinit var timerHelper: TimerHelper
    private lateinit var preferenceHelper: PreferenceHelper

    private val flashHelper = FlashHelper()

    private val receiverList = ArrayList<BroadcastReceiver>()

    private val random: Random by lazy { Random() }

    private val timerView: TextView? by findInSelf()
    private val notificationGroup: FlexboxLayout? by findInSelf()
    private val powerView: TextView? by findInSelf()
    private val backgroundView: ImageView? by findInSelf()
    private val flashView: View? by findInSelf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        timerHelper = TimerHelper(
            this,
            { timerView },
            { notificationGroup },
            { powerView },
            { backgroundView }
        )
        preferenceHelper = PreferenceHelper(binding.preferenceList)
        binding.preferenceList.post {
            initPreference()
        }

        flashHelper.bindToBackground(flashView)
        val testFlash = LDreamPreference.registerFlashTest(this) {
            if (random.nextBoolean()) {
                flashHelper.postDefault(isPortrait())
            } else {
                flashHelper.postRandom(random.nextInt(5) + 1)
            }
        }
        receiverList.add(testFlash)
    }

    private fun initPreference() {
        preferenceHelper.build(LDreamPreference.getPreferenceList(this))
        preferenceHelper.onPreferenceChange {
            onPreferenceChange(it.key)
        }
    }

    private fun onPreferenceChange(key: String) {
        when (key) {
            LDreamPreference.KEY_KEYWORD -> {
                timerHelper.specialKeyword = timerKeyWord()
                timerHelper.notifyUpdateText()
            }
            LDreamPreference.KEY_PRIMARY_COLOR -> {
                timerHelper.specialKeywordColor = timerPrimaryColor()
                timerHelper.notifyUpdateText()
            }
            LDreamPreference.KEY_SECONDARY_COLOR -> {
                timerHelper.secondaryTextColor = timerSecondaryColor()
                timerHelper.notifyUpdateText()
            }
            LDreamPreference.KEY_BACKGROUND -> {
                timerHelper.backgroundUri = timerBackgroundUri()
                timerHelper.notifyUpdateBackground()
            }
            LDreamPreference.KEY_FLASH_ENABLE -> {
                flashHelper.flashEnable = timerFlashEnable()
                flashHelper.updateInfo(this)
            }
            LDreamPreference.KEY_FLASH_COLOR -> {
                flashHelper.updateInfo(this)
            }
            LDreamPreference.KEY_TINT_ENABLE -> {
                timerHelper.isTintIcon = timerTintEnable()
                timerHelper.notifyUpdateIcon()
            }
            LDreamPreference.KEY_TINT_COLOR -> {
                timerHelper.iconTintColor = timerTintColor()
                timerHelper.notifyUpdateIcon()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        timerHelper.onStart()
    }

    override fun onStop() {
        super.onStop()
        timerHelper.onStop()
        flashHelper.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        timerHelper.onDestroy()
        receiverList.forEach {
            unregisterReceiver(it)
        }
        receiverList.clear()
    }

}
