package com.lollipop.ldream.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.lollipop.ldream.databinding.ActivityAboutBinding
import com.lollipop.ldream.util.FlashHelper
import com.lollipop.ldream.util.lazyBind
import com.lollipop.ldream.util.openFlashFeatures
import java.util.*

/**
 * 关于
 * @author Lollipop
 */
class AboutActivity : AppCompatActivity() {

    private val binding: ActivityAboutBinding by lazyBind()

    private var tapCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initView()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initView() {
        binding.versionView.text = packageManager.getPackageInfo(packageName, 0).versionName
        val flashHelper = FlashHelper()
        flashHelper.bindToBackground(binding.rootGroup)
        val random = Random()
        binding.logoView.setOnClickListener {
            tapCount++
            if (tapCount == 7) {
                openFlashFeatures()
            }
            when {
                random.nextBoolean() -> {
                    flashHelper.postDefault(random.nextBoolean())
                }
                else -> {
                    flashHelper.postRandom(random.nextInt(20) + 2)
                }
            }
        }

    }

}
