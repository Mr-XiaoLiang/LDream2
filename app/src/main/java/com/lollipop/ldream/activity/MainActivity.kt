package com.lollipop.ldream.activity

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.lollipop.ldream.R
import com.lollipop.ldream.util.TimerHelper
import com.lollipop.lpreference.PreferenceHelper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_timer.*

class MainActivity : AppCompatActivity() {

    private lateinit var timerHelper: TimerHelper
    private lateinit var preferenceHelper: PreferenceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        timerHelper = TimerHelper(timerView, notificationGroup, powerView)
        preferenceHelper = PreferenceHelper(preferenceList)
        preferenceList.post {
            initPreference()
        }
    }

    private fun initPreference() {
        preferenceHelper.build {
            addItem(
                number("keyWorld") {
                    title = "关键字"
                    summary = "设置关键字"
                },
                action("action", Intent(Settings.ACTION_SETTINGS)) {
                    title = "跳转到设置"
                    summary = "跳转到设置页面"
                },
                switch("switch") {
                    title = "开关"
                    summary = "打开或者关闭开关"
                    summaryTrue = "你已经打开了开关"
                    summaryFalse = "你已经关闭了开关"
                }
            )
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
    }

    override fun onDestroy() {
        super.onDestroy()
        timerHelper.onDestroy()
    }

}
