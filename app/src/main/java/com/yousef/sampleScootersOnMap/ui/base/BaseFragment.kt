package com.yousef.sampleScootersOnMap.ui.base

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.yousef.sampleScootersOnMap.R
import com.yousef.sampleScootersOnMap.utils.CommonUtils
import com.yousef.sampleScootersOnMap.utils.MyToast
import javax.inject.Inject

abstract class BaseFragment<T : ViewDataBinding, V : BaseViewModel<*>> : Fragment() {
    var baseActivity: BaseActivity<*, *>? = null
        private set
    private var mRootView: View? = null
    var viewDataBinding: T? = null
        private set
    private var mViewModel: V? = null
    private var mProgressDialog: ProgressDialog? = null

    @JvmField
    @Inject
    var utils: CommonUtils? = null


    /**
     * Override for set view model
     *
     * @return view model instance
     */
    val viewModel: V by lazy { ViewModelProvider(this).get(getViewModelClass()) }
    abstract fun getViewModelClass(): Class<V>

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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity<*, *>) {
            val activity = context
            baseActivity = activity
            activity.onFragmentAttached()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = viewModel
        setHasOptionsMenu(false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewDataBinding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        mRootView = viewDataBinding!!.getRoot()
        return mRootView
    }

    override fun onDetach() {
        baseActivity = null
        super.onDetach()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding!!.setVariable(bindingVariable, mViewModel)
        viewDataBinding!!.lifecycleOwner = this
        viewDataBinding!!.executePendingBindings()
    }

    fun handleError(exception: String?) {
        if(exception!!.length >= 23) {
            if (exception.substring(0, 22) == "Unable to resolve host") {
                MyToast.show(activity, getString(R.string.internetError), true)
            } else {
                MyToast.show(activity, if(exception.isNullOrEmpty())
                    "Something went wrong!" else exception, true)
            }
        }else {
            MyToast.show(activity, if(exception.isNullOrEmpty())
                "Something went wrong!" else exception, true)
        }
    }

    interface Callback {
        fun onFragmentAttached()
        fun onFragmentDetached(tag: String?)
    }
}