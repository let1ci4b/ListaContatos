package com.example.recyclerlistacontatos.models

import androidx.appcompat.app.AppCompatDialogFragment
import android.app.Dialog
import android.os.Bundle
import com.example.recyclerlistacontatos.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
class ConfirmationDialog : AppCompatDialogFragment() {
    companion object {
        const val TAG = "ConfirmationDialog"

        private const val TITLE = "TITLE"
        private const val MESSAGE = "MESSAGE"
        private const val POSITIVE_BUTTON_TITLE = "POSITIVE_BUTTON_TITLE"
        private const val NEGATIVE_BUTTON_TITLE = "NEGATIVE_BUTTON_TITLE"
        private const val NEUTRAL_BUTTON_TITLE = "NEUTRAL_BUTTON_TITLE"
        private const val FORMAT_ARGS = "FORMAT_ARGS"

        fun newInstance(
            title: Int, message: Int, positiveButtonTitle: Int = R.string.button_save, negativeButtonTitle: Int = R.string.button_discard,
            neutralButtonTitle: Int? = null, vararg formatArgs: String
        ): ConfirmationDialog {
            val args = Bundle()

            args.putInt(TITLE, title)
            args.putInt(MESSAGE, message)
            args.putInt(POSITIVE_BUTTON_TITLE, positiveButtonTitle)
            args.putInt(NEGATIVE_BUTTON_TITLE, negativeButtonTitle)
            neutralButtonTitle?.let { args.putInt(NEUTRAL_BUTTON_TITLE, it) }
            args.putStringArray(FORMAT_ARGS, formatArgs)

            return ConfirmationDialog().apply { arguments = args }
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { activity ->
            isCancelable = false

            val title = arguments?.getInt(TITLE)
            val message = arguments?.getInt(MESSAGE)
            val neutralButtonTitle =
                arguments?.getInt(NEUTRAL_BUTTON_TITLE)
            val positiveButtonTitle =
                arguments?.getInt(POSITIVE_BUTTON_TITLE) ?: R.string.button_save
            val negativeButtonTitle =
                arguments?.getInt(NEGATIVE_BUTTON_TITLE) ?: R.string.button_cancel
            val formatArgs = arguments?.getStringArray(FORMAT_ARGS)
            val listener = activity as? ConfirmationDialogListener
            val titleText = title?.let { getString(it) }
            //val neutralButton = neutralButtonTitle?.let { getString(it) }
            val messageText = message?.let { messageRes ->
                if (formatArgs?.isNotEmpty() == true) {
                    getString(messageRes, *formatArgs)
                } else {
                    getString(messageRes)
                }
            }

            if (!titleText.isNullOrBlank() && !messageText.isNullOrBlank() && neutralButtonTitle != 0) {
                neutralButtonTitle?.let {
                    MaterialAlertDialogBuilder(activity)
                        .setTitle(titleText)
                        .setMessage(messageText)
                        .setPositiveButton(positiveButtonTitle) { _, _ ->
                            listener?.onPositiveButtonClicked()
                            dismiss()
                        }
                        .setNegativeButton(negativeButtonTitle) { _, _ ->
                            listener?.onNegativeButtonClicked()
                            dismiss()
                        }
                        .setNeutralButton(it) { _, _ ->
                            listener?.onNeutralButtonClicked()
                            dismiss()
                        }
                        .create()
                }
            } else if (!titleText.isNullOrBlank() && !messageText.isNullOrBlank()) {
                MaterialAlertDialogBuilder(activity)
                    .setTitle(titleText)
                    .setMessage(messageText)
                    .setPositiveButton(positiveButtonTitle) { _, _ ->
                        listener?.onPositiveButtonClicked()
                        dismiss()
                    }
                    .setNegativeButton(negativeButtonTitle) { _, _ ->
                        listener?.onNegativeButtonClicked()
                        dismiss()
                    }
                    .create()
            }
            else throw IllegalArgumentException("Invalid String Resources")
        } ?: throw IllegalArgumentException("Activity cannot be null")
    }

    interface ConfirmationDialogListener {
        fun onPositiveButtonClicked()
        fun onNegativeButtonClicked()
        fun onNeutralButtonClicked()
    }
}