package com.yousef.sampleScootersOnMap.ui.splashFragment

import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.yousef.sampleScootersOnMap.BR
import com.yousef.sampleScootersOnMap.R
import com.yousef.sampleScootersOnMap.databinding.FragmentSplashBinding
import com.yousef.sampleScootersOnMap.ui.base.BaseFragment
import com.yousef.sampleScootersOnMap.utils.Const
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding, SplashViewModel>(), SplashNavigator {
    var mFragmentSplashBinding: FragmentSplashBinding? = null
    
    override val bindingVariable: Int
        get() = BR.viewModel

    override val layoutId: Int
        get() = R.layout.fragment_splash

    override fun getViewModelClass() = SplashViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       viewModel.setNavigator(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFragmentSplashBinding = viewDataBinding
        setAnimation()

        Handler(Looper.getMainLooper()).postDelayed({
            lifecycleScope.launchWhenResumed {
                findNavController().navigate(
                    SplashFragmentDirections.actionSplashFragmentToMapFragment())
            } }, Const.SPLASH_TIME)
    }

    private fun setAnimation() {
        //to prevent animation lag
        mFragmentSplashBinding!!.backgroundOne.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        mFragmentSplashBinding!!.backgroundTwo.setLayerType(View.LAYER_TYPE_HARDWARE, null)

        //define and start the background animation
        val animator = ValueAnimator.ofFloat(1.00f, 0.00f)
        animator.repeatCount = ValueAnimator.INFINITE
        animator.interpolator = LinearInterpolator()
        animator.duration = 7500L
        animator.addUpdateListener { animation: ValueAnimator ->
            val progress = animation.animatedValue as Float
            val height = mFragmentSplashBinding!!.backgroundOne.height.toFloat()
            val translationY = height * progress
            mFragmentSplashBinding!!.backgroundOne.translationY = translationY
            mFragmentSplashBinding!!.backgroundTwo.translationY = translationY - height
        }
        animator.start()
    }

    companion object {
        fun newInstance(): SplashFragment {
            val args = Bundle()
            val fragment = SplashFragment()
            fragment.arguments = args
            return fragment
        }
    }
}