package com.yousef.sampleScootersOnMap.ui.base

import android.annotation.TargetApi
import android.app.ProgressDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import androidx.multidex.MultiDex
import com.yousef.sampleScootersOnMap.MyApplication
import com.yousef.sampleScootersOnMap.R
import com.yousef.sampleScootersOnMap.utils.CommonUtils
import com.yousef.sampleScootersOnMap.utils.MyToast
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import javax.inject.Inject


abstract class BaseActivity<T : ViewDataBinding, V : BaseViewModel<*>> : AppCompatActivity(),
    BaseFragment.Callback{
    private var mProgressDialog: ProgressDialog? = null

    @JvmField
    @Inject
    var utils: CommonUtils? = null
    var viewDataBinding: T? = null
        private set
    private var mViewModel: V? = null
    @JvmField
    var mContext: Context? = null

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    abstract val bindingVariable: Int

    /**
     * @return layout resource id
     */
    @get:LayoutRes
    abstract val layoutId: Int

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    val viewModel: V by lazy { ViewModelProvider(this).get(getViewModelClass()) }
    abstract fun getViewModelClass(): Class<V>

    override fun onFragmentAttached() {}
    override fun onFragmentDetached(tag: String?) {}
    override fun attachBaseContext(newBase: Context) {
        val context = (newBase.applicationContext as MyApplication)
        super.attachBaseContext(ViewPumpContextWrapper.wrap(context))
        MultiDex.install(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ViewPump.init(
            ViewPump.builder()
                .addInterceptor(
                    CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                            .build())
                )
                .build())
        mContext = this@BaseActivity
        performDataBinding()
    }

    fun handleError(exception: String?) {
        if(exception!!.length >= 23) {
            if (exception.substring(0, 22) == "Unable to resolve host") {
                MyToast.show(this, getString(R.string.internetError), true)
            } else {
                MyToast.show(this, if(exception.isNullOrEmpty())
                    "Something went wrong!" else exception, true)
            }
        }else {
            MyToast.show(this, if(exception.isNullOrEmpty())
                "Something went wrong!" else exception, true)
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun requestPermissionsSafely(permissions: Array<String?>?, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions!!, requestCode)
        }
    }

    private fun performDataBinding() {
        viewDataBinding = DataBindingUtil.setContentView(this, layoutId)
        mViewModel = if (mViewModel == null) viewModel else mViewModel
        viewDataBinding!!.setVariable(bindingVariable, mViewModel)
        viewDataBinding!!.executePendingBindings()
    }


}