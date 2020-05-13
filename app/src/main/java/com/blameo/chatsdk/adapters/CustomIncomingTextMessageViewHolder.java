package com.blameo.chatsdk.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelStore;

import com.blameo.chatsdk.R;
import com.blameo.chatsdk.controllers.UserVMStore;
import com.blameo.chatsdk.controllers.UserViewModel;
import com.blameo.chatsdk.models.Message;
import com.blameo.chatsdk.models.results.UserStatus;
import com.stfalcon.chatkit.messages.MessageHolders;

public class CustomIncomingTextMessageViewHolder
        extends MessageHolders.IncomingTextMessageViewHolder<Message> {

    private View onlineIndicator;
    private UserVMStore store = UserVMStore.getInstance();
    private TextView tvSeenBy;

    public CustomIncomingTextMessageViewHolder(View itemView, Object payload) {
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

        //We can set click listener on view from payload
        final Payload payload = (Payload) this.payload;
        userAvatar.setOnClickListener(view -> {
            if (payload != null && payload.avatarClickListener != null) {
                payload.avatarClickListener.onAvatarClick();
            }
        });

        tvSeenBy.setText(message.getMessageStatus().getSeenBy());

        if(message.getMessageStatus().isShowing())
            tvSeenBy.setVisibility(View.VISIBLE);
        else
            tvSeenBy.setVisibility(View.GONE);
    }

    public static class Payload {
        public OnAvatarClickListener avatarClickListener;
    }

    public interface OnAvatarClickListener {
        void onAvatarClick();
    }
}
