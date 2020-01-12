package com.lollipop.ldream.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.lollipop.ldream.R
import com.lollipop.ldream.util.TimerHelper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_timer.*

class MainActivity : AppCompatActivity() {

    private lateinit var timerHelper: TimerHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        timerHelper = TimerHelper(timerView, notificationGroup, powerView)
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
