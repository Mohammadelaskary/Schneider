package net.gbs.schneider.SignIn

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.gbs.schneider.Base.MainActivity
import net.gbs.schneider.Tools.Tools.NavigateTo
import net.gbs.schneider.R
import net.gbs.schneider.Tools.Tools.hideToolBar
import net.gbs.schneider.databinding.FragmentSplashBinding


class SplashFragment : Fragment() {
    // TODO: Rename and change types of parameters

    lateinit var binding:FragmentSplashBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashBinding.inflate(inflater,container,false)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SplashFragment().apply {

            }
    }

    override fun onResume() {
        super.onResume()
        hideToolBar(activity as MainActivity)
        Handler(Looper.getMainLooper()).postDelayed({
            NavigateTo(binding.root,R.id.action_splashFragment_to_signInFragment)
        }, 2000)
    }
}