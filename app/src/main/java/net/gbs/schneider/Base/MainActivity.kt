package net.gbs.schneider.Base

import android.app.Application
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.honeywell.aidc.AidcManager
import com.honeywell.aidc.BarcodeReader
import net.gbs.schneider.R
import net.gbs.schneider.SignIn.ChangeSettingsDialog.Companion.refreshUi
import net.gbs.schneider.SignIn.SignInFragment.Companion.USER_ID
import net.gbs.schneider.Tools.CommunicationData
import net.gbs.schneider.Tools.LocaleHelper

class MainActivity : AppCompatActivity() {
    companion object {
        private var application : Application? =null
        fun setApplication (application: Application){
            Companion.application =application
        }
        var BASE_URL = ""

        var barcodeReader: BarcodeReader? = null
    }

    private var manager: AidcManager? = null
    private lateinit var navController :NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT // Fixed portrait orientation
        setApplication(application)
        BASE_URL = "${application?.let { CommunicationData.getProtocol(it) }}://${
            application?.let {
                CommunicationData.getIpAddress(
                    it
                )
            }
        }:${application?.let { CommunicationData.getPortNumber(it) }}/api/GBSSchneider/"
        LocaleHelper.onCreate(this)
        if (LocaleHelper.getLanguage(this).equals("ar")) {
            window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
        } else {
            window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
        }
        AidcManager.create(this) { aidcManager: AidcManager ->
            manager = aidcManager
            barcodeReader = manager!!.createBarcodeReader()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.sign_out -> {
                USER_ID = "0"
                navController = findNavController(R.id.myNavhostfragment)
                navController.navigateUp()
                navController.navigate(R.id.signInFragment)
            }
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}