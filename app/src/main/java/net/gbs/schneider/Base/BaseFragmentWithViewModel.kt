package net.gbs.schneider.Base

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import net.gbs.schneider.Tools.LoadingDialog
import net.gbs.schneider.ViewModelFactories.BaseViewModelFactory
import java.lang.reflect.ParameterizedType

abstract class BaseFragmentWithViewModel<VM:ViewModel,VB:ViewBinding> : Fragment() {
    lateinit var loadingDialog: LoadingDialog
    lateinit var viewModel:VM
    lateinit var binding: VB
    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = bindingInflater(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingDialog = LoadingDialog(requireContext())
        loadingDialog.setCancelable(false)
        val parameterizedType = javaClass.genericSuperclass as? ParameterizedType

        // now get first actual class, which is the class of VM (ProfileVM in this case)
        @Suppress("UNCHECKED_CAST")
        val vmClass = parameterizedType?.actualTypeArguments?.getOrNull(0) as? Class<VM>?
        if(vmClass != null)
            viewModel = ViewModelProvider(this,BaseViewModelFactory(requireActivity().application!!,requireActivity()))[vmClass]
        else
            Log.i("BaseFragment", "could not find VM class for $this")
    }
}