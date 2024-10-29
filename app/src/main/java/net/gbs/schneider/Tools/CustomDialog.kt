package net.gbs.schneider.Tools

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.graphics.drawable.ColorDrawable
import android.view.View
import net.gbs.schneider.databinding.CustomAlertDialogBinding

class CustomDialog(context: Context, private val message: String, private val image: Int) :
    Dialog(context) {
    lateinit var binding: CustomAlertDialogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CustomAlertDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.message.text = message
        binding.imageView.setImageResource(image)
        binding.ok.setOnClickListener { dismiss() }
    }
}