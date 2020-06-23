package com.blameo.chatsdk.adapters;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.blameo.chatsdk.R;
import com.blameo.chatsdk.models.CustomMessage;
import com.stfalcon.chatkit.messages.MessageHolders;

public class CustomOutcomingTextMessageViewHolder
        extends MessageHolders.OutcomingTextMessageViewHolder<CustomMessage> {

    private TextView tvSeenBy;

    public CustomOutcomingTextMessageViewHolder(View itemView, Object payload) {
        super(itemView, payload);
        tvSeenBy = itemView.findViewById(R.id.seenBy);
    }

    @Override
    public void onBind(CustomMessage message) {
        super.onBind(message);

        time.setText(message.getStatus() + " " + time.getText());

        if(message.getMessageStatus() != null){
            if(!TextUtils.isEmpty(message.getMessageStatus().getSeenBy()))
                tvSeenBy.setText(message.getMessageStatus().getSeenBy());
            else
                tvSeenBy.setText(message.getMessageStatus().getReceivedBy());

            if(message.getMessageStatus().isShowing())
                tvSeenBy.setVisibility(View.VISIBLE);
            else
                tvSeenBy.setVisibility(View.GONE);
        }else{
            tvSeenBy.setVisibility(View.GONE);
        }

    }
}
