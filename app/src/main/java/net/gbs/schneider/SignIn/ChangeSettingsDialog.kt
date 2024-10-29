package net.gbs.schneider.SignIn

import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View

import kotlinx.coroutines.*
import net.gbs.schneider.Base.MainActivity
import net.gbs.schneider.R
import net.gbs.schneider.Tools.CommunicationData
import net.gbs.schneider.Tools.Tools
import net.gbs.schneider.Tools.Tools.loadingProgressDialog
import net.gbs.schneider.databinding.ChangeSettingsDialogLayoutBinding
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class ChangeSettingsDialog(context: Context, activity: Activity) :
    Dialog(context), View.OnClickListener {
    private val application: Application
    private val activity: Activity
    private lateinit var binding: ChangeSettingsDialogLayoutBinding
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChangeSettingsDialogLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        val metrics: DisplayMetrics = context.resources.displayMetrics
        val width: Int = metrics.widthPixels
        val height: Int = metrics.heightPixels
        this.window?.setLayout(6 * width / 7, 4 * height / 7)
        progressDialog = loadingProgressDialog(context)
        when (CommunicationData.getProtocol(application)) {
            "http" -> binding.http.isChecked = true
            "https" -> binding.https.isChecked = true
        }
        binding.ip.editText?.setText(CommunicationData.getIpAddress(application))
        binding.port.editText?.setText(CommunicationData.getPortNumber(application))
        binding.save.setOnClickListener(this)
    }

    private var ipAddress: String? = null
    private var portNum = ""
    private var protocol: String? = null

    init {
        this.application = activity.application
        this.activity = activity
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.save -> {
                val protocolId: Int = binding.protocol.checkedRadioButtonId
                when (protocolId) {
                    R.id.http -> protocol = "http"
                    R.id.https -> protocol = "https"
                }
                ipAddress = binding.ip.editText?.text.toString().trim()
                portNum = binding.port.editText?.text.toString().trim()
                if (!ipAddress!!.isEmpty()) {
                        hasInternetConnection("$protocol://$ipAddress:$portNum/api/GBSSchneider/GetPlantList?UserID=1&DeviceSerialNo=009da5e1413ce5f2&applang=en")
                } else binding.ip.error = application.getString(R.string.please_enter_ip_address)
            }
        }
    }

//    suspend fun hasInternetConnection(newBaseUrl: String) {
////        return Single.fromCallable {
//            var isOnline = false
//            try {
//                val url = URL(newBaseUrl)
//                val urlc = url.openConnection() as HttpURLConnection
//                urlc.setRequestProperty(
//                    "User-Agent",
//                    "Android Application:" + Build.VERSION.SDK_INT
//                )
//                urlc.setRequestProperty("Connection", "close")
//                urlc.connectTimeout = 1000 * 30 // mTimeout is in seconds
//                urlc.connect()
//                if (urlc.responseCode == 200) {
//                    isOnline = true
//                }
//            } catch (e1: IOException) {
//                e1.printStackTrace()
//                isOnline = false
//            }
//            isOnline
//        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
//            .doOnEvent { aBoolean, throwable ->
//                progressDialog.dismiss()
//                if (aBoolean) {
//                    CommunicationData.saveProtocol(application, protocol)
//                    CommunicationData.saveIPAddress(application, ipAddress)
//                    CommunicationData.savePortNum(application, portNum)
//                    showSuccessAlerter(context.getString(R.string.saved_successfully), activity)
//                    refreshUi(activity as MainActivity)
//                } else warningDialog(context, context.getString(R.string.wrong_ip))
//            }
//    }
    fun hasInternetConnection(newBaseUrl: String){
        progressDialog.show()
    Log.d(TAG, "hasInternetConnection: $newBaseUrl")
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val url = URL(newBaseUrl)
                val urlc = url.openConnection() as HttpURLConnection
                urlc.setRequestProperty(
                    "User-Agent",
                    "Android Application:" + Build.VERSION.SDK_INT
                )
                urlc.setRequestProperty("Connection", "close")
                urlc.connectTimeout = 1000 * 30 // mTimeout is in seconds
                urlc.connect()
                print(urlc.responseCode)
                Log.d(TAG, "hasInternetConnection: ${urlc.responseCode}")
                if (urlc.responseCode == 200) {
                    CommunicationData.saveProtocol(application, protocol)
                    CommunicationData.saveIPAddress(application, ipAddress)
                    CommunicationData.savePortNum(application, portNum)

                    withContext(Dispatchers.Main) {
                        dismiss()
//                        successDialog(activity, activity.getString(R.string.saved_successfully))
                        Tools.showSuccessAlerter(activity.getString(R.string.saved_successfully),activity)
                        delay(1000L)
                        refreshUi(activity as MainActivity)
                    }
                }
            } catch (e1: IOException) {
                e1.printStackTrace()
                withContext(Dispatchers.Main) {
//                    warningDialog(context, context.getString(R.string.wrong_ip))
                    dismiss()
                    Tools.showErrorAlerter(activity.getString(R.string.wrong_ip),activity)
                }
            }
            withContext(Dispatchers.Main) {
                progressDialog.dismiss()
            }
        }
    }
    companion object {
        fun refreshUi(activity: MainActivity) {
            val intent = Intent(activity,MainActivity::class.java)
            activity.startActivity(intent)
        }
    }
}