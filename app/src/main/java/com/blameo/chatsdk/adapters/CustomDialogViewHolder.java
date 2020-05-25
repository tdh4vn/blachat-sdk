package com.blameo.chatsdk.adapters;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.Observer;

import com.blameo.chatsdk.ChatApplication;
import com.blameo.chatsdk.R;
import com.blameo.chatsdk.controllers.ChannelVMlStore;
import com.blameo.chatsdk.controllers.ConversationViewModel;
import com.blameo.chatsdk.controllers.UserVMStore;
import com.blameo.chatsdk.controllers.UserViewModel;
import com.blameo.chatsdk.models.CustomChannel;
import com.blameo.chatsdk.models.results.UserStatus;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;


public class CustomDialogViewHolder
        extends DialogsListAdapter.DialogViewHolder<CustomChannel> {

    private View onlineIndicator;

    public CustomDialogViewHolder(View itemView) {
        super(itemView);
        onlineIndicator = itemView.findViewById(R.id.onlineIndicator);
    }

    @Override
    public void onBind(CustomChannel channel) {
        super.onBind(channel);
        ConversationViewModel channelVM = ChannelVMlStore.getInstance().getChannelViewModel(channel.getChannel());

        onlineIndicator.setVisibility(View.VISIBLE);

        channelVM.getPartnerId().observeForever(partnerId -> {
            Log.i("ADAPTER", " "+channel.getId() + " "+partnerId);
            UserViewModel userStatus = UserVMStore.getInstance().getUserViewModel(new UserStatus(partnerId, 1));
            userStatus.getStatus().observeForever(status -> {
                if(status){
                    onlineIndicator.setBackgroundResource(R.drawable.shape_bubble_online);
                }else
                    onlineIndicator.setBackgroundResource(R.drawable.shape_bubble_offline);
            });
        });
        channelVM.getChannel_avatar().observeForever(url -> {
            ImageLoader.getInstance().displayImage(url, ivAvatar, ChatApplication.getInstance().getDisplayImageOption());
        });

        channelVM.getChannel_name().observeForever(name -> {
            if(!TextUtils.isEmpty(name))
            tvName.setText(name);
        });

        channelVM.getLast_message().observeForever(message -> {
            if(!TextUtils.isEmpty(message))
                tvLastMessage.setText(message);
        });

        channelVM.getChannel_updated().observeForever(time -> {
            tvDate.setText(time);
        });

//        if (dialog.getUsers().size() > 1) {
//            onlineIndicator.setVisibility(View.GONE);
//        } else {
//            boolean isOnline = dialog.getUsers().get(0).isOnline();

 //           if (isOnline) {
//                onlineIndicator.setBackgroundResource(R.drawable.shape_bubble_online);
 //           } else {
 //               onlineIndicator.setBackgroundResource(R.drawable.shape_bubble_offline);
 //           }
 //       }
    }
}
