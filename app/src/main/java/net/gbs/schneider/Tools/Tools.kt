package net.gbs.schneider.Tools

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.View.VISIBLE
import android.view.animation.AlphaAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.tapadoo.alerter.Alerter
import net.gbs.schneider.Base.MainActivity
import net.gbs.schneider.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object Tools {
    fun containsOnlyDigits(s: String): Boolean {
        return s.matches(Regex("\\d+"))
    }

    fun showSuccessAlerter(message: String, activity: Activity) {
        Alerter.create(activity).setText(message)
            .setIcon(R.drawable.ic_done)
            .setBackgroundColorInt(activity.resources.getColor(R.color.alerter_success_color))
            .setDuration(1000)
            .setTextAppearance(R.style.alerter_text_appearance)
            .setEnterAnimation(com.tapadoo.alerter.R.anim.alerter_slide_in_from_top)
            .setExitAnimation(com.tapadoo.alerter.R.anim.alerter_slide_out_to_top)
            .show()
    }

    fun showErrorAlerter(message: String, activity: Activity) {
        Alerter.create(activity).setText(message)
            .setIcon(com.google.android.material.R.drawable.mtrl_ic_error)
            .setBackgroundColorInt(activity.resources.getColor(R.color.alerter_error_color))
            .setDuration(1000)
            .setTextAppearance(R.style.alerter_text_appearance)
            .setEnterAnimation(com.tapadoo.alerter.R.anim.alerter_slide_in_from_top)
            .setExitAnimation(com.tapadoo.alerter.R.anim.alerter_slide_out_to_top)
            .show()
    }

    fun loadingProgressDialog(context: Context): ProgressDialog {
        val progressDialog = ProgressDialog(context)
        progressDialog.setCancelable(false)
        progressDialog.setMessage(context.getString(R.string.loading_3dots))
        return progressDialog
    }

    fun hideToolBar(mainActivity: AppCompatActivity) {
        mainActivity.supportActionBar!!.hide()
    }

    fun showToolBar(mainActivity: AppCompatActivity) {
        mainActivity.supportActionBar!!.show()
    }


    fun changeTitle(mainTitle: String?, mainActivity: MainActivity) {
        mainActivity.supportActionBar!!.title = mainTitle
    }

    fun showErrorTextView(errorMessage: String, textView: TextView) {
        textView.visibility = VISIBLE
        textView.text       = errorMessage
    }

    fun warningDialog(context: Context, message: String) {
        CustomDialog(context, message, R.drawable.ic_warning_alert).show()
    }

    fun errorDialog(context: Context?, message: String?) {
        CustomDialog(context!!, message!!, R.drawable.baseline_error_outline_24).show()
    }

    fun successDialog(context: Context, message: String) {
        CustomDialog(context, message, R.drawable.ic_done).show()
    }

    fun back(fragment: Fragment?) {
        val navController = NavHostFragment.findNavController(fragment!!)
        navController.popBackStack()
    }

    fun clearInputLayoutError(vararg inputLayouts: TextInputLayout) {
        for (inputLayout in inputLayouts) {
            inputLayout.editText!!.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    inputLayout.error = null
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    inputLayout.error = null
                }

                override fun afterTextChanged(s: Editable) {
                    inputLayout.error = null
                }
            })
        }
    }

    fun attachButtonsToListener(
        listener: View.OnClickListener?,
        vararg materialButtons: MaterialButton
    ) {
        for (button in materialButtons) {
            button.setOnClickListener(listener)
        }
    }

    fun hideKeyboard(activity: Activity?) {
        if (activity != null) {
            val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            //Find the currently focused view, so we can grab the correct window token from it.
            var view = activity.currentFocus
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = View(activity)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun showKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    fun activateItem(itemView: View) {
        val alphaAnimation = AlphaAnimation(0.9f, 1.0f)
        alphaAnimation.fillAfter = true
        alphaAnimation.duration = 50 //duration in millisecond
        itemView.startAnimation(alphaAnimation)
    }

    fun deactivateItem(itemView: View) {
        val alphaAnimation = AlphaAnimation(0.9f, 0.4f)
        alphaAnimation.fillAfter = true
        alphaAnimation.duration = 50 //duration in millisecond
        itemView.startAnimation(alphaAnimation)
    }

    fun getRemainingTime(expectedSignOut: String?): Long {
        val currentDate = Calendar.getInstance().time
        //        expectedSignOut+=":00";
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
        var d: Date? = null
        try {
            d = sdf.parse(expectedSignOut)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        Log.d("dateCurrent", sdf.format(currentDate))
        Log.d("dateSignOut", expectedSignOut!!)
        //        assert d != null;
        return d!!.time - currentDate.time
    }

    //    public static void startRemainingTimeTimer(long remainingTime, TextView remainingTimeTv){
    //        if (remainingTime>0) {
    //            Log.d("dateRemaining",remainingTime+"");
    //            new CountDownTimer(remainingTime, 1000) {
    //                @Override
    //                public void onTick(long millisUntilFinished) {
    //                    remainingTimeTv.setText(convertToTimeFormat(millisUntilFinished));
    //                }
    //
    //                @Override
    //                public void onFinish() {
    //                    remainingTimeTv.setText(R.string.operation_finished);
    //                }
    //            }.start();
    //        } else
    //            remainingTimeTv.setText(R.string.operation_finished);
    //    }
    fun convertToTimeFormat(millisUntilFinished: Long): String {
        var millisUntilFinished = millisUntilFinished
        val days = millisUntilFinished / (24 * 60 * 60 * 1000)
        millisUntilFinished -= days * (24 * 60 * 60 * 1000)
        val hours = millisUntilFinished / (60 * 60 * 1000)
        millisUntilFinished -= hours * (60 * 60 * 1000)
        val minutes = millisUntilFinished / (60 * 1000)
        millisUntilFinished -= minutes * (60 * 1000)
        val seconds = millisUntilFinished / 1000
        val daysString = String.format("%02d", days)
        val hoursString = String.format("%02d", hours)
        val minsString = String.format("%02d", minutes)
        val secondsString = String.format("%02d", seconds)
        return "$hoursString:$minsString:$secondsString"
    }

    fun getEditTextText(editText: EditText): String {
        return editText.text.toString().trim { it <= ' ' }
    }

    //    public static void showSuccessAlerter(String message,Activity activity){
    //       Alerter.create(activity).setText(message)
    //                .setIcon(R.drawable.ic_done)
    //                .setBackgroundColorInt(activity.getResources().getColor(R.color.alerter_success_color))
    //                .setDuration(1000)
    //                .setTextAppearance(R.style.alerter_text_appearance)
    //                .setEnterAnimation(R.anim.alerter_slide_in_from_top)
    //                .setExitAnimation(R.anim.alerter_slide_out_to_top)
    //                .show();
    //    }
    val currentDate: String
        get() = SimpleDateFormat("MM/dd/yyyy").format(Calendar.getInstance().time)
    val currentDate2: String
        get() = SimpleDateFormat("MM-dd-yyyy").format(Calendar.getInstance().time)

    fun <T> compareList(list1: List<T>?, list2: List<T>?): Boolean {
        return HashSet(list1) == HashSet(list2)
    } //    public static List<DefectsPerQty> getDefectsPerQtyList_Painting(List<PaintingDefect> paintingDefects) {




    fun monthNumToAbbreviatedName(monthNum:Int):String {
        when(monthNum){
            1 -> return "JAN"
            2 -> return "FEB"
            3 -> return "MAR"
            4 -> return "APR"
            5 -> return "MAY"
            6 -> return "JUN"
            7 -> return "JUL"
            8 -> return "AUG"
            9 -> return "SEP"
            10 -> return "OCT"
            11 -> return "NOV"
            12 -> return "DEC"
            else -> return "Not supported"
        }
    }

    fun NavigateTo(v:View,dest:Int){
        Navigation.findNavController(v).navigate(dest)
    }
    fun NavigateTo(v:View,dest:Int,bundle: Bundle){
        Navigation.findNavController(v).navigate(dest,bundle)
    }

    fun errorState(workOrdersList: RecyclerView, errorTextView: TextView, errorMessage:String) {
        errorTextView.text = errorMessage
        workOrdersList.visibility = View.GONE
        errorTextView.visibility = VISIBLE
    }

    fun loadingState(workOrdersList: RecyclerView, errorMessage: TextView) {
        workOrdersList.visibility = View.GONE
        errorMessage.visibility = View.GONE
    }
    fun successState(workOrdersList: RecyclerView, errorMessage: TextView) {
        workOrdersList.visibility = VISIBLE
        errorMessage.visibility = View.GONE
    }
}