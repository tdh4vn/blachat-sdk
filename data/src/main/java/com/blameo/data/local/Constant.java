package com.blameo.data.local;

public class Constant {

    public static final String CHANNEL_TAG = "SQLite";
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Channel_Local_Repository";
    public static final String CHANNEL_TABLE_NAME = "Channel_Table";
    public static final String CHANNEL_ID = "_ID";
    public static final String CHANNEL_COLUMN_NAME = "Channel_Name";
    public static final String CHANNEL_COLUMN_AVATAR = "Channel_Avatar";
    public static final String CHANNEL_TYPE = "Channel_Type";
    public static final String CHANNEL_COLUMN_UPDATED_AT = "Channel_Updated_At";
    public static final String CHANNEL_COLUMN_CREATED_AT = "Channel_Created_At";
    public static final String CHANNEL_LAST_MESSAGE_ID = "Channel_Last_M_Id";




//    public static final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    public static final String EMAIL_PATTERN = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";


    // pending intent
    public static final String DATABASE_PENDING_NAME = "Agency_Pending_Manager";
    public static final String PENDING_TABLE_NAME = "Agency_Pending_Table";
    public static final String PENDING_COLUMN_ID_ORDER = "Pending_Order_Id";
    public static final String PENDING_COLUMN_TIMES = "Pending_Times";
    public static final String PENDING_COLUMN_IS_CANCELED = "Pending_Is_Canceled";
    public static final String PENDING_ID = "_ID";

}
