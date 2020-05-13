package com.blameo.chatsdk.adapters;

import android.view.View;
import android.widget.TextView;

import com.blameo.chatsdk.R;
import com.blameo.chatsdk.controllers.UserVMStore;
import com.blameo.chatsdk.controllers.UserViewModel;
import com.blameo.chatsdk.models.Message;
import com.blameo.chatsdk.models.results.UserStatus;
import com.stfalcon.chatkit.messages.MessageHolders;

/*
 * Created by troy379 on 05.04.17.
 */
public class CustomIncomingImageMessageViewHolder
        extends MessageHolders.IncomingImageMessageViewHolder<Message> {

    private View onlineIndicator;
    private UserVMStore store = UserVMStore.getInstance();
    private TextView tvSeenBy;

    public CustomIncomingImageMessageViewHolder(View itemView, Object payload) {
        super(itemView, payload);
        onlineIndicator = itemView.findViewById(R.id.onlineIndicator);
        tvSeenBy = itemView.findViewById(R.id.seenBy);
    }

    @Override
    public void onBind(Message message) {
        super.onBind(message);

        UserViewModel userVM = store.getUserViewModel(new UserStatus(message.getMyUser().getId(), 1));
        userVM.getStatus().observeForever(it -> {
            if (it) {
                onlineIndicator.setBackgroundResource(R.drawable.shape_bubble_online);
            } else {
                onlineIndicator.setBackgroundResource(R.drawable.shape_bubble_offline);
            }
        });

        if(message.getMessageStatus() != null){

            tvSeenBy.setText(message.getMessageStatus().getSeenBy());

            if(message.getMessageStatus().isShowing())
                tvSeenBy.setVisibility(View.VISIBLE);
            else
                tvSeenBy.setVisibility(View.GONE);
        }



    }
}