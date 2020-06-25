package com.blameo.chatsdk.controllers;

import java.io.IOException;

public interface UserController {
    void updateFCMToken(String imei, String fcmToken) throws IOException;

    void deleteFCMToken(String imei) throws IOException;

}
