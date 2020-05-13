package com.blameo.chatsdk.repositories.local;

public class Constant {


    public static final int DATABASE_VERSION = 2;
    public static final String EMAIL_PATTERN = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";


    public static final String DB_FILE_NAME = "BLA_CHAT_DB";

    // CHANNEL
    public static final String CHANNEL_TABLE_NAME = "CHANNELS";
    public static final String CHANNEL_ID = "ID";
    public static final String CHANNEL_NAME = "NAME";
    public static final String CHANNEL_AVATAR = "AVATAR";
    public static final String CHANNEL_TYPE = "TYPE";
    public static final String CHANNEL_UPDATED_AT = "UPDATED_AT";
    public static final String CHANNEL_CREATED_AT = "CREATED_AT";
    public static final String CHANNEL_LAST_MESSAGE_ID = "LAST_MESSAGE_ID";
    public static final String CHANNEL_CUSTOM_DATA = "CUSTOM_DATA";


    // MESSAGE
    public static final String MESSAGE_TABLE_NAME = "MESSAGES";
    public static final String MESSAGE_ID = "ID";
    public static final String MESSAGE_AUTHOR_ID = "AUTHOR_ID";
    public static final String MESSAGE_CHANNEL_ID = "CHANEL_ID";
    public static final String MESSAGE_CONTENT = "CONTENT";
    public static final String MESSAGE_TYPE = "TYPE";
    public static final String MESSAGE_UPDATED_AT = "UPDATED_AT";
    public static final String MESSAGE_CREATED_AT = "CREATED_AT";
    public static final String MESSAGE_SENT_AT = "SENT_AT";
    public static final String MESSAGE_SEEN_AT = "SEEN_AT";
    public static final String MESSAGE_IS_SYSTEM = "IS_SYSTEM";
    public static final String MESSAGE_CUSTOM_DATA = "CUSTOM_DATA";


    // USER
    public static final String USER_TABLE_NAME = "USERS";
    public static final String USER_ID = "ID";
    public static final String USER_NAME = "NAME";
    public static final String USER_AVATAR = "AVATAR";
    public static final String USER_CONNECTION_STATUS = "CONTENT";
    public static final String USER_LAST_ACTIVE_AT = "USER_LAST_ACTIVE";
    public static final String USER_CUSTOM_DATA = "CUSTOM_DATA";

    // USER_IN_CHANNEL
    public static final String UIC_TABLE_NAME = "USER_IN_CHANNEL";
    public static final String UIC_ID = "ID";
    public static final String UIC_CHANNEL_ID = "CHANNEL_ID";
    public static final String UIC_USER_ID = "USER_ID";
    public static final String UIC_LAST_RECEIVE = "LAST_RECEIVE";
    public static final String UIC_LAST_SEEN = "LAST_SEEN";


    //USER REACT MESSAGE
    public static final String REACT_MESSAGE_TABLE_NAME = "REACT_MESSAGE";
    public static final String REACT_USER_ID = "USER_ID";
    public static final String REACT_MESSAGE_ID = "MESSAGE_ID";
    public static final String REACT_TYPE = "TYPE";
    public static final String REACT_DATE = "DATE";


    //SENT MESSAGE STATE
    public static final String SENT_MESSAGE_TABLE_NAME = "SENT_MESSAGE_STATE";
    public static final String SENT_MESSAGE_ID = "MESSAGE_ID";
    public static final String SENT_USER_ID = "MESSAGE_ID";
    public static final String SENT_STATE = "STATE";


}
