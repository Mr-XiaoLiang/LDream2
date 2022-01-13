package com.lollipop.lpreference.dialog

import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lollipop.base.findInSelf
import com.lollipop.lpreference.R
import com.lollipop.lpreference.util.isPortrait

/**
 * @author lollipop
 * @date 2020-02-04 20:42
 * 基础的Dialog
 */
abstract class BaseDialog : BottomSheetDialogFragment() {

    companion object {
        fun BottomSheetDialogFragment.show(fragment: Fragment, tag: String) {
            this.show(fragment.childFragmentManager, tag)
        }

        fun BottomSheetDialogFragment.show(context: Context, tag: String) {
            var c = context
            do {
                if (c is FragmentActivity) {
                    this.show(c.supportFragmentManager, tag)
                    return
                }
                if (c is ContextWrapper) {
                    c = c.baseContext
                } else {
                    throw RuntimeException("Need a FragmentActivity context")
                }
            } while (true)
        }
    }

    abstract val contextId: Int

    private val contentGroup: FrameLayout? by findInSelf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_base_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (contextId != 0) {
            layoutInflater.inflate(contextId, contentGroup)
        }
//        view.fitsSystemWindows = true
//        view.setOnApplyWindowInsetsListener { v, insets ->
//            val allPadding = insets.stableInsetBottom + insets.stableInsetTop
//            contentGroup.setPadding(0, 0, 0, allPadding)
//            insets.consumeStableInsets()
//        }
    }

//    override fun setupDialog(dialog: Dialog, style: Int) {
//        super.setupDialog(dialog, style)
//        dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
//    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.let {
            it.requestedOrientation = if (context.isPortrait()) {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            } else {
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

}