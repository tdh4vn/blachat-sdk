package com.blameo.chatsdk.models.entities;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.blameo.chatsdk.repositories.local.Constant;

import java.util.ArrayList;
import java.util.List;

public class MessageWithUserReact {
    @Embedded
    public Message message;
    @Relation(
            entity = UserReactMessage.class,
            entityColumn = Constant.REACT_MESSAGE_ID,
            parentColumn = Constant.MESSAGE_ID
    )
    public List<UserReactMessage> userReactMessages;

    @Relation(
            entity = User.class,
            entityColumn = Constant.USER_ID,
            parentColumn = Constant.MESSAGE_ID,
            associateBy = @Junction(
                    value = UserReactMessage.class,
                    parentColumn = Constant.REACT_MESSAGE_ID,
                    entityColumn = Constant.REACT_USER_ID
            )
    )
    public List<User> users;




//    public MessageWithUserReact messageWithUserReact;
//
//    @Relation(
//            entity = User.class,
//            entityColumn = Constant.USER_ID,
//            parentColumn = Constant.REACT_USER_ID
//    )
//    public List<User> user;
//
//    @Relation(
//            entity = Message.class,
//            entityColumn = Constant.MESSAGE_ID,
//            parentColumn = Constant.REACT_MESSAGE_ID
//    )
//    public Message messages;


    //...
//    @Relation(
//            parentColumn = Constant.MESSAGE_ID,
//            entityColumn = Constant.USER_ID,
//            associateBy = @Junction(
//                    value = UserReactMessage.class,
//                    parentColumn = Constant.REACT_MESSAGE_ID,
//                    entityColumn = Constant.REACT_USER_ID
//            )
//    )
//    public List<User> users;
    //...//

//    @Relation(
//            associateBy = @Junction(UserReactMessage.class)
//    )
//    public List<UserReactMessage> userReactMessages;
}
