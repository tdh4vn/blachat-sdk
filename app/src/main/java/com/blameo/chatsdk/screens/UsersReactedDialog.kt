package com.blameo.chatsdk.screens

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.blameo.chatsdk.R
import com.blameo.chatsdk.adapters.MemberAdapter
import com.blameo.chatsdk.models.CustomMessage
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class UsersReactedDialog : BottomSheetDialogFragment() {

    lateinit var rvUsersSeen: RecyclerView
    lateinit var rvUsersReceived: RecyclerView
    private var channelId = ""
    private var customMessage: CustomMessage? = null
    lateinit var adapterSeen: MemberAdapter
    lateinit var adapterReceived: MemberAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_users_reacted, container, false)
        return v
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun onResume() {
        super.onResume()
        val window: Window? = dialog!!.window
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(arguments != null){
            customMessage = arguments?.getSerializable("messsage") as CustomMessage?
            initView()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val d = super.onCreateDialog(savedInstanceState)
//        d.setOnShowListener { dialog ->
//            val d = dialog as BottomSheetDialog
//            val bottomSheet =
//                dialog.findViewById(android.support.design.R.id.design_bottom_sheet) as FrameLayout?
//            val behaviour: BottomSheetBehavior<*> =
//                BottomSheetBehavior.from(bottomSheet)
//            behaviour.state = BottomSheetBehavior.STATE_EXPANDED
//            behaviour.setBottomSheetCallback(object : BottomSheetCallback() {
//                override fun onStateChanged(
//                    @NonNull bottomSheet: View,
//                    newState: Int
//                ) {
//                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
//                        // Bottom Sheet was dismissed by user! But this is only fired, if dialog is swiped down! Not if touch outside dismissed the dialog or the back button
//                        Toast.makeText(MainApp.get(), "HIDDEN", Toast.LENGTH_SHORT).show()
//                        dismiss()
//                    }
//                }
//
//                override fun onSlide(
//                    @NonNull bottomSheet: View,
//                    slideOffset: Float
//                ) {
//                }
//            })
//        }
        return d

    }

    private fun initView() {

        Log.i("DIALOG", "message "+customMessage?.id + " "+customMessage?.message?.receivedBy?.size
         + " "+customMessage?.message?.seenBy?.size)

//        adapterReceived = MemberAdapter(context!!, customMessage?.message?.receivedBy!!, UserSP.getInstance().id, 1)
//        adapterSeen = MemberAdapter(context!!, customMessage?.message?.seenBy!!, UserSP.getInstance().id, 1)
//        val layoutManager = GridLayoutManager(context, 3)
//        rvUsersReceived.layoutManager = layoutManager
//        rvUsersSeen.layoutManager = layoutManager
//        rvUsersReceived.adapter = adapterReceived
//        rvUsersSeen.adapter = adapterSeen
    }

    companion object{
        fun newInstance(message: CustomMessage): UsersReactedDialog? {
            val f = UsersReactedDialog()
            val args = Bundle()
            args.putSerializable("message", message)
            f.arguments = args
            return f
        }
    }

}