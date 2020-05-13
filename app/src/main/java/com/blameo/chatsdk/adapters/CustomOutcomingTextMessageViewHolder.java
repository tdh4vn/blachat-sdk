package com.blameo.chatsdk.adapters;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.blameo.chatsdk.R;
import com.blameo.chatsdk.models.Message;
import com.stfalcon.chatkit.messages.MessageHolders;

public class CustomOutcomingTextMessageViewHolder
        extends MessageHolders.OutcomingTextMessageViewHolder<Message> {

    private TextView tvSeenBy;

    public CustomOutcomingTextMessageViewHolder(View itemView, Object payload) {
        super(itemView, payload);
        tvSeenBy = itemView.findViewById(R.id.seenBy);
    }

    @Override
    public void onBind(Message message) {
        super.onBind(message);

        time.setText(message.getStatus() + " " + time.getText());


        if(message.getMessageStatus() != null){
            tvSeenBy.setText(message.getMessageStatus().getSeenBy());

            if(message.getMessageStatus().isShowing())
                tvSeenBy.setVisibility(View.VISIBLE);
            else
                tvSeenBy.setVisibility(View.GONE);
        }

    }
}
