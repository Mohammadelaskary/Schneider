package net.gbs.schneider.SignIn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import net.gbs.schneider.Base.BaseFragmentWithViewModel
import net.gbs.schneider.Base.MainActivity
import net.gbs.schneider.Model.APIDataFormats.User
import net.gbs.schneider.R
import net.gbs.schneider.SignIn.ChangeSettingsDialog.Companion.refreshUi
import net.gbs.schneider.Tools.LocaleHelper
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Tools.Tools.NavigateTo
import net.gbs.schneider.Tools.Tools.attachButtonsToListener
import net.gbs.schneider.Tools.Tools.changeTitle
import net.gbs.schneider.Tools.Tools.warningDialog
import net.gbs.schneider.databinding.FragmentSignInBinding

class SignInFragment : BaseFragmentWithViewModel<SignInViewModel, FragmentSignInBinding>(),OnClickListener,LocationDialog.OnLocationSaved {

    companion object {
        fun newInstance() = SignInFragment()
        var USER_ID :String = "0"
        var USER_INFO: User? =null
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSignInBinding
        get() = FragmentSignInBinding::inflate
    private lateinit var changeSettingsDialog: ChangeSettingsDialog
    var currentLang = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attachButtonsToListener(this,binding.signIn,binding.location,binding.settings,binding.language)
        changeSettingsDialog = ChangeSettingsDialog(requireContext(),requireActivity())
        locationDialog = LocationDialog(viewModel,this)
        LocaleHelper.onCreate(requireContext())
        currentLang = LocaleHelper.getLanguage(requireContext()).toString()
        observeSignIn()
    }

    private fun observeSignIn() {
        viewModel.signInStatus.observe(requireActivity()){
            when(it.status){
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> loadingDialog.dismiss()
                else -> {
                    warningDialog(requireContext(),it.message)
                    loadingDialog.dismiss()
                }
            }
        }
        viewModel.signInLiveData.observe(requireActivity()){
            USER_ID = it.userId.toString()
            USER_INFO = it
            if (viewModel.getPlantFromLocalStorage()!=null)
                NavigateTo(requireView(),R.id.action_signInFragment_to_mainMainMenuFragment)
            else
                locationDialog.show(childFragmentManager,"Location Dialog")
        }
    }

    override fun onResume() {
        super.onResume()
        changeTitle(getString(R.string.sign_in),activity as MainActivity)
    }
    private lateinit var locationDialog :LocationDialog
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.sign_in ->{
                val userName = binding.userName.editText?.text.toString()
                val password = binding.password.editText?.text.toString()
                if (userName.isNotEmpty()){
                    if (password.isNotEmpty()){
                        viewModel.signIn(userName,password)
                    } else binding.password?.error = getString(R.string.please_enter_password)
                } else binding.userName?.error = getString(R.string.please_enter_user_name)
            }
            R.id.location -> {
                locationDialog.show(parentFragmentManager,"LocationDialog")
            }
            R.id.settings -> changeSettingsDialog.show()
            R.id.language -> {
                if (currentLang == "ar") {
                    context?.let { LocaleHelper.setLocale(it, "en") }
                    (requireActivity() as MainActivity?)?.let { refreshUi(it) }
                } else if (currentLang == "en") {
                    context?.let { LocaleHelper.setLocale(it, "ar") }
                    (activity as MainActivity?)?.let { refreshUi(it) }
                }
                (activity as MainActivity?)?.let { refreshUi(it) }
            }
        }
    }

    override fun onLocationSaved() {
        if (USER_ID != "0"){
            NavigateTo(requireView(),R.id.action_signInFragment_to_mainMainMenuFragment)
        } else {
            locationDialog.dismiss()
            locationDialog.dismiss()
        }
    }
}