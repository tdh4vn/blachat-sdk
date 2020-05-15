package com.blameo.chatsdk.adapters;

import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.blameo.chatsdk.R;
import com.blameo.chatsdk.models.CustomMessage;
import com.stfalcon.chatkit.messages.MessageHolders;

/*
 * Created by troy379 on 05.04.17.
 */
public class CustomOutcomingImageMessageViewHolder
        extends MessageHolders.OutcomingImageMessageViewHolder<CustomMessage> {

    private TextView tvSeenBy;

    public CustomOutcomingImageMessageViewHolder(View itemView, Object payload) {
        super(itemView, payload);
        tvSeenBy = itemView.findViewById(R.id.seenBy);
    }

    @Override
    public void onBind(CustomMessage message) {
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

    //Override this method to have ability to pass custom data in ImageLoader for loading image(not avatar).
    @Override
    protected Object getPayloadForImageLoader(CustomMessage message) {
        //For example you can pass size of placeholder before loading
        return new Pair<>(100, 100);
    }
}