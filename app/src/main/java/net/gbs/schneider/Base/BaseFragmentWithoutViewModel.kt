package net.gbs.schneider.Base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import net.gbs.schneider.Tools.LoadingDialog

abstract class BaseFragmentWithoutViewModel <VB:ViewBinding> : Fragment() {
    lateinit var loadingDialog: LoadingDialog
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadingDialog = LoadingDialog(requireContext())
    }
}