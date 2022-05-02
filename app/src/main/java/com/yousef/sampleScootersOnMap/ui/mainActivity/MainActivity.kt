package com.yousef.sampleScootersOnMap.ui.mainActivity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.yousef.sampleScootersOnMap.BR
import com.yousef.sampleScootersOnMap.R
import com.yousef.sampleScootersOnMap.databinding.ActivityMainBinding
import com.yousef.sampleScootersOnMap.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(), MainNavigator{
    private var mActivityMainBinding: ActivityMainBinding? = null

    override val bindingVariable: Int
        get() = BR.viewModel

    override val layoutId: Int
        get() = R.layout.activity_main

    override fun getViewModelClass() = MainViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setNavigator(this)
        mActivityMainBinding = viewDataBinding
    }

    companion object {
        fun newIntent(context: Context?): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}
