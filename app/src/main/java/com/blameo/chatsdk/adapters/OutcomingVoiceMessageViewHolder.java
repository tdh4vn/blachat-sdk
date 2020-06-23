package com.blameo.chatsdk.adapters;

import android.view.View;
import android.widget.TextView;

import com.blameo.chatsdk.R;
import com.blameo.chatsdk.models.CustomMessage;
import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.utils.DateFormatter;

/*
 * Created by troy379 on 05.04.17.
 */
public class OutcomingVoiceMessageViewHolder
        extends MessageHolders.OutcomingTextMessageViewHolder<CustomMessage> {

    private TextView tvTime;
    private TextView tvContent;

    public OutcomingVoiceMessageViewHolder(View itemView, Object payload) {
        super(itemView, payload);
        tvTime = (TextView) itemView.findViewById(R.id.time);
        tvContent = (TextView) itemView.findViewById(R.id.content);
    }

    @Override
    public void onBind(CustomMessage message) {
        super.onBind(message);
        tvContent.setText(message.getText());
        tvTime.setText(DateFormatter.format(message.getCreatedAt(), DateFormatter.Template.TIME));
    }
}
