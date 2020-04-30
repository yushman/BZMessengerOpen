package ooo.emessi.messenger.ui.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ooo.emessi.messenger.R
import ooo.emessi.messenger.data.model.dto_model.message.MessageDto

/**
 * A simple [Fragment] subclass.
 */
class BottomMessageActionFragment(
    val messageDto: MessageDto,
    val isEditable: Boolean,
    val listener: (View) -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_bottom_message_action, container, false)
        val tvReply = v.findViewById<LinearLayout>(R.id.tv_reply_message_action)
        val tvForward = v.findViewById<LinearLayout>(R.id.tv_forward_message_action)
        val tvEdit = v.findViewById<LinearLayout>(R.id.tv_edit_message_action)
        val tvCopy = v.findViewById<LinearLayout>(R.id.tv_copy_message_action)
        val tvDelete = v.findViewById<LinearLayout>(R.id.tv_delete_message_action)

        if (!isEditable) tvEdit.visibility = View.GONE
        else View.VISIBLE

        tvReply.setOnClickListener { actionClick(it) }
        tvForward.setOnClickListener { actionClick(it) }
        tvEdit.setOnClickListener { actionClick(it) }
        tvCopy.setOnClickListener { actionClick(it) }
        tvDelete.setOnClickListener { actionClick(it) }

        return v
    }

    private fun actionClick(it: View) {
        it.background = resources.getDrawable(R.drawable.ripple_effect)
        listener.invoke(it)
        dismiss()
    }


}
