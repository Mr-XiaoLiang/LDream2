package com.lollipop.ldream.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.ldream.R
import com.lollipop.ldream.databinding.ActivityPrivacyAgreementBinding
import com.lollipop.ldream.databinding.ItemPrivacyAgreementBinding
import com.lollipop.ldream.util.bind
import com.lollipop.ldream.util.isAgreePrivacyAgreement
import com.lollipop.ldream.util.lazyBind
import com.lollipop.privacy.PrivacyAgreementAdapter
import com.lollipop.privacy.PrivacyAgreementHolder
import com.lollipop.privacy.PrivacyAgreementItem

class PrivacyAgreementActivity : AppCompatActivity() {

    companion object {

        private const val RESULT_AGREE = "RESULT_AGREE"

        private fun getResult(intent: Intent?): Boolean {
            return intent?.getBooleanExtra(RESULT_AGREE, false) ?: false
        }
    }

    private val binding: ActivityPrivacyAgreementBinding by lazyBind()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.recyclerView.adapter = PrivacyAgreementAdapter(getPrivacyAgreement()) {
            Holder(it.bind(false))
        }

        binding.closeBtn.setOnClickListener {
            isAgreePrivacyAgreement = false
            updateResult(false)
            onBackPressedDispatcher.onBackPressed()
        }

        binding.agreeBtn.setOnClickListener {
            isAgreePrivacyAgreement = true
            updateResult(true)
            onBackPressedDispatcher.onBackPressed()
        }

        val isAgree = isAgreePrivacyAgreement
        binding.bottomActionBar.isVisible = !isAgree
        binding.backButton.isVisible = isAgree

        updateResult(isAgree)
    }


    private fun updateResult(agree: Boolean) {
        setResult(RESULT_OK, Intent().putExtra(RESULT_AGREE, agree))
    }

    private fun getPrivacyAgreement(): List<PrivacyAgreementItem> {
        return listOf(
            PrivacyAgreementItem(
                R.string.privacy_agreement_label1,
                R.string.privacy_agreement_content1
            ),
            PrivacyAgreementItem(
                R.string.privacy_agreement_label2,
                R.string.privacy_agreement_content2
            ),
            PrivacyAgreementItem(
                R.string.privacy_agreement_label3,
                R.string.privacy_agreement_content3
            ),
            PrivacyAgreementItem(
                R.string.privacy_agreement_label4,
                R.string.privacy_agreement_content4
            ),
            PrivacyAgreementItem(
                R.string.privacy_agreement_label5,
                R.string.privacy_agreement_content5
            ),
            PrivacyAgreementItem(
                R.string.privacy_agreement_label6,
                R.string.privacy_agreement_content6
            ),
        )
    }


    private class Holder(
        private val binding: ItemPrivacyAgreementBinding,
    ) : PrivacyAgreementHolder(binding.root) {
        override fun getLabelView(): TextView {
            return binding.agreementLabelView
        }

        override fun getContentView(): TextView {
            return binding.agreementContentView
        }
    }

    class ResultContract : ActivityResultContract<Any?, Boolean?>() {

        override fun createIntent(context: Context, input: Any?): Intent {
            return Intent(context, PrivacyAgreementActivity::class.java)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Boolean? {
            intent ?: return null
            if (resultCode != Activity.RESULT_OK) {
                return null
            }
            return getResult(intent)
        }

    }
}