package com.blameo.chatsdk.repositories.local;

public class Constant {


    public static final int DATABASE_VERSION = 1;
    public static final String EMAIL_PATTERN = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";

    // CHANNEL
    public static final String CHANNEL_DB_NAME = "Channel_Local_Repository";
    public static final String CHANNEL_TABLE_NAME = "Channel_Table";
    public static final String CHANNEL_ID = "_ID";
    public static final String CHANNEL_COLUMN_NAME = "Channel_Name";
    public static final String CHANNEL_COLUMN_AVATAR = "Channel_Avatar";
    public static final String CHANNEL_TYPE = "Channel_Type";
    public static final String CHANNEL_COLUMN_UPDATED_AT = "Channel_Updated_At";
    public static final String CHANNEL_COLUMN_CREATED_AT = "Channel_Created_At";
    public static final String CHANNEL_LAST_MESSAGE_ID = "Channel_Last_M_Id";


    // MESSAGE
    public static final String MESSAGE_DB_NAME = "Message_Local_Repository";
    public static final String MESSAGE_TABLE_NAME = "Message_Table";
    public static final String MESSAGE_ID = "_ID";
    public static final String MESSAGE_AUTHOR_ID = "author_id";
    public static final String MESSAGE_CHANNEL_ID = "channel_id";
    public static final String MESSAGE_CONTENT = "content";
    public static final String MESSAGE_TYPE = "type";
    public static final String MESSAGE_UPDATED_AT = "updated_at";
    public static final String MESSAGE_CREATED_AT = "created_at";
    public static final String MESSAGE_SENT_AT = "sent_at";
    public static final String MESSAGE_SEEN_AT = "seen_at";
    public static final String MESSAGE_CUSTOM_DATA = "custom_data";


    // USER
    public static final String USER_DB_NAME = "User_Local_Repository";
    public static final String USER_TABLE_NAME = "User_Table";
    public static final String USER_ID = "_ID";
    public static final String USER_NAME = "user_name";
    public static final String USER_AVATAR = "user_avatar";
    public static final String USER_CONNECTION_STATUS = "content";
    public static final String USER_LAST_ACTIVE_AT = "user_last_active_at";
    public static final String USER_CUSTOM_DATA = "user_custom_data";

    // USER_IN_CHANNEL
    public static final String UIC_DB_NAME = "UIC_Local_Repository";
    public static final String UIC_TABLE_NAME = "UIC_Table";
    public static final String UIC_ID = "_ID";
    public static final String UIC_CHANNEL_ID = "uic_channel_id";
    public static final String UIC_USER_ID = "uic_user_id";


}
