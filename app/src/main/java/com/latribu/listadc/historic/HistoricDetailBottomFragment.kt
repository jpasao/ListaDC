package com.latribu.listadc.historic

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.latribu.listadc.R
import com.latribu.listadc.common.models.Historic
import com.latribu.listadc.databinding.HistoricDetailModalBinding

class HistoricDetailBottomFragment(val item: Historic) : BottomSheetDialogFragment() {

    private lateinit var binding: HistoricDetailModalBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = HistoricDetailModalBinding.inflate(inflater, container, false)
        val sentText = if (item.firebaseSent == 1)
            getString(R.string.historic_notification_sent)
        else
            getString(R.string.historic_notification_not_sent)
        binding.messageText.text = sentText
        binding.ipText.text = getString(R.string.historic_ip_sender, item.remoteAddr)
        binding.originalData.isVisible = item.originalData.isNullOrEmpty() == false
        binding.originalText.text = getString(R.string.historic_original_data, item.originalData)
        binding.headerText.text = getString(R.string.historic_modal_title, item.itemName)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog?.setOnShowListener { it ->
            val d = it as BottomSheetDialog
            val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return super.onCreateDialog(savedInstanceState)
    }

    companion object {
        const val TAG = "ModalBottomSheetDialog"
    }
}