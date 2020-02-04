package com.lollipop.lpreference.dialog

import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lollipop.lpreference.R
import kotlinx.android.synthetic.main.fragment_base_dialog.*

/**
 * @author lollipop
 * @date 2020-02-04 20:42
 * 基础的Dialog
 */
abstract class BaseDialog: BottomSheetDialogFragment() {

    abstract val contextId: Int

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_base_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (contextId != 0) {
            layoutInflater.inflate(contextId, contentGroup)
        }
    }

}