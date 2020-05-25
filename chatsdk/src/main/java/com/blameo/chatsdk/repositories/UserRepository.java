package com.blameo.chatsdk.repositories;

import com.blameo.chatsdk.blachat.BlaPresenceListener;
import com.blameo.chatsdk.models.bla.BlaUser;
import com.blameo.chatsdk.models.entities.User;

import java.io.IOException;
import java.util.List;

public interface UserRepository {
    List<BlaUser> getUsersByIds(List<String> uIds) throws Exception ;

    List<BlaUser> getAllUser() throws IOException;

    BlaUser getUserById(String id);

    List<BlaUser> getUsersPresence() throws Exception;

    void updateOwnPresence() throws Exception;

    List<BlaUser> getAllUsersStates();

    void saveUser(User user);
}
