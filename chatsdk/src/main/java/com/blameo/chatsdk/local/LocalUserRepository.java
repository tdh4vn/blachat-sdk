package com.blameo.chatsdk.local;

import com.blameo.chatsdk.models.pojos.User;

import java.util.ArrayList;

public interface LocalUserRepository {
    ArrayList<User> getUsersByIds(ArrayList<String> ids);
    ArrayList<User> getAllUsers();
    void addLocalUser(User user);
    int getTotalLocalUsers();
    int updateUser(User user);
    User getUserByID(String id);
    void exportUserDB();
    void deleteUserByID(String uId);
    boolean checkIfUserIsExist(String id);
    void clearAllLocalUsers();
}
