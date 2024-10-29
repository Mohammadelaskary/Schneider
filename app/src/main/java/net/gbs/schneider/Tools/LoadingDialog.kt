package net.gbs.schneider.Tools

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import net.gbs.schneider.R

class LoadingDialog(private val context: Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_loading)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
}