package com.blameo.chatsdk.adapters;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.blameo.chatsdk.R;
import com.blameo.chatsdk.controllers.UserVMStore;
import com.blameo.chatsdk.controllers.UserViewModel;
import com.blameo.chatsdk.models.CustomMessage;
import com.blameo.chatsdk.models.results.UserStatus;
import com.stfalcon.chatkit.messages.MessageHolders;

public class CustomIncomingTextMessageViewHolder
        extends MessageHolders.IncomingTextMessageViewHolder<CustomMessage> {

    private View onlineIndicator;
    private UserVMStore store = UserVMStore.getInstance();

    public CustomIncomingTextMessageViewHolder(View itemView, Object payload) {
        super(itemView, payload);
        onlineIndicator = itemView.findViewById(R.id.onlineIndicator);
    }

    @Override
    public void onBind(CustomMessage message) {
        super.onBind(message);

        UserViewModel userVM = store.getUserViewModel(new UserStatus(message.getMyCustomUser().getId(), 1));
        userVM.getStatus().observeForever(it -> {
            if (it) {
                onlineIndicator.setBackgroundResource(R.drawable.shape_bubble_online);
            } else {
                onlineIndicator.setBackgroundResource(R.drawable.shape_bubble_offline);
            }
        });

        //We can set click listener on view from payload
        final Payload payload = (Payload) this.payload;
        userAvatar.setOnClickListener(view -> {
            if (payload != null && payload.avatarClickListener != null) {
                payload.avatarClickListener.onAvatarClick();
            }
        });
    }

    public static class Payload {
        OnAvatarClickListener avatarClickListener;
    }

    public interface OnAvatarClickListener {
        void onAvatarClick();
    }
}
